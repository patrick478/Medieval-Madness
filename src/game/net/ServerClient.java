package game.net;

import initial3d.engine.Quat;
import initial3d.engine.Vec3;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;

import common.BufferQueue;
import common.DataPacket;

public class ServerClient 
{	
	private Vec3 position = Vec3.zero;
	private Vec3 velocity = Vec3.zero;
	private Quat orientation = Quat.one;
	
	private long predictedLatency = 0;
	private int syncsLeft = 5;
	private long lastSync = System.currentTimeMillis();
	
	private int playerIndex = -1;
	private boolean isReady = false;
	private boolean isHost = false;
	
	private BufferQueue dq = new BufferQueue(8192);
	private BlockingQueue<DataPacket> packets = new LinkedBlockingQueue<DataPacket>();
	private short packetLength = -1;
	private long updatePosTime = System.currentTimeMillis();
	
	private SocketChannel socket;

	public ServerClient(int pIndex, SocketChannel sc)
	{
		this.playerIndex = pIndex;
		this.socket = sc;
	}
	
	public int getPlayerIndex()
	{
		return this.playerIndex;
	}

	public void setPosition(Vec3 p)
	{
		this.updatePosTime = System.currentTimeMillis();
		this.position = p;
	}
	
	public void setVelocity(Vec3 v)
	{
		this.velocity = v;
	}

	public void setOrientation(Quat o)
	{
		this.orientation = o;
	}

	public Quat getOrientation() {
		return this.orientation;
	}

	public Vec3 getPosition() {
		return this.position.add(this.velocity.scale((System.currentTimeMillis() - this.updatePosTime) / 1000));
	}
	
	public Vec3 getVelocity()
	{
		return this.velocity;
	}
	
	public void setPredictedLatency(long pl) {
		this.predictedLatency = pl;
	}

	public long getPredictedLatency() {
		return this.predictedLatency;
	}

	public void addToDataBuffer(byte[] data) {
		this.dq.append(data);
		
		readPacketsFromBuffer();
	}
	
	private void readPacketsFromBuffer()
	{
		while(true)
		{
			if(this.packetLength < 0 && this.dq.getCount() > 2)
			{
				byte[] lenData = new byte[2];
				this.dq.read(lenData, 0, 2);
				this.packetLength = bytesToShort(lenData);
			}
			if(this.dq.getCount() >= this.packetLength && this.packetLength > 0)
			{
				byte[] data = new byte[this.packetLength];
				this.dq.read(data, 0, packetLength);
				DataPacket dp = new DataPacket(data, false);
				this.packets.add(dp);
				
				this.packetLength = -1;
			}
			else
				break;
		}
	}
	
	private short bytesToShort(byte[] data)
	{
		ByteBuffer b = ByteBuffer.wrap(data);
		return b.getShort();
	}

	public boolean hasPackets() {
		return !this.packets.isEmpty();
	}

	public DataPacket getNextPacket() {
		return this.packets.poll();
	}

	public void setSyncsRequired(int i) {
		this.syncsLeft = i;
	}
	
	public void setLastSyncSent(long t) {
		this.lastSync = t;
	}

	public void sentSync() {
		this.syncsLeft--;
	}

	public long getLastSyncTime() {
		return this.lastSync;
	}

	public boolean needsSync() {
		return this.syncsLeft > 0;
	}

	public SocketChannel getSocket() {
		return this.socket;
	}

	public void setReady(boolean newReadyStatus) {
		this.isReady = newReadyStatus;
	}

	public boolean getReadyState() {
		return this.isReady;
	}

	public void setHost() {
		this.isHost = true;
	}
	
	public boolean isHost() {
		return this.isHost;
	}
}