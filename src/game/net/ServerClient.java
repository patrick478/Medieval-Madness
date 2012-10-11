package game.net;

import initial3d.engine.Vec3;

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
	
	public Vec3 position = Vec3.zero;
	public Vec3 velocity = Vec3.zero;
	
	private long predictedLatency = 0;
	public int syncsLeft = 5;
	public long lastSync = System.currentTimeMillis();
	
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

	public void setPosition(Vec3 p)
	{
		this.position = p;
	}
	
	public void setVelocity(Vec3 v)
	{
		this.velocity = v;
	}

	public Vec3 getPosition() {
		return this.position;
	}
	
	public Vec3 getVelocity()
	{
		return this.velocity;
	}
	
	public void setPredictedLatency(long pl) {
		this.predictedLatency = pl;
		System.out.printf("Setting latency to %d\n", pl);
	}

	public long getPredictedLatency() {
		return this.predictedLatency;
	}
}