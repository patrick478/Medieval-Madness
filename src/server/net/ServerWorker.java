package server.net;

import java.nio.channels.*;
import java.util.*;

import server.Server;
import server.session.Session;
import server.session.SessionMngr;
import server.session.SessionState;

import common.DataPacket;
import common.Packet;
import common.PacketFactory;
import common.packets.*;

public class ServerWorker implements Runnable {
	private List<ServerDataEvent> queue = new LinkedList<ServerDataEvent>();
	private Server parentServer;
	
	public ServerWorker(Server s) {
		this.parentServer = s;
	}
	
	public void processData(ServerLayer server, SocketChannel sc, byte[] data, int dsize, String id)
	{
		byte[] dataCopy = new byte[dsize];
		
		int s = peekShort(data, 0);
		System.out.printf("Recieved packet. header.size=%d\n", s);
		System.arraycopy(data, 2, dataCopy, 0, s);
		
		synchronized(queue) {
			queue.add(new ServerDataEvent(server, sc, dataCopy, id));
			queue.notify();
		}
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
				while(queue.isEmpty()) {
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
			
//			System.out.printf("Recieved %d byte%s [SessionID=%s]\n", dataEvent.data.length, (dataEvent.data.length == 1 ? "" : "s"), dataEvent.session);
			DataPacket p = new DataPacket(dataEvent.data);
			Session s = SessionMngr.getInstance().getSession(dataEvent.session);
			if(s == null) return; // TODO: This is bad.
			
			process(p, s, dataEvent.server);
			
			/*
			int a = p.getShort();
			int b = p.getShort();
			int result = a * b;
			DataPacket reply = new DataPacket();
			//System.out.printf("Client %s requested %d*%d. Result=%d\n", dataEvent.session, a, b, result);
			reply.addShort(result);
			
			dataEvent.server.send(dataEvent.socket, reply.getData());
			*/
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
		
		System.out.println(s.getState().toString());
		
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
					System.out.println("Got here");
					LoginPacket px = (LoginPacket)from;
					String username = px.username;
					String password = px.password;
					
					boolean loginSuccess = true;//this.parentServer.db.passwordCorrect(username, password);
					this.parentServer.log.printf("Login attempt (%s). Password correct=%s\n", username, loginSuccess ? "yes" : "no", password);
					
					
					px.isReply = true;
					px.loginOkay = loginSuccess;
					sl.send(s.getSocket(), px.toData().getData());
					
					if(loginSuccess)
					{
						// skip character selection
					
					
						// send into game world
						EnterWorldPacket ewp = new EnterWorldPacket();
						ewp.newWorld = 0;
						sl.send(s.getSocket(), ewp.toData().getData());
						
						for(int i = -5; i < 6; i++)
						{
							for(int j = -5; j < 6; j++)
							{
								SegmentPacket sp = new SegmentPacket();
								sp.segment = this.parentServer.game.getSegment(i, j);
								sl.send(s.getSocket(), sp.toData().getData());
								
//								try {
//									//Thread.sleep(500);
//								} catch (InterruptedException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
							}
						}
						
						System.out.println("Test packets sent");
					}
					
				}
				break;
		}
	}
}
