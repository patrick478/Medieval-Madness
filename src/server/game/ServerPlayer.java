package server.game;

import server.session.Session;
import initial3d.engine.Vec3;
import common.entity.Player;
import common.packets.EntityUpdatePacket;

public class ServerPlayer extends Player {

	public Session session;
	public ServerPlayer(Vec3 _radius, long id) {
		super(_radius, id);
	}
	
	public void setPosition(Vec3 pos)
	{
		this.position = pos;
	}

	public void teleportTo(Vec3 target) {
		this.position = target;
		EntityUpdatePacket up = new EntityUpdatePacket();
		
		up.entityID = id;
		up.position = target;
		session.send(up);
		
		System.out.println("Sent teleport command");
	}
}
