package game.entity;

import game.bound.Bound;
import game.bound.BoundingBox;
import initial3d.engine.Vec3;

public class WallEntity extends Entity {
	
	private static final Vec3 wallRadius = Vec3.create(0.5, 0.5, 0.5);//TODO even need this?
	private final Bound bound; 
	
	public WallEntity(long _id, Vec3 _pos){
		super(_id);
		position = _pos;
		bound = new BoundingBox(_pos, wallRadius);
//		bound = new BoundingSphere(_pos, 0.5);
	}
	
	@Override
	public void poke() {}

	@Override
	protected Bound getBound(Vec3 position) {
		return bound;
	}

	@Override
	public boolean isSolid() {
		return true;
	}
}
