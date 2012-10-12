package game.net.packets;

import initial3d.engine.Vec3;
import common.DataPacket;

public class MovementPacket extends Packet
{
	public static final short ID = 3;
	
	public int playerIndex = -1;
	public Vec3 position = Vec3.one;
	public Vec3 velocity = Vec3.zero;
	
	public MovementPacket()
	{
		super(ID);
	}
	
	public MovementPacket(int i, Vec3 p, Vec3 m)
	{
		super(ID);
		this.playerIndex = i;
		this.position = p;
		this.velocity = m;
	}
	
	@Override
	public void fromData(DataPacket packet) {
		if(packet.getShort() != MovementPacket.ID)
			return;
		
		this.playerIndex = packet.getShort();
		this.position = (Vec3.create(packet.getDouble(), packet.getDouble(), packet.getDouble()));
		this.velocity = (Vec3.create(packet.getDouble(), packet.getDouble(), packet.getDouble()));
	}
	@Override
	public DataPacket toData() {
		DataPacket dp = new DataPacket();
		dp.addShort(MovementPacket.ID);
		
		dp.addShort(this.playerIndex);
		
		dp.addDouble(this.position.x);
		dp.addDouble(this.position.y);
		dp.addDouble(this.position.z);
		
		dp.addDouble(this.velocity.x);
		dp.addDouble(this.velocity.y);
		dp.addDouble(this.velocity.z);
		
		return dp;
	}
}
