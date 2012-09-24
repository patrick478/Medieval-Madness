package common.packets;

import common.DataPacket;
import common.Packet;

public class WelcomePacket extends Packet {
	
	public static final short ID = 1;
	
	public WelcomePacket()
	{
		super(ID);
	}
	
	@Override
	public void fromData(DataPacket packet) {
		if(packet.getShort() == 1)
			this.isReply = true;
		else
			this.isReply = false;
	}

	@Override
	public DataPacket toData() {
		DataPacket p = new DataPacket();
		p.addShort(this.ID);
		if(this.isReply)
			p.addShort(1);
		else
			p.addShort(0);
		return p;
	}

	@Override
	public boolean replyValid() {
		return true;
	}
}
