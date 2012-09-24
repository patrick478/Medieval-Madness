package client.networking;

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
		switch(p.ID())
		{
			case WelcomePacket.ID:
				reply1 = new WelcomePacket();
				reply1.isReply = true;
				replyLine.send(reply1.toData());
				this.replyLine.isConnected = true;
			break;
			case LoginPacket.ID:
				LoginPacket lp = (LoginPacket)p;
				if(lp.isReply && lp.loginOkay)
					System.out.println("Logged in. Awaiting available character selections..");
				// TODO: Change the client state to gotLogin or some shit
			break;
		}
	}
}
