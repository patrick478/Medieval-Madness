package game.entity;

import game.bound.Bound;
import initial3d.engine.Vec3;

public class ProjectileEntity extends MoveableEntity {

	public ProjectileEntity(long _id, Vec3 _pos){
		super(_id);
		position = _pos;
	}

	@Override
	public void poke() {
		
	}

	@Override
	protected Bound getBound(Vec3 position) {
		return null;
	}

	@Override
	public boolean isSolid() {
		return false;
	}
}
