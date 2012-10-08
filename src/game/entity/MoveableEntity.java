package game.entity;

import game.bound.Bound;
import initial3d.engine.Quat;
import initial3d.engine.Vec3;

public abstract class MoveableEntity extends Entity{

	//velocity here is interpreted as the movement per update(poke)
	protected Vec3 velocity = Vec3.zero;
	
	public Vec3 getVelocity(){
		return velocity;
	}

	public void setPosition(Vec3 _pos) {
		position = _pos;
	}
	
	public void setVelocity(Vec3 _vel){
		velocity = _vel;
	}
	
	public void setOrientation(Quat _dir){
		orientation = _dir;
	}
	
	/**
	 * Returns the bounding box of the next position of this 
	 * particular entity.
	 * 
	 * @return The bounding volume at the next position for 
	 * this movable entity
	 */
	public Bound getNextBound(){
		return getBound(position.add(velocity));
	}
}
