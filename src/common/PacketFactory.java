package common;

import common.packets.LoginPacket;
import common.packets.WelcomePacket;

public class PacketFactory
{
	public static Packet identify(DataPacket p)
	{
		Packet ret;
		switch(p.peekShort())
		{
			case WelcomePacket.ID:
				ret = new WelcomePacket();
				ret.fromData(p);
			break;
			case LoginPacket.ID:
				ret = new LoginPacket();
				ret.fromData(p);
				break;
			default:
				ret = null;				
				break;
		}
		return ret;
	}
}
