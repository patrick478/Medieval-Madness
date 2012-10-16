package game.net.packets;

import common.DataPacket;

public class GiveItemPacket extends Packet {
	
	public static final short ID = 2806;
	
	public GiveItemPacket()
	{
		super(ID);
	}
	
	public long itemID = 0;
	public long eid = 0;

	@Override
	public void fromData(DataPacket packet)
	{
		if(packet.getShort() != ID)
			return;
		
		this.eid = packet.getLong();
		this.itemID = packet.getLong();
	}

	@Override
	public DataPacket toData()
	{
		DataPacket dp = new DataPacket();
		dp.addShort(ID);
		dp.addLong(eid);
		dp.addLong(itemID);
		return dp;
	}

}
