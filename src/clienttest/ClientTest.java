package clienttest;

import java.io.IOException;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.channels.spi.*;
import java.util.*;

import common.*;
import common.packets.WelcomePacket;

public class ClientTest implements Runnable {
	SocketChannel clientChannel;
	Selector selector;
	SelectionKey clientKey;
	ClientState gameState = ClientState.Welcome;
	int substate = 0;
	Queue<ByteBuffer> toSend = new LinkedList<ByteBuffer>();
	
	public ClientTest() {
	}
	
	public void Connect(String hostname, int port) {
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
		
		
	}
	
	public void run()
	{
		while(true)
		{
			try {
				selector.select();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Set<SelectionKey> keys = selector.selectedKeys();
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
						System.out.printf("Can write..\n");
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
				System.out.printf("Connected");
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
			e.printStackTrace();
		}
		System.out.printf("Read %d bytes from stream\n", rx);
		
		DataPacket p = new DataPacket(buffer.array());
		Packet from = PacketFactory.identify(p);
		
		System.out.printf("Packet is %s\n", from.toString());
		System.out.printf("substate=%d;replyValid=%s;isReply=%s\n", substate, from.replyValid() ? "true" : "false", from.isReply ? "true" : "false");
		
		switch(this.gameState)
		{
			case Welcome:
				if(substate == 0 && from.replyValid() && !from.isReply)
				{
					
					Packet reply = new WelcomePacket();
					reply.isReply = true;
					key.interestOps(SelectionKey.OP_WRITE);
					this.selector.wakeup();
					
					this.send(reply.toData());
					this.gameState = ClientState.Login;
				}
			break;
		}
	}
	
	private void write(ByteBuffer data)
	{
		int tx = 0;
		try {
			tx = this.clientChannel.write(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.printf("Wrote %d bytes\n", tx);
	}
	
	private void send(DataPacket p)
	{		
		this.toSend.add(ByteBuffer.wrap(p.getData()));
	}
	
	public static void main(String[] args)
	{
		ClientTest test = new ClientTest();
		test.Connect("localhost",  14121);
		Thread t = new Thread(test);
		t.run();
	}
}
