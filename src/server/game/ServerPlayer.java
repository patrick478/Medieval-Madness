package server.game;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import server.session.Session;
import initial3d.engine.Vec3;
import common.Command;
import common.entity.Player;
import common.packets.EntityUpdatePacket;

public class ServerPlayer extends Player {

	public Session session;
	
	public Set<Command> activeCommands = new HashSet<Command>();
	
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
		
		PlayerManager.getInstance().notifyMoved(this);
		
		System.out.println("Sent teleport command");
	}
}
