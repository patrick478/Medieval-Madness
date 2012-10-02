package client.networking;

import client.Game;
import common.Packet;
import common.packets.*;

public class NetworkHandler {
	private NetworkThread replyLine;
	
	public NetworkHandler(NetworkThread nt)
	{
		this.replyLine = nt;
	}
	
	public void passoff(Packet p)
	{
		if(p == null) return;
		
		Packet reply1;
//		System.out.printf("Got packet with the ID of %d\n", p.ID());
		switch(p.ID())
		{
			case WelcomePacket.ID:
				reply1 = new WelcomePacket();
				reply1.isReply = true;
				reply1.toData().printPacket();
				replyLine.send(reply1.toData());
				this.replyLine.isConnected = true;
			break;
			case LoginPacket.ID:
				LoginPacket lp = (LoginPacket)p;
				if(lp.isReply && lp.loginOkay)
					System.out.println("Logged in.");
				else if(lp.isReply && !lp.loginOkay)
					System.out.println("Login failure");
				// TODO: Change the client state to gotLogin or some shit
			break;
			case EnterWorldPacket.ID:
				EnterWorldPacket ewp = (EnterWorldPacket)p;
				Game.getInstance().enterWorld(ewp.newWorld);
			break;
			case SegmentPacket.ID:
				SegmentPacket sp = (SegmentPacket)p;
				Game.getInstance().addTerrain(sp.segment);
//				System.out.println("Got segment packet from server");
			break;
			case ChangeEntityModePacket.ID:
				System.out.println("Holy fuck it worked!");
			break;
		}
	}
}
