package common.entity;

import initial3d.engine.Vec3;

public abstract class MovableEntity extends Entity{

	protected Vec3 velocity;
	
	public Vec3 getVelocity(){
		return velocity;
	}
	
}
