package game.entity;

import game.bound.Bound;
import initial3d.engine.Quat;
import initial3d.engine.Vec3;

public abstract class MoveableEntity extends Entity{

	//velocity here is interpreted as the movement per update(poke)
	protected Vec3 velocity = Vec3.zero;
	protected long lastVelUpdate = System.currentTimeMillis();
	
	public Vec3 getVelocity(){
		return velocity;
	}

	public void setPosition(Vec3 _pos) {
		position = _pos;
	}
	
	// REALLY BEN!?!
	@Override
	public Vec3 getPosition()
	{
		this.position = position.add(this.velocity.scale((System.currentTimeMillis()-lastVelUpdate)/1000d));
		return this.position;
	}
	
	public void setVelocity(Vec3 _vel){
		this.lastVelUpdate = System.currentTimeMillis();
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
