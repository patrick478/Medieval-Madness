package client.networking;
import java.io.IOException;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.channels.spi.*;
import java.util.*;

import common.*;
import common.packets.*;

public class NetworkThread implements Runnable {
	private Selector selector = null;
	Queue<ByteBuffer> toSend = new LinkedList<ByteBuffer>();
	SocketChannel clientChannel;
	NetworkClient nClient;
	
	NetworkHandler handler;
	
	String hostname = "localhost";
	int port = 14121;
	
	public boolean isConnected = false;
	
	public NetworkThread(String hostname, int port, NetworkClient parent)
	{
		this.hostname = hostname;
		this.port = port;
		
		this.nClient = parent;
		
		this.handler = new NetworkHandler(this);
	}
	
	public void run()
	{
		try {
			this.selector = SelectorProvider.provider().openSelector();
			this.clientChannel = SocketChannel.open();
			this.clientChannel.configureBlocking(false);
			this.clientChannel.connect(new InetSocketAddress(hostname, port));
			this.clientChannel.register(this.selector,  SelectionKey.OP_CONNECT);
		} catch(IOException e)
		{
			// TODO: really, ben?
		}
		
		while(true)
		{
			try {
				selector.select();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Set<SelectionKey> keys = selector.selectedKeys();
			if(keys.isEmpty()) continue;
			
			Iterator<SelectionKey> iterator = keys.iterator();

			while(iterator.hasNext())
			{
				SelectionKey key = iterator.next();
				iterator.remove();

				if(!key.isValid())
					continue;

				if(key.isConnectable())
					this.connect(key);
				else if(key.isReadable())
					this.read(key);
				else if(key.isWritable())
				{
					if(this.toSend.size() > 0)
					{
						this.write(this.toSend.remove());
					}
				}
			}
		}
	}

	private void connect(SelectionKey key)
	{
		if(this.clientChannel.isConnectionPending())
		{
			try {
				this.clientChannel.finishConnect();
				key.interestOps(SelectionKey.OP_READ);
			} catch (IOException e) {
				key.cancel();
				return;
			}
		}
	}

	private void read(SelectionKey key)
	{
		ByteBuffer buffer = ByteBuffer.allocate(8192);
		int rx = 0;
		try {
			rx = this.clientChannel.read(buffer);
		} catch (IOException e) {
			if(rx <= 0)
			{
				this.isConnected = false;
				System.out.println("[Network] Uh-oh! Dropped connection to server..");
				return;
			}
		}

		DataPacket p = new DataPacket(buffer.array());
		Packet from = PacketFactory.identify(p);
		
		handler.passoff(from);
	}

	private void write(ByteBuffer data)
	{
		int tx = 0;
		try {
			tx = this.clientChannel.write(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.clientChannel.keyFor(selector).interestOps(SelectionKey.OP_READ);
		}
	}

	public void send(DataPacket p)
	{		
		this.toSend.add(ByteBuffer.wrap(p.getData()));
		this.clientChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
		this.selector.wakeup();
	}
}
