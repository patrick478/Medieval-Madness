package game.net.packets;

import common.DataPacket;

public class SetReadyPacket extends Packet {

	public static final short ID = 11;
	public SetReadyPacket() {
		super(ID);
	}
	
	public int pIndex = 0;
	public boolean newReadyStatus = false;
	
	@Override
	public void fromData(DataPacket packet) {
		if(packet.getShort() != SetReadyPacket.ID)
			return;
		
		this.pIndex = packet.getShort();
		this.newReadyStatus = packet.getByte() == 0 ? false : true;
	}
	@Override
	public DataPacket toData() {
		DataPacket p = new DataPacket();
		p.addShort(SetReadyPacket.ID);
		p.addShort(this.pIndex);
		p.addByte((byte)(newReadyStatus ? 1 : 0));
		return p;
	}
	
}
