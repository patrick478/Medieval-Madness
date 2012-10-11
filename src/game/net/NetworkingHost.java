package game.net;

import game.net.packets.EnterGamePacket;
import game.net.packets.PingPacket;
import game.net.packets.WelcomePacket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class NetworkingHost extends NetworkMode implements Runnable
{
	private ServerSocketChannel serverChannel;
	private Selector selector;
	private ByteBuffer readBuffer = ByteBuffer.allocate(8192);
	
	private BlockingQueue<ChangeRequest> changes = new LinkedBlockingQueue<ChangeRequest>();
	private Map<SocketChannel, LinkedList<ByteBuffer>> pendingWrites = new HashMap<SocketChannel, LinkedList<ByteBuffer>>();
	
	private Thread mainThread = null;
	private Thread workerThread = null;
	private ServerWorker worker = null;
	
	private int maxPlayers = -1;
	private Map<Integer, ServerClient> clients = new HashMap<Integer, ServerClient>();
	
	@Override
	protected void modeStart()
	{
		Selector socketSelector = null;
		try
		{
			socketSelector = SelectorProvider.provider().openSelector();		
			this.serverChannel = ServerSocketChannel.open();
			this.serverChannel.configureBlocking(false);
			
			this.serverChannel.socket().bind(new InetSocketAddress(14121));
			
			this.serverChannel.register(socketSelector, SelectionKey.OP_ACCEPT);
		}
		catch(Exception ex)
		{
			this.printError("There was an error creating the server.", "Check another program isn't using port 14121.");
		}
		
		this.selector = socketSelector;
		
		this.mainThread = new Thread(this);
		this.mainThread.start();
		
		this.worker = new ServerWorker(this);
		this.workerThread = new Thread(this.worker);
		this.workerThread.start();
	}
	
	@Override
	public void destroy()
	{
		
	}
	
	public void setNumPlayers(int n) {
//		System.out.printf("Number of players changed to %d\n", n);
		this.maxPlayers = n;
	}
	
	public void run()
	{
		if(this.maxPlayers < 1)
		{
			this.printError("There was an error starting the server.", "You need to start the server with at least one player.");
			return;
		}
		
		while(true)
		{
			try
			{
				Iterator<ChangeRequest> ch = this.changes.iterator();
				while(ch.hasNext())
				{
					ChangeRequest change = ch.next();
					switch(change.type)
					{
						case ChangeRequest.CHANGEOPS:
							SelectionKey key = change.socket.keyFor(this.selector);
							key.interestOps(change.ops);
						break;
					}
				}
				this.changes.clear();
				
				// wait for an socket event
				this.selector.select(1000);
				
				Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
				while(selectedKeys.hasNext())
				{
					SelectionKey key = selectedKeys.next();
					selectedKeys.remove();
					
					if(!key.isValid())
						continue;
					
					if(key.isAcceptable())
					{
						ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
						SocketChannel clientSocket = ssc.accept();
						clientSocket.configureBlocking(false);
						
						// generate a correct server client
						int index = this.clients.size();
						int playerIndex = this.getFreePlayerIndex();
						ServerClient client = new ServerClient(playerIndex, clientSocket);
						this.clients.put(index,  client);
						
						clientSocket.register(this.selector, SelectionKey.OP_READ, index);
						
						// begin initial ping testing here
						client.setSyncsRequired(5);
						client.setPredictedLatency(0);
						PingPacket syncPacket = new PingPacket();
						syncPacket.isReply = false;
						syncPacket.predictedLatency = client.getPredictedLatency();
						syncPacket.time = System.currentTimeMillis();
						client.setLastSyncSent(syncPacket.time);
						this.send(clientSocket,  syncPacket.toData().getData());
						client.sentSync();
						
						if(this.numPlayers() == this.maxPlayers)
						{
							// done accepting - close the accept socket.
							this.serverChannel.close();
							
							// notify players of their player indexes and tell them to enter the game
							for(ServerClient c : this.clients.values())
							{
								WelcomePacket wp = new WelcomePacket();
								wp.isReply = false;
								wp.playerIndex = c.getPlayerIndex();
								wp.maxPlayers = this.maxPlayers;
								this.send(c.getSocket(), wp.toData().getData());
								
								EnterGamePacket egp = new EnterGamePacket();
								egp.isReply = false;
								this.send(c.getSocket(), egp.toData().getData());
							}
						}
					}
					else if(key.isReadable())
					{
						SocketChannel sc = (SocketChannel) key.channel();
						this.readBuffer.clear();
						int rx = -1;
						try
						{
							rx = sc.read(this.readBuffer);
						}
						catch(IOException ioex)
						{
							key.cancel();
							sc.close();
						}
						
						if(rx < 0)
						{
							key.channel().close();
							key.cancel();
							continue;
						}
						
						int index = (Integer) key.attachment();
						ServerClient client = this.clients.get(index);
						if(client == null)
						{
							this.printError("Attempting to received data failed", "The client is not setup");
							continue;
						}
						this.worker.processData(this, client, sc, this.readBuffer.array(), rx);
					}
					else if(key.isWritable())
					{
						SocketChannel sc = (SocketChannel) key.channel();
						synchronized(this.pendingWrites)
						{
							Queue<ByteBuffer> writes = this.pendingWrites.get(sc);
							if(writes == null) writes = new LinkedList<ByteBuffer>();
							while(!writes.isEmpty())
							{
								ByteBuffer buf = writes.poll();
								sc.write(buf);
								if(buf.remaining() > 0)
								{
									// basically, this is bad - it means the socket has a full buffer. wait till its writable again, the continue.
									break;
								}
							}
							
							if(writes.isEmpty()) {
								key.interestOps(SelectionKey.OP_READ);
							}
						}
					}
				}
			}
			catch(Exception ex)
			{
				// FIXME: We need useful error printouts
				ex.printStackTrace();
			}
		}
	}
	
	public void send(SocketChannel socket, byte[] data)
	{
		this.changes.add(new ChangeRequest(socket, ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));
		synchronized(this.pendingWrites)
		{
			LinkedList<ByteBuffer> writes = this.pendingWrites.get(socket);
			if(writes == null)
				writes = new LinkedList<ByteBuffer>();
			
			writes.add(ByteBuffer.wrap(data));
			this.pendingWrites.put(socket,  writes);
		}
		
		this.selector.wakeup();
	}
	
	private int numPlayers()
	{
		return this.clients.size();
	}
	
	private int getFreePlayerIndex()
	{
		for(int i = 0; i < this.maxPlayers; i++)
		{
			if(!this.clients.containsKey(i)) return i;
		}
		return -1;
	}
	
	private void printError(String ... msgLines)
	{
		final String line = "--[ERROR]-----------------------------------------------------------------------";
		String msg = "";
		for(String str : msgLines)
		{
			msg = str + "\n";
		}
		System.err.printf("%s\nmsg%s\n",  line, msg, line);
	}
//
//	public void updateOthersOnMovements(ServerClient client) {
//		MovementPacket packet = new MovementPacket(client.getPlayerIndex(), client.getPosition(), client.getVelocity());
//		for(ServerClient sc : this.clients)
//		{
//			if(sc.equals(client))
//				continue;
//			
//			sc.send(packet.toData());
//		}
//	}

}