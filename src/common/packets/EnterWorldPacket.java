package common.packets;

import common.DataPacket;
import common.Packet;

public class EnterWorldPacket extends Packet {
	public static final short ID = 3;
	
	public int newWorld;

	public EnterWorldPacket()
	{
		super(ID);
	}
	
	@Override
	public void fromData(DataPacket packet) {
		if(packet.getShort() != EnterWorldPacket.ID)
			return;
		
		this.newWorld = packet.getInt();
	}

	@Override
	public DataPacket toData() {
		DataPacket p = new DataPacket();
		
		p.addShort(EnterWorldPacket.ID);
		p.addInt(newWorld);
		return p;
	}

	@Override
	public boolean replyValid() {
		return true;
	}

}
