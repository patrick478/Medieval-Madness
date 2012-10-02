package common.entity;

import initial3d.engine.Vec3;

public class NPC extends MovableEntity{

	public NPC(Vec3 _radius, long id){
		super(_radius, id);
		type = EntityType.NPC;
	}
}