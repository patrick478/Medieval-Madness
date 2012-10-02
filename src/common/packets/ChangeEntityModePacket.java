package common.packets;

import common.DataPacket;
import common.Packet;

public class ChangeEntityModePacket extends Packet {
	public long entityID = 0;
	public EntityMode mode = EntityMode.Dead;
	
	public static final short ID = 5;
	public ChangeEntityModePacket() {
		super(ID);
	}

	@Override
	public void fromData(DataPacket packet) {
		if(packet.getShort() != ChangeEntityModePacket.ID)
			return;
		
		this.entityID = packet.getLong();
		byte ordinal = packet.getByte();
		this.mode = EntityMode.values()[ordinal];
	}

	@Override
	public DataPacket toData() {
		DataPacket pk = new DataPacket();
		pk.addShort(ChangeEntityModePacket.ID);
		pk.addLong(this.entityID);
		byte b = (byte)mode.ordinal();
		pk.addByte(b);
		return pk;
	}

	@Override
	public boolean replyValid() {
		// TODO Auto-generated method stub
		return false;
	}

}
