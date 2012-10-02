package common.entity;

import server.session.Session;
import initial3d.engine.Vec3;

public class Player extends MovableEntity {
	
	public int segmentX = 0;
	public int segmentZ = 0;
	public int world = 0;
	
	public Player(Vec3 _radius, long id){
		super(_radius, id);
		type = EntityType.Player;
	}

}
