package client.networking;

import initial3d.engine.Vec3;
import client.Game;
import client.PlayState;
import common.Packet;
import common.entity.EntityType;
import common.entity.EntityMode;
import common.entity.GameObject;
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
				{
					System.out.println("Logged in.");
					this.replyLine.nClient.client.setState(new PlayState());
				}
				else if(lp.isReply && !lp.loginOkay)
					System.out.println("Login failure");
				// TODO: Change the client state to gotLogin or some shit
			break;
			case EnterWorldPacket.ID:
				EnterWorldPacket ewp = (EnterWorldPacket)p;
				Game.getInstance().enterWorld(ewp.newWorld, ewp.playerEntity);
			break;
			case SegmentPacket.ID:
				SegmentPacket sp = (SegmentPacket)p;
				Game.getInstance().addTerrain(sp.segment);
//				System.out.println("Got segment packet from server");
			break;
			case EntityUpdatePacket.ID:
				EntityUpdatePacket eup = (EntityUpdatePacket)p;
				Game.getInstance().entityMoved(eup.entityID, eup.position, eup.velocity, eup.orientation, eup.angularVel, System.currentTimeMillis());
			break;
			case ChangeEntityModePacket.ID:
//				System.out.println("Holy fuck it worked!");
				ChangeEntityModePacket cemp = (ChangeEntityModePacket)p;
				switch(cemp.mode)
				{
					case Born:
						if(cemp.type == EntityType.Static)
							System.out.println("Adding new static entity");
						else if(cemp.type == EntityType.Moveable)
							System.out.println("Adding new moveable entity");
						else
							System.out.printf("Error: Unknown entity type for change entity mode packet: %s\n", cemp.type.toString());
					break;
				}
			break;
		}
	}
}
