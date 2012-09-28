package server.net;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.channels.spi.*;
import java.util.*;

import server.Server;
import server.ServerStatus;
import server.session.SessionMngr;

import common.Log;
import common.NetworkLayer;
import common.Packet;
import common.packets.WelcomePacket;

public class ServerLayer extends NetworkLayer implements Runnable {
	
	private InetSocketAddress hostAddress;
	
	public Server parentServer;
	public Log log;
	public boolean ready = false;
	
	private int port;
	
	private ServerSocketChannel listenChannel;
	private Selector selector;
	private Thread serverThread;
	
	private ServerWorker worker;
	private Thread workerThread;
	
	private  ByteBuffer readBuffer = ByteBuffer.allocate(8192);

	private List<ChangeRequest> changeRequests = new LinkedList<ChangeRequest>();
	private HashMap<SocketChannel, List<ByteBuffer>> pendingData = new HashMap<SocketChannel, List<ByteBuffer>>();
	
	public ServerLayer(int port, Server server)
	{
		this.parentServer = server;
		this.port = port;
		this.log = this.parentServer.log;
		this.hostAddress = new InetSocketAddress(this.port);
		try {
			this.selector = this.createSelector();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(this.selector == null) return;
		
		this.ready = true;
	}

	@Override
	public boolean Start() {
		if(!this.ready) return false;
		try {
			this.serverThread = new Thread(this);
			this.serverThread.start();
			
			this.worker = new ServerWorker(this.parentServer);
			this.workerThread = new Thread(this.worker);
			this.workerThread.start();
		} catch (Exception e)
		{
			// TODO: this is a blank catch - fix it fool
		}
		return true;
	}
	
	public void stopAndWait() {
		try {
			synchronized(this.serverThread)
			{
				while(this.serverThread.isAlive())
				{
					this.serverThread.wait(1000);
					this.serverThread.interrupt();
				}
			}
			
			synchronized(this.workerThread)
			{
				while(this.workerThread.isAlive())
				{
					this.workerThread.wait(1000);
					this.workerThread.interrupt();
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		this.parentServer.log.printf("ListenWorker :: starting up\n");
		
		while(!this.parentServer.getStatus().equals(ServerStatus.Stopping)) {
			try {
				// process changes
				synchronized(this.changeRequests) {
					Iterator<ChangeRequest> changes = this.changeRequests.iterator();
					while(changes.hasNext()) {
						ChangeRequest change = changes.next();
						switch(change.type) {
							case ChangeRequest.CHANGEOPS:
								SelectionKey key = change.socket.keyFor(this.selector);
								key.interestOps(change.ops);
								break;
						}
					}
					this.changeRequests.clear();
				}
				
				// wait for an event
				this.selector.select();
				if(Thread.interrupted()) break;
				
				Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
				while(selectedKeys.hasNext())
				{
					SelectionKey key = selectedKeys.next();
					selectedKeys.remove();
					
					if(!key.isValid())
						continue;
					
					if(key.isAcceptable())
						this.accept(key);
					else if(key.isReadable())
						this.read(key);
					else if(key.isWritable())
						this.write(key);
				}
			} catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		this.parentServer.log.printf("ListenWorker :: detected shutdown - stopping\n");
		
	}
	
	private void read(SelectionKey key) throws IOException {
		if(!key.isValid() || !key.isReadable())
			return;
		
		SocketChannel sc;
		try {
			sc = (SocketChannel)key.channel();	
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
		
		this.readBuffer.clear();
		
		int rx =0;
		try {
			rx = sc.read(this.readBuffer);
		} catch(IOException ex) {
			key.cancel();
			sc.close();
			SessionMngr.getInstance().destroySession((String)key.attachment());
			return;
		}
		
		if(rx == -1)
		{
			// clean shutdown
			SessionMngr.getInstance().destroySession((String)key.attachment());
			sc.close();
			key.cancel();
			return;
		}
		
		String id = (String) key.attachment();
//		System.out.printf("Recieved %d bytes\n", rx);
		this.worker.processData(this, sc, this.readBuffer.array(), rx, id);
	}
	
	private void write(SelectionKey key) throws IOException {
		SocketChannel sc = (SocketChannel)key.channel();
		synchronized (this.pendingData) {
			List<ByteBuffer> queue = this.pendingData.get(sc);
			int tx = 0;
			while(!queue.isEmpty()) {
				ByteBuffer buf = queue.get(0);
				tx = sc.write(buf);
//				System.out.printf("Wrote %d bytes\n", tx);
				if(buf.remaining() > 0)
					break;
				queue.remove(0);
			}
			
			if(queue.isEmpty()) {
				key.interestOps(SelectionKey.OP_READ);
			}
		}
	}
	
	private void accept(SelectionKey key) throws IOException {
		ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
		
		
		
		SocketChannel sc = ssc.accept();
		sc.configureBlocking(false);
		
		String sesID = SessionMngr.getInstance().createSession(sc);
		sc.register(this.selector,  SelectionKey.OP_READ, sesID);
		Packet p = new WelcomePacket();
		this.send(sc, p.toData().getData());
		SessionMngr.getInstance().getSession(sesID).setSubstate(1);
		this.log.printf("Accepted new client [SessionID=%s]\n", sesID);
	}
	
	private Selector createSelector() throws IOException {
		Selector socketSelector = SelectorProvider.provider().openSelector();
		
		this.listenChannel = ServerSocketChannel.open();
		this.listenChannel.configureBlocking(false);
		
		try {
		this.listenChannel.socket().bind(this.hostAddress);
		} catch(Exception e) {
			this.log.printf("Unable to bind, port probably already in use\n");
			return null;
		}
		
		this.listenChannel.register(socketSelector, SelectionKey.OP_ACCEPT);
		return socketSelector;
	}

	public void send(SocketChannel socket, byte[] data) {
		synchronized(this.changeRequests)
		{
			this.changeRequests.add(new ChangeRequest(socket, ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));
			
			synchronized(this.pendingData) {
				List<ByteBuffer> queue = this.pendingData.get(socket);
				if(queue == null) {
					queue = new ArrayList<ByteBuffer>();
					this.pendingData.put(socket, queue);
				}
				queue.add(ByteBuffer.wrap(data));
			}
		}
		this.selector.wakeup();
	}
}
