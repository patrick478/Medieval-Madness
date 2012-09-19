package clienttest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

public class ClientTest {
	SocketChannel clientChannel;
	Selector selector;
	SelectionKey clientKey;
	
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
				
				SocketChannel channel = (SocketChannel) key.channel();
				
				if(key.isConnectable())
				{
					System.out.printf("Connected");
					if(channel.isConnectionPending())
					{
						try {
							channel.finishConnect();
							key.interestOps(SelectionKey.OP_WRITE);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				if(key.isWritable())
				{
					ByteBuffer buffer = null;
					while(true)
					{
						buffer = ByteBuffer.wrap(new String("Hello!").getBytes());
						try {
							channel.write(buffer);
						} catch(IOException e) {
							e.printStackTrace();
						}
						buffer.clear();
						System.out.println("Sent!");
					}
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		ClientTest test = new ClientTest();
		test.Connect("localhost",  14121);
	}
}
