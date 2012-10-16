package game.net.packets;

import initial3d.engine.Quat;
import initial3d.engine.Vec3;
import common.DataPacket;

public class MoveMobPacket extends Packet {
	public static final short ID = 9423;
	
	public long eid = 0;
	public Vec3 pos = Vec3.zero;
	public Vec3 vel = Vec3.zero;
	public Quat ori = Quat.one;
	
	public MoveMobPacket()
	{
		super(ID);
	}

	@Override
	public void fromData(DataPacket packet) {
		if(packet.getShort() != ID)
			return;
		
		this.eid = packet.getLong();
		this.pos = Vec3.create(packet.getDouble(), packet.getDouble(), packet.getDouble());
		this.vel = Vec3.create(packet.getDouble(), packet.getDouble(), packet.getDouble());
		this.ori = Quat.create(packet.getDouble(), packet.getDouble(), packet.getDouble(), packet.getDouble());
	}

	@Override
	public DataPacket toData() {
		DataPacket dp = new DataPacket();
		dp.addShort(ID);
		dp.addLong(eid);
		dp.addDouble(pos.x);
		dp.addDouble(pos.y);
		dp.addDouble(pos.z);
		
		dp.addDouble(vel.x);
		dp.addDouble(vel.y);
		dp.addDouble(vel.z);
		
		dp.addDouble(ori.w);
		dp.addDouble(ori.x);
		dp.addDouble(ori.y);
		dp.addDouble(ori.z);
		return dp;
	}

}
