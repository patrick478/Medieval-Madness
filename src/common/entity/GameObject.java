package common.entity;

import initial3d.engine.Quat;
import initial3d.engine.ReferenceFrame;
import initial3d.engine.Vec3;

public class GameObject extends Entity {

	public GameObject(Vec3 _radius, Vec3 _position, Quat _orientation){
		super(_radius);
		type = EntityType.GameObject;
		position = _position;
		orientation = _orientation;
	}
}
