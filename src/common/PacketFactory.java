package common;

import common.packets.*;

public class PacketFactory
{
	public static Packet identify(DataPacket p)
	{
		Packet ret;
		switch(p.peekShort())
		{
			case WelcomePacket.ID:
				ret = new WelcomePacket();
				break;
				
			case LoginPacket.ID:
				ret = new LoginPacket();
				break;
				
			case SegmentPacket.ID:
				ret = new SegmentPacket();
				break;
				
			case EnterWorldPacket.ID:
				ret = new EnterWorldPacket();
				break;
				
			case ChangeEntityModePacket.ID:
				ret = new ChangeEntityModePacket();
				
			case EntityUpdatePacket.ID:
				ret = new EntityUpdatePacket();
				
			default:
				System.out.printf("[CRITICAL - UN_ID_PACKET] undef_recv_id = %d\n", p.peekShort());
				return null;
		}
		ret.fromData(p);
		return ret;
	}
}
