package game.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import common.DataPacket;
import game.net.packets.Packet;
import game.net.packets.WelcomePacket;

public class ServerWorker implements Runnable 
{
	ServerClient client = null;
	int pIndex = 0;
	public ServerWorker(ServerClient sc)
	{
		this.client = sc;
		System.out.printf("Client %d networking ready..\n", client.getPlayerIndex());
	}
	
	@Override
	public void run()
	{
		try {
			this.client.in = new DataInputStream(this.client.socket.getInputStream());
			this.client.out = new DataOutputStream(this.client.socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		WelcomePacket wpacket = new WelcomePacket();
		wpacket.playerIndex = this.pIndex;
		this.client.send(wpacket.toData());
		
		short packetSize = -1;
		
		while(true)
		{
			byte data = 0;
			try {
				data = this.client.in.readByte();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			this.client.bq.append(new byte[] { data });
			if(packetSize > 0 && this.client.bq.getCount() >= packetSize)
			{
				byte[] pData = new byte[packetSize];
				this.client.bq.read(pData, 0, packetSize);
				DataPacket dp = new DataPacket(pData, false);
				this.client.dataPackets.add(dp);
			}
			else if(packetSize < 0 && this.client.bq.getCount() >= 2)
			{
				byte[] lenData = new byte[2];
				this.client.bq.read(lenData, 0, 2);
				packetSize = peekShort(lenData);				
			}
		}
	}
	
	private short peekShort(byte[] data)
	{
		ByteBuffer bb = ByteBuffer.allocate(2);
		bb.put(data[0]);
		bb.put(data[1]);
		return bb.getShort(0);
	}
	
}
