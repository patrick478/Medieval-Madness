package game.net.packets;

import initial3d.engine.Quat;
import initial3d.engine.Vec3;
import game.Game;
import game.entity.moveable.ProjectileEntity;
import common.DataPacket;

public class ProjectileLifePacket extends Packet {

	public static final short ID = 13309;
	public ProjectileLifePacket()
	{
		super(ID);
	}
	

	
	public long eid = 0;
	public int mode = 0;
	
	public Vec3 pos = Vec3.zero;
	public Vec3 vel = Vec3.zero;
	public Quat ori = Quat.one;
	public short creator = 0;
	public long createTime = 0;
	
	@Override
	public void fromData(DataPacket packet) {
		if(packet.getShort() != ID)
			return;
		

		this.mode = packet.getByte();		
		this.eid = packet.getLong();
		
		if(mode == 0)
		{
			this.pos = Vec3.create(packet.getDouble(), packet.getDouble(), packet.getDouble());
			this.vel = Vec3.create(packet.getDouble(), packet.getDouble(), packet.getDouble());
			this.ori = Quat.create(packet.getDouble(), packet.getDouble(), packet.getDouble(), packet.getDouble());
			creator = packet.getShort();
			createTime = packet.getLong();
		}
	}

	@Override
	public DataPacket toData() {		
		DataPacket dp = new DataPacket();
		dp.addShort(ID);
		dp.addByte((byte)mode);
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
		
		dp.addShort(creator);
		dp.addLong(createTime);
		
		return dp;
	}

	public void setCreateMode() {
		this.mode = 0;
	}
	
	public boolean isCreateMode() {
		return this.mode == 0;
	}
}
