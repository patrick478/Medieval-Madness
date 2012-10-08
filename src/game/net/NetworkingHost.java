package game.net;

import game.net.packets.EnterGamePacket;
import game.net.packets.MovementPacket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class NetworkingHost extends NetworkMode implements Runnable
{
	ServerSocket serverSocket;
	Thread serverThread;
	
	int numPlayers = 0;
	public static final int maxPlayers = 2;
	public List<ServerClient> clients = new ArrayList<ServerClient>();
	
	@Override
	public void modeStart()
	{
		try {
			this.serverSocket = new ServerSocket(14121);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.serverThread = new Thread(this);
		this.serverThread.start();
	}
	
	@Override
	public void destroy()
	{
		
	}
	
	public void run()
	{
		while(this.numPlayers < (this.maxPlayers))
		{
			Socket cSocket = null;
			try {
				cSocket = serverSocket.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(cSocket == null) continue;
			
			ServerClient sc = new ServerClient(numPlayers++);
			sc.socket = cSocket;
			this.clients.add(sc);
			ServerWorker sw = new ServerWorker(sc, this);
			
			sc.thread = new Thread(sw);
			sc.thread.start();
		}
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.printf("Beginning game..\n");
		EnterGamePacket egp = new EnterGamePacket();
		
		for(ServerClient sc : this.clients)
		{
			System.out.printf("Sent 'enterGame' to player #%d\n", sc.getPlayerIndex());
			sc.send(egp.toData());
		}
	}

	public void updateOthersOnMovements(ServerClient client) {
		MovementPacket packet = new MovementPacket(client.getPlayerIndex(), client.getPosition(), client.getVelocity());
		for(ServerClient sc : this.clients)
		{
			if(!sc.equals(client))
			{
				sc.send(packet.toData());
				System.out.printf("Bahaa!!\n");
			}
		}
	}
}