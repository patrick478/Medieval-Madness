package game.net.packets;

import common.DataPacket;


public class EntityDestroyPacket extends Packet {
	public static final short ID = 31415;
	
	public long eid = 0;
	
	public EntityDestroyPacket()
	{
		super(ID);
	}

	@Override
	public void fromData(DataPacket packet)
	{
		if(packet.getShort() != ID)
			return;
		
		this.eid = packet.getLong();
	}

	@Override
	public DataPacket toData()
	{
		DataPacket dp = new DataPacket();
		dp.addShort(ID);
		dp.addLong(eid);
		return dp;
	}

}
