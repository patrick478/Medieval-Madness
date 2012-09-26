package common.entity;

import initial3d.engine.Vec3;

public class NPC extends MovableEntity{

	public NPC(Vec3 _radius){
		super(_radius);
		type = EntityType.NPC;
	}
}