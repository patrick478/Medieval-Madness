package common.entity;

import initial3d.engine.Quat;
import initial3d.engine.Vec3;

public abstract class MovableEntity extends Entity{

	protected long lastUpdate = 0;//TODO world time?
	protected Vec3 linVelocity = Vec3.zero; //linear velocity
	protected Vec3 angVelocity = Vec3.zero; //angular velocity
	protected Vec3 intVelocity = Vec3.zero; //intended velocity
	
	public MovableEntity(Vec3 _radius, long id) {
		super(_radius, id);
	}
	
	public Vec3 getLinearVelocity(){
		return linVelocity;
	}
	
	public Vec3 getAngularVelocity(){
		return angVelocity;
	}
	
	public Vec3 getIntendedVelocity(){
		return intVelocity;
	}
	
	@Override
	public BoundingBox getBound() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Quat getOrientation() {
		// TODO Auto-generated method stub
		//(MY)BENS JOB
		return orientation;
	}

	@Override
	public Vec3 getPosition() {
		//TODO change to incorperate 
		//change in position via, 
		//timestamp and velocity
		return position.add(linVelocity.scale((System.currentTimeMillis()-lastUpdate)/1000d));
	}
	
	public void updateMotion(Vec3 _position, Vec3 _linVelocity, 
			Quat _orientation, Vec3 _angVelocity, long timeStamp){
		position = _position;
		linVelocity = _linVelocity;
		orientation = _orientation;
		angVelocity = _angVelocity;
		lastUpdate = timeStamp;
	}
	
	public void updateIntendedVelocity(Vec3 _intVelocity){
		intVelocity = _intVelocity;
//		this.updateMotion(_position, _linVelocity, _orientation, _angVelocity, timeStamp)
	}
}
