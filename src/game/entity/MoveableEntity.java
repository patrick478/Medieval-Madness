package game.entity;

import game.bound.Bound;
import initial3d.engine.Quat;
import initial3d.engine.Vec3;

public abstract class MoveableEntity extends Entity{

	protected Vec3 linVelocity = Vec3.zero;
	protected Vec3 angVelocity = Vec3.zero;
	private long lastUpdate = 0;
	
	public MoveableEntity(long _id) {
		super(_id);
	}
	
	@Override
	public void poke(){
		updateMotion(getPosition(), linVelocity, getOrientation(), angVelocity, System.currentTimeMillis());
	}
	
	@Override
	public Vec3 getPosition(){
		return position.add(linVelocity.scale((System.currentTimeMillis()-lastUpdate)/1000d));
	}
	
	@Override
	public Quat getOrientation(){
		//TODO MYBEN GET HERE
		return orientation;
	}
	
	public Vec3 getLinVelocity(){
		return linVelocity;
	}
	
	public Vec3 getAngVelocity(){
		return angVelocity;
	}
	
	/**
	 * Stops the motion of the MoveableEntity
	 */
	public void fix(){
		linVelocity = Vec3.zero;
	}
	
	public void updateMotion(Vec3 _pos, Vec3 _linvel, Quat _orient, Vec3 _angvel, long _timeStamp){
		position = _pos;
		linVelocity = _linvel;
		orientation = _orient;
		angVelocity = _angvel;
		lastUpdate = _timeStamp;
	}
	
	/**
	 * Returns the bounding box of the next position of this 
	 * particular entity.
	 * 
	 * @return The bounding volume at the next position for 
	 * this movable entity
	 */
	public Bound getNextBound(){
		return getBound(getPosition());
	}
}
