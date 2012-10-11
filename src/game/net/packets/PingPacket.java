package game.net.packets;

import common.DataPacket;

public class PingPacket extends Packet
{
	public static final short ID = 999;
	
	public long time;
	public long predictedLatency;
	public boolean isReply = false;
	
	public PingPacket()
	{
		super(ID);
	}
	
	@Override
	public void fromData(DataPacket packet)
	{
		if(packet.getShort() != PingPacket.ID)
			return;
		
		this.isReply = packet.getBoolean();
		if(!this.isReply)
		{
			this.time = packet.getLong();
			this.predictedLatency = packet.getLong();
		}
	}
	
	@Override
	public DataPacket toData()
	{
		DataPacket dp = new DataPacket();
		dp.addShort(PingPacket.ID);
		dp.addBoolean(isReply);
		if(!isReply)
		{
			dp.addLong(time);
			dp.addLong(predictedLatency);
		}
		
		return dp;
	}
}
