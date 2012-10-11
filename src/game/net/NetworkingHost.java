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
	public int maxPlayers = -1;
	public List<ServerClient> clients = new ArrayList<ServerClient>();
	
	public List<ServerWorker> workers = new ArrayList<ServerWorker>();
	
	@Override
	protected void modeStart()
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
		if(this.maxPlayers < 1)
			return;
		
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
			workers.add(sw);
		}
		
		for(ServerWorker sw : workers)
		{
			while(!sw.isReady())
			{
				synchronized(sw)
				{
					try {
						sw.wait(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		System.out.printf("Game starting..\n");
		
		EnterGamePacket egp = new EnterGamePacket();
		
		for(ServerClient sc : this.clients)
		{
			sc.send(egp.toData());
		}
	}

	public void updateOthersOnMovements(ServerClient client) {
		MovementPacket packet = new MovementPacket(client.getPlayerIndex(), client.getPosition(), client.getVelocity());
		for(ServerClient sc : this.clients)
		{
			if(sc.equals(client))
				continue;
			
			sc.send(packet.toData());
		}
	}

	public void setNumPlayers(int n) {
		System.out.printf("Number of players changed to %d\n", n);
		this.maxPlayers = n;
	}
}