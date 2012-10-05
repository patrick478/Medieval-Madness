package server.game;

import server.session.Session;
import initial3d.engine.Vec3;
import common.entity.Player;

public class ServerPlayer extends Player {

	public Session session;
	public ServerPlayer(Vec3 _radius, long id) {
		super(_radius, id);
	}
	
	public void setPosition(Vec3 pos)
	{
		this.position = pos;
	}
}
