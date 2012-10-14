package game.net.packets;

import common.DataPacket;

public class ChangeAttributePacket extends Packet {
	
	public static final short ID = 543;
	
	public ChangeAttributePacket()
	{
		super(ID);
	}
	
	public long eid = 0;
	private int attribute = 0;
	public int newVal = 0;

	@Override
	public void fromData(DataPacket packet) {
		if(packet.getShort() != ID)
			return;
		
		this.eid = packet.getLong();
		this.attribute = packet.getShort();
		this.newVal = packet.getShort();
	}

	@Override
	public DataPacket toData() {
		DataPacket pk = new DataPacket();
		pk.addShort(ID);
		pk.addLong(eid);
		pk.addShort(attribute);
		pk.addShort(newVal);
		return pk;
	}
	
	public void setHealth()
	{
		this.attribute = 0;
	}
	
	public boolean isHealth()
	{
		return this.attribute == 0;
	}
	
	public void setEnergy()
	{
		this.attribute = 1;
	}
	
	public boolean isEnergy()
	{
		return this.attribute == 1;
	}
}
