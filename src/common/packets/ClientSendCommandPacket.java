package common.packets;

import initial3d.engine.Quat;
import initial3d.engine.Vec3;
import common.Command;
import common.CommandType;
import common.DataPacket;
import common.Packet;
import common.entity.EntityMode;

public class ClientSendCommandPacket extends Packet {

	public static final short ID = 10;
	public ClientSendCommandPacket() {
		super(ID);
	}
	
	public Command command;
	public boolean active;

	@Override
	public void fromData(DataPacket packet) {
		if(packet.getShort() != ClientSendCommandPacket.ID)
			return;
		
		byte ordinal = packet.getByte();
		this.command = Command.values()[ordinal];
		this.active = packet.getByte() == (byte)1 ? true : false;
	}

	@Override
	public DataPacket toData() {
		DataPacket pk = new DataPacket();
		pk.addShort(ClientSendCommandPacket.ID);
		pk.addByte((byte)command.ordinal());
		pk.addByte((byte)(this.active ? 1 : 0));
		
		return pk;
	}

	@Override
	public boolean replyValid() {
		// TODO Auto-generated method stub
		return false;
	}

}
