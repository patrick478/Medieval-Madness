package game.net.packets;

import common.DataPacket;


public class EnterGamePacket extends Packet {
	public static final short ID = 2;
	public EnterGamePacket()
	{
		super(ID);
	}
	
	@Override
	public void fromData(DataPacket packet) {
		if(packet.getShort() != EnterGamePacket.ID)
			return;
	}
	@Override
	public DataPacket toData() {
		DataPacket dp = new DataPacket();
		dp.addShort(EnterGamePacket.ID);
		return dp;
	}
}
