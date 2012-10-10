package game.entity;

import game.bound.Bound;
import game.floor.Level;
import initial3d.engine.Vec3;

public abstract class EventEntity extends Entity{

	protected final Level level;
	private boolean active;
	
	public EventEntity(long _id, Level _level) {
		super(_id);
		level = _level;
	}

	@Override
	public void poke(){
		if(active){
			level.firstCollision(getBound());
		}
	}
	
	@Override
	public boolean isSolid() {
		return false;
	}
	
	public void activate(Entity e){
		active = true;
	}
	
	protected abstract void entityEntered(Entity e);
	protected abstract void entityExited(Entity e);
}
