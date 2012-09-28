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
	
	private BufferQueue bq = new BufferQueue(1000000);

	byte[] peek = new byte[2];
	byte[] packetBytes = null;
	int packetLength = -1;
	boolean hasReadPacket = true;
	
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
				this.isConnected = true;
			} catch (IOException e) {
				key.cancel();
				return;
			}
		}
	}

	private void read(SelectionKey key)
	{
		ByteBuffer buffer = ByteBuffer.allocate(81920);
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
		
		byte[] data = buffer.array();
		if(rx < 0)
		{
			this.isConnected = false;
//			this.nClient.reconnect(); // TODO: Implement
		}
		
		data = Arrays.copyOf(data, rx);
		bq.append(data);
		
		do
		{
			if(bq.getCount() >= 2 && hasReadPacket)
			{
				bq.read(peek, 0, 2);
				packetLength = this.peekShort(peek, 0);
				packetBytes = new byte[packetLength];
				System.out.printf("Reading packet with length=%d\n", packetLength);
				hasReadPacket = false;
			}
			
			if(!hasReadPacket && bq.getCount() >= packetLength)
			{
				
				bq.read(packetBytes, 0, packetLength);
				for(int i = 0; i < packetBytes.length; i++)
					System.out.printf("%02X ", packetBytes[i]);
				DataPacket dataPacket = new DataPacket(packetBytes);
				Packet recvdPacket = PacketFactory.identify(dataPacket);
				this.handler.passoff(recvdPacket);
				packetLength = -1;
				hasReadPacket = true;
			}
			else break;
			
		} while(true);
//		
//		System.out.printf("Recieved %d bytes\n", rx);
//		int index = 0;
//		while(index < rx)
//		{
//			int size = peekShort(data, index);
//			index += 2;
//			System.out.printf("Recieved packet - header says length = %d\n", size);
//			if((index + size) > rx)
//			{
//				System.out.println("TCP Split has occured! Goddammit!");
//			}
//			
//			byte[] thisP = Arrays.copyOfRange(data, index, index+size);
//			for(int i = 0; i < thisP.length; i++)
//				System.out.printf("%02X ", thisP[i]);
//			System.out.println();
//
//			DataPacket p = new DataPacket(thisP);
//			Packet from = PacketFactory.identify(p);
//			handler.passoff(from);
//			
//			index += size;
//			
//			System.out.printf("Finished reading that packet. index=%d, rx=%d\n", index, rx);
//		}
	}
	
	private short peekShort(byte[] d, int i)
	{
		if(d.length < 2) return 0;
		
		short s = (short)((d[i] << 8) |(d[1 + i] & 0xFF));
		return s;
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
