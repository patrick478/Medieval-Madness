package game.net;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;

import common.BufferQueue;
import common.DataPacket;

public class ServerClient 
{
	public Socket socket;
	public Thread thread;
	
	public DataInputStream in;
	public DataOutputStream out;
	
	BufferQueue bq = new BufferQueue(8096);
	public BlockingQueue<DataPacket> dataPackets = new LinkedBlockingQueue<DataPacket>();
	
	private int playerIndex = -1;
	

	public ServerClient(int pIndex)
	{
		this.playerIndex = pIndex;
	}
	
	public void send(DataPacket dp)
	{
		try {
			this.out.write(dp.getData());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getPlayerIndex()
	{
		return this.playerIndex;
	}
}