package game.entity.moveable;

import java.util.List;

import game.entity.Damageable;

public abstract class EnemyEntity extends MoveableEntity implements Damageable{
	
	public EnemyEntity(long _id) {
		super(_id);
	}
	
	@Override
	public void poke() {
		super.poke();
	}

	@Override
	public boolean isSolid() {
		return true;
	}
}
