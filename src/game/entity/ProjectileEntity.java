package game.entity;

import game.bound.Bound;
import initial3d.engine.Vec3;

public class ProjectileEntity extends MoveableEntity {

	public ProjectileEntity(Vec3 _start, Vec3 _vel){
		position = _start;
	}

	@Override
	public void poke() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Bound getBound(Vec3 position) {
		// TODO Auto-generated method stub
		return null;
	}
}
