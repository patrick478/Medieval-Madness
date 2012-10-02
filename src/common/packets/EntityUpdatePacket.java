package common.packets;

import initial3d.engine.Quat;
import initial3d.engine.Vec3;
import common.DataPacket;
import common.Packet;

public class EntityUpdatePacket extends Packet {
	
	public long entityID = 0;
	public Vec3 position = Vec3.zero;
	public Vec3 velocity = Vec3.zero;
	public Quat orientation = Quat.one;
	public Vec3 angularVel = Vec3.zero;

	public static final short ID = 6;
	public EntityUpdatePacket() {
		super(ID);
	}

	@Override
	public void fromData(DataPacket pk) {
		if(pk.getShort() != EntityUpdatePacket.ID)
				return;
		
		this.entityID = pk.getLong();
		// pos
		
		this.position = Vec3.create(pk.getFloat(), pk.getFloat(), pk.getFloat());
		
		// vel
		
		this.velocity = Vec3.create(pk.getFloat(), pk.getFloat(), pk.getFloat());

		// orientation
		
		this.orientation = Quat.create(pk.getFloat(), pk.getFloat(), pk.getFloat(), pk.getFloat());

		// ang-vel
		
		this.angularVel = Vec3.create(pk.getFloat(), pk.getFloat(), pk.getFloat());
	}

	@Override
	public DataPacket toData() {
		DataPacket pk = new DataPacket();
		pk.addShort(EntityUpdatePacket.ID);
		
		pk.addLong(entityID);
		
		// pos
		pk.addFloat((float)this.position.x);
		pk.addFloat((float)this.position.y);
		pk.addFloat((float)this.position.z);
		
		// vel
		pk.addFloat((float)this.velocity.x);
		pk.addFloat((float)this.velocity.y);
		pk.addFloat((float)this.velocity.z);
		
		// orientation
		pk.addFloat((float)this.orientation.w);
		pk.addFloat((float)this.orientation.x);
		pk.addFloat((float)this.orientation.y);
		pk.addFloat((float)this.orientation.z);
		
		// ang-vel
		pk.addFloat((float)this.angularVel.x);
		pk.addFloat((float)this.angularVel.y);
		pk.addFloat((float)this.angularVel.z);
		
		return pk;
	}

	@Override
	public boolean replyValid() {
		return false;
	}

}
