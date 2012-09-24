package common.packets;

import common.DataPacket;
import common.Packet;

public class LoginPacket extends Packet {
	
	public static final short ID = 2;
	
	public String username;
	public String password;
	public boolean loginOkay = false;
	
	public LoginPacket()
	{
		super(ID);
	}
	
	@Override
	public void fromData(DataPacket p) {
		short id = p.getShort();
		if(id != LoginPacket.ID)
		{
			System.out.printf("Data failure. Got %d for ID instead of %d\n", id, LoginPacket.ID);
			return; // TODO: Throw an exceptiopn here
		}
		
		byte b = p.getByte();
		if(b == (byte)0)
			this.isReply = false;
		else
			this.isReply = true;
		
		if(this.isReply)
		{
			if(p.getByte() == (byte)0)
				this.loginOkay = false;
			else
				this.loginOkay = true;
		}
		else
		{
			this.username = p.getString();
			this.password = p.getString();
		}
		
	}

	@Override
	public DataPacket toData() {
		DataPacket p = new DataPacket();
		p.addShort(this.ID);
		if(this.isReply)
		{
			p.addByte((byte)1);
			p.addByte((byte) (loginOkay ? 1 : 0));
		}
		else
		{
			p.addByte((byte)0);
			p.addString(username);
			p.addString(password);
		}
		return p;
	}

	@Override
	public boolean replyValid() {
		return true;
	}
}
