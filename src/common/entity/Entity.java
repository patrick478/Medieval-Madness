package common.entity;

import initial3d.engine.Vec3;

public abstract class Entity {
	
	//position of the very center of the entity much like
	//the center of mass if mass was evenly distributed
	protected Vec3 position;
	
	//radius to determine the bounds (box and sphere)
	//y-component determines half the height of the entity
	protected Vec3 radius;
	protected EntityType type;
	
	public abstract BoundingBox getBound();
	
	public Vec3 getPosition(){
		return position;
	}
}
