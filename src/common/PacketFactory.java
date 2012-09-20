package common;

import common.packets.WelcomePacket;

public class PacketFactory
{
	public static Packet identify(DataPacket p)
	{
		Packet ret;
		switch(p.getShort())
		{
			case WelcomePacket.ID:
				ret = new WelcomePacket();
				ret.fromData(p);
			break;
			default:
				ret = null;
				break;
		}
		return ret;
	}
}
