package game.net.packets;

import common.DataPacket;

public class EnterPrePostPacket extends Packet {

	public static final short ID = 10;
	private short targetScreen = 0;
	
	public EnterPrePostPacket() {
		super(ID);
	}

	@Override
	public void fromData(DataPacket packet) {
		if(packet.getShort() != EnterPrePostPacket.ID)
			return;
		
		this.targetScreen = packet.getShort();
	}

	@Override
	public DataPacket toData() {
		DataPacket p = new DataPacket();
		p.addShort(EnterPrePostPacket.ID);
		p.addShort(targetScreen);
		return p;
	}
	
	public boolean isPre()
	{
		return this.targetScreen == 0;
	}
	
	public boolean isPost()
	{
		return this.targetScreen == 1;
	}

	public void setPre() {
		this.targetScreen = 0;
	}

}
