package game.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import common.DataPacket;
import game.net.packets.MovementPacket;
import game.net.packets.Packet;
import game.net.packets.WelcomePacket;

public class ServerWorker implements Runnable 
{
	ServerClient client = null;
	NetworkingHost host = null;
	public ServerWorker(ServerClient sc, NetworkingHost nh)
	{
		this.client = sc;
		this.host = nh;
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
		wpacket.playerIndex = client.getPlayerIndex();
		this.client.send(wpacket.toData());
		
		short packetSize = -1;
		
		while(true)
		{
			byte[] data = new byte[8192];
			int rx = -1;
			try {
				rx = client.in.read(data, 0, 8192);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(rx < 0) break;
			byte[] actualData = Arrays.copyOf(data, rx);
			
			this.client.bq.append(actualData);
			
			while(true)
			{
				if(packetSize < 0 && this.client.bq.getCount() >= 2)
				{
					byte[] lenData = new byte[2];
					this.client.bq.read(lenData, 0, 2);
					packetSize = peekShort(lenData);				
				}
				else break;
				
				if(packetSize > 0 && this.client.bq.getCount() >= packetSize)
				{
					byte[] pData = new byte[packetSize];
					this.client.bq.read(pData, 0, packetSize);
					DataPacket dp = new DataPacket(pData, false);
					processPacket(dp);
					packetSize = -1;
	//				this.client.dataPackets.add(dp);
				}
			}
		}
	}
	
	private void processPacket(DataPacket dp)
	{
		switch(dp.peekShort())
		{
			case MovementPacket.ID:
				MovementPacket mp = new MovementPacket();
				mp.fromData(dp);
				this.client.setPosition(mp.position);
				this.client.setVelocity(mp.velocity);
				System.out.printf("[ServerClientWorker] Updating position of %d\n", client.getPlayerIndex());
				
				this.host.updateOthersOnMovements(this.client);
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
	
}
