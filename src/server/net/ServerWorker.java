package server.net;

import initial3d.engine.Vec3;

import java.nio.channels.*;
import java.util.*;

import server.Server;
import server.game.EntityManager;
import server.game.PlayerManager;
import server.game.ServerPlayer;
import server.session.Session;
import server.session.SessionMngr;
import server.session.SessionState;

import common.BufferQueue;
import common.DataPacket;
import common.Packet;
import common.PacketFactory;
import common.entity.Player;
import common.packets.*;

public class ServerWorker implements Runnable {
	private List<ServerDataEvent> queue = new LinkedList<ServerDataEvent>();
	private Server parentServer;
	
	private BufferQueue bq = new BufferQueue(8192);
	private boolean hasReadPacket = true;
	short packetLength = 0;
	byte[] packetBytes;
	
	public ServerWorker(Server s) {
		this.parentServer = s;
	}
	
	public void processData(ServerLayer server, SocketChannel sc, byte[] data, int dsize, String id)
	{
		byte[] dCopy = Arrays.copyOf(data, dsize);
		byte[] peek = new byte[2];
		bq.append(dCopy);
		
		do
		{
			if(bq.getCount() >= 2 && hasReadPacket)
			{
				bq.read(peek, 0, 2);
				packetLength = this.peekShort(peek, 0);
				packetBytes = new byte[packetLength];
//				System.out.printf("Reading packet with length=%d\n", packetLength);
				hasReadPacket = false;
			}
			
			if(!hasReadPacket && bq.getCount() >= packetLength)
			{
				
				bq.read(packetBytes, 0, packetLength);
//				for(int i = 0; i < packetBytes.length; i++)
//					System.out.printf("%02X ", packetBytes[i]);
//				DataPacket dataPacket = new DataPacket(packetBytes, false);
//				System.out.println("Packet: ");
//				dataPacket.printPacket();
//				Packet recvdPacket = PacketFactory.identify(dataPacket);
				
				synchronized(queue) {
					queue.add(new ServerDataEvent(server, sc, packetBytes, id));
					queue.notify();
				}
				
				packetLength = -1;
				hasReadPacket = true;
			}
			else break;
			
		} while(true);
		
//		// TODO: this only deals with the first packet if multiple packets are merged
//		int s = peekShort(data, 0);
////		System.out.printf("Recieved packet. header.size=%d\n", s);
//		System.arraycopy(data, 2, dataCopy, 0, s);

	}
	
	private short peekShort(byte[] d, int i)
	{
		if(d.length < 2) return 0;
		
		short s = (short)((d[i] << 8) |(d[1 + i] & 0xFF));
		return s;
	}

	@Override
	public void run() {
		ServerDataEvent dataEvent;
		
		boolean exiting = false;
		
		this.parentServer.log.printf("ServerWorker :: starting up\n");
		
		while(!exiting)
		{
			synchronized(queue) {
				while(queue.isEmpty()) { // FIXME
					try {
						queue.wait();
					} catch(InterruptedException ie) {
						exiting = true;
						break;
					}
				}
				if(exiting) break;
				dataEvent = (ServerDataEvent)queue.remove(0);
			}
			
			DataPacket p = new DataPacket(dataEvent.data, false);
			Session s = SessionMngr.getInstance().getSession(dataEvent.session);
			if(s == null) return; // TODO: This is bad.
			
			process(p, s, dataEvent.server);
		}
		
		this.parentServer.log.printf("ServerWorker :: detected shutdown - stopping\n");
	}
	
	public void process(DataPacket p, Session s, ServerLayer sl)
	{
		Packet from = PacketFactory.identify(p);
		if(from == null)
		{
			System.out.println("[WARNING] Unidentifable packet recieved, ignoring..");
			return;
		}
		
		switch(s.getState())
		{
			case Welcome:
				if(from.ID() == WelcomePacket.ID && s.getSubstate() == 1 && from.replyValid() && from.isReply)
				{
					s.setState(SessionState.Login);
					s.setSubstate(0);
				}				
				break;
				
			case Login:
				if(s.getSubstate() == 0 && !from.isReply)
				{
					LoginPacket px = (LoginPacket)from;
					String username = px.username.toLowerCase();
					String password = px.password;
					
					// temporary
					if(!this.parentServer.db.accountExists(username))
						this.parentServer.db.createAccount(username,  password);
					
					boolean loginSuccess = this.parentServer.db.passwordCorrect(username, password);
					
					px.isReply = true;
					px.loginOkay = loginSuccess;
					sl.send(s.getSocket(), px.toData().getData());
					
					if(loginSuccess)
					{
						this.parentServer.log.printf("Login attempt (%s). Password correct=%s\n", username, loginSuccess ? "yes" : "no", password);
						long id = System.nanoTime();
						ServerPlayer player = new ServerPlayer(Vec3.one, id);
						player.world = 0;
						player.segmentX = this.parentServer.serverSettings.getIntValue("default_spawn_segment_x", 0);
						player.segmentZ = this.parentServer.serverSettings.getIntValue("default_spawn_segment_x", 0);
						player.session = s;
						
						EntityManager.getInstance().addMoveableEntity(player, id);
						PlayerManager.getInstance().addPlayer(username, player);
					
						// send into game world
						EnterWorldPacket ewp = new EnterWorldPacket();
						ewp.newWorld = 0;
						sl.send(s.getSocket(), ewp.toData().getData());
						
						int range = 12;
						for(int i = -(range / 2) + 1; i <= (range/2); i++)
						{
							for(int j = -(range / 2) + 1; j <= (range/2); j++)
							{
//								SegmentPacket sp = new SegmentPacket();
//								sp.segment = this.parentServer.game.getSegment(i, j);
//								System.out.printf("Sent segment. Request(i:%d;j%d)-Sent(i:%d;j:%d)\n", i, j, sp.segment.xPos, sp.segment.zPos);
//								sl.send(s.getSocket(), sp.toData().getData());
								
								this.parentServer.game.addSegmentRequest(s, i, j);
								
//								try {
//									//Thread.sleep(500);
//								} catch (InterruptedException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
							}
						}
						
						System.out.println("Test packets queued");
					}
					
				}
				break;
		}
	}
}
