package game.entity.moveable;

import game.bound.Bound;
import initial3d.engine.Vec3;

public class EnemyEntity extends MoveableEntity {

	
	//TODO implement
	public EnemyEntity(long _id) {
		super(_id);
	}

	@Override
	protected Bound getBound(Vec3 position) {
		return null;
	}

	@Override
	public boolean isSolid() {
		return true;
	}
}
