package common.entity;

import initial3d.engine.Vec3;

public class BoundingBox {

	private final Vec3 position;
	private final Vec3 size;
	
	public BoundingBox()
	{
		// Just to shut eclipse up
		size = Vec3.create(0, 0, 0);
		position = Vec3.create(0, 0, 0);
	}
}
