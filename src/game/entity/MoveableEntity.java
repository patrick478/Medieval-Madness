package game.entity;

import initial3d.engine.Quat;
import initial3d.engine.Vec3;

public abstract class MoveableEntity extends Entity{

	protected Vec3 velocity = Vec3.zero;
	
	public Vec3 getVelocity(){
		return velocity;
	}
	
	public void setVelocity(Vec3 _vel){
		velocity = _vel;
	}
	
	public void setOrientation(Quat _dir){
		orientation = _dir;
	}
	
}
