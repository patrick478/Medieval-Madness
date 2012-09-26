package common.entity;

import initial3d.engine.Vec3;

public class Player extends MovableEntity {
	
	public Player(Vec3 _radius){
		super(_radius);
		type = EntityType.Player;
	}

}
