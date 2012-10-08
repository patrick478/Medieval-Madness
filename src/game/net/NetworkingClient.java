package game.net;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.concurrent.*;

import game.states.*;

import common.BufferQueue;
import common.DataPacket;
import game.net.packets.*;

public class NetworkingClient extends NetworkMode implements Runnable {

	Socket clientSocket;
	Thread clientThread;
	
	DataInputStream in;
	DataOutputStream out;
	
	BufferQueue bq = new BufferQueue(8096);
	BlockingQueue<DataPacket> dataPackets = new LinkedBlockingQueue<DataPacket>();
	
	@Override
	public void modeStart() 
	{
		try {
			this.clientSocket = new Socket("localhost", 14121);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(clientSocket.isConnected())
		{
			this.clientThread = new Thread(this);
			this.clientThread.start();
		}
	}

	@Override
	public void destroy()
	{
	}

	@Override
	public void run()
	{
		try {
			this.in = new DataInputStream(this.clientSocket.getInputStream());
			this.out = new DataOutputStream(this.clientSocket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		short packetSize = -1;
		
		while(true)
		{
			byte data = 0;
			try {
				data = this.in.readByte();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			this.bq.append(new byte[] { data });
			if(packetSize > 0 && this.bq.getCount() >= packetSize)
			{
				byte[] pData = new byte[packetSize];
				this.bq.read(pData, 0, packetSize);
				DataPacket dp = new DataPacket(pData, false);
				processPacket(dp);
//				this.dataPackets.add(dp);
			}
			else if(packetSize < 0 && this.bq.getCount() >= 2)
			{
				byte[] lenData = new byte[2];
				this.bq.read(lenData, 0, 2);
				packetSize = peekShort(lenData);		
			}
		}
	}
	
	private void processPacket(DataPacket dp) {
		System.out.printf("Packet header id=%d\n", dp.peekShort());
		switch(dp.peekShort())
		{
			case WelcomePacket.ID:
				dp.getShort();
				System.out.printf("length=%d\n", dp.getLength());
				int pIndex = dp.getShort();
				this.game.setPlayerIndex(pIndex);
				break;
				
			case EnterGamePacket.ID:
				dp.getShort();
				this.game.changeState(new LoadingGameState(this.game));
				break;
		}
	}

	private short peekShort(byte[] data)
	{
		ByteBuffer bb = ByteBuffer.allocate(2);
		bb.put(data[0]);
		bb.put(data[1]);
		return bb.getShort(0);
	}

	public void send(DataPacket data) {
		try {
			this.out.write(data.getData());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}
