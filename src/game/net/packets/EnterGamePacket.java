package game.net.packets;

import initial3d.engine.Vec3;
import common.DataPacket;


public class EnterGamePacket extends Packet {
	public static final short ID = 2;
	
	public Vec3 position = Vec3.zero;
	public EnterGamePacket()
	{
		super(ID);
	}
	
	@Override
	public void fromData(DataPacket packet) {
		if(packet.getShort() != EnterGamePacket.ID)
		{
			System.out.printf("Oh, no.\n");
			return;
		}
		this.position = Vec3.create(packet.getDouble(), packet.getDouble(), packet.getDouble());
	}
	@Override
	public DataPacket toData() {
		DataPacket dp = new DataPacket();
		dp.addShort(EnterGamePacket.ID);
		dp.addDouble(position.x);
		dp.addDouble(position.y);
		dp.addDouble(position.z);
		return dp;
	}
}
