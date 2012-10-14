package game.net;

import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import common.DataPacket;
import game.Game;
import game.net.packets.ChangeAttributePacket;
import game.net.packets.MovementPacket;
import game.net.packets.PingPacket;
import game.net.packets.ProjectileLifePacket;
import game.net.packets.SetReadyPacket;

public class ServerWorker implements Runnable 
{
	BlockingQueue<ServerDataEvent> dataQueue = new LinkedBlockingQueue<ServerDataEvent>();
	
	private NetworkingHost server;
	public ServerWorker(NetworkingHost _server)
	{
		this.server = _server;
	}
	
	public void processData(NetworkingHost server, ServerClient c, SocketChannel sc, byte[] data, int rx)
	{
		byte[] dataCopy = new byte[rx];
		System.arraycopy(data,  0,  dataCopy,  0,  rx);
		dataQueue.add(new ServerDataEvent(server, sc, c, dataCopy));
		
	}
	
	@Override
	public void run()
	{
		ServerDataEvent dEvent = null;
		while(true)
		{
			try {
				dEvent = this.dataQueue.take();
			} catch (InterruptedException e) {
				// TODO: Proper error message
				System.err.printf("Ben - fix this!\n");
			}
			
			// do some stuff with that data.
			ServerClient client = dEvent.getClient();
			client.addToDataBuffer(dEvent.getData());
			
			while(client.hasPackets())
			{
				DataPacket dp = client.getNextPacket();
				processPacket(client, dp);
			}
		}
	}

	private void processPacket(ServerClient client, DataPacket dp) {
		switch(dp.peekShort())
		{
			case PingPacket.ID:
				long recvTime = System.currentTimeMillis();
				long roundTrip = recvTime - client.getLastSyncTime();
				long latency = roundTrip / 2;
				client.setPredictedLatency(latency);
				
				if(client.needsSync())
				{
					PingPacket syncPacket = new PingPacket();
					syncPacket.isReply = false;
					syncPacket.predictedLatency = client.getPredictedLatency();
					syncPacket.time = System.currentTimeMillis();
					client.setLastSyncSent(syncPacket.time);
					this.server.send(client.getSocket(),  syncPacket.toData().getData());
					client.sentSync();
				}
			break;
			
			case MovementPacket.ID:
				MovementPacket mp = new MovementPacket();
				mp.fromData(dp);
				client.setPosition(mp.position);
				client.setVelocity(mp.velocity);
				client.setOrientation(mp.orientation);
				
				this.server.updateOthersOnMovements(client);
			break;
			
			case SetReadyPacket.ID:
				SetReadyPacket srp = new SetReadyPacket();
				srp.fromData(dp);
				client.setReady(srp.newReadyStatus);
				System.out.println("boo");
				this.server.updateOthersOnReadyChange(client);
				
				this.server.checkReady();
				break;
				
			case ProjectileLifePacket.ID:
				ProjectileLifePacket plp = new ProjectileLifePacket();
				plp.fromData(dp);
//				if(plp.isCreateMode())
//				{
//					Game.getInstance().selfCreateProjectile(plp.eid, plp.pos, plp.vel, plp.ori, plp.creator, plp.createTime);
					this.server.notifyAllClients(plp);
//				}
				break;
				
			case ChangeAttributePacket.ID:
				ChangeAttributePacket cap = new ChangeAttributePacket();
				cap.fromData(dp);
				cap.pindex = client.getPlayerIndex();
				this.server.notifyAllClients(cap);
				break;
		}
	}
}

	/*
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
		
		PingPacket pp = new PingPacket();
		pp.time = System.currentTimeMillis();
		pp.predictedLatency = client.getPredictedLatency();
		pp.isReply = false;
		this.client.send(pp.toData());
		this.client.syncsLeft = 5;
		
		short packetSize = -1;
		
		synchronized(this)
		{
			this.isReady = true;
			this.notifyAll();
		}
		
		while(true)
		{
			byte[] data = new byte[ServerClient.BUFFER_SIZE];
			int rx = -1;
			try {
				rx = client.in.read(data, 0, ServerClient.BUFFER_SIZE);
			} catch (IOException e) {
				break;
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
				
				this.host.updateOthersOnMovements(this.client);
			break;
			
			case PingPacket.ID:
				PingPacket pp = new PingPacket();
				pp.fromData(dp);
				if(!pp.isReply) return; 
				this.client.syncsLeft--;
				this.client.setPredictedLatency(pp.predictedLatency);
				if(this.client.syncsLeft > 0)
				{
					PingPacket pp2 = new PingPacket();
					pp2.time = System.currentTimeMillis();
					pp2.predictedLatency = this.client.getPredictedLatency();
					pp2.isReply = false;
					this.client.send(pp2.toData());
				}
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
	
}*/
