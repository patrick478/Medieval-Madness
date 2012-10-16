package game.net.packets;

import common.DataPacket;

public class EnterPrePostPacket extends Packet {

	public static final short ID = 10;
	
	public long sgtime = 0;
	private short targetScreen = 0;
	
	public EnterPrePostPacket(long gt) {
		super(ID);
		sgtime = gt;
	}

	@Override
	public void fromData(DataPacket packet) {
		if(packet.getShort() != EnterPrePostPacket.ID)
			return;
		
		this.targetScreen = packet.getShort();
		this.sgtime = packet.getLong();
	}

	@Override
	public DataPacket toData() {
		DataPacket p = new DataPacket();
		p.addShort(EnterPrePostPacket.ID);
		p.addShort(targetScreen);
		p.addLong(this.sgtime);
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
	
	public void setPost() {
		this.targetScreen = 1;
	}
}
