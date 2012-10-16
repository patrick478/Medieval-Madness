package game.event;

import game.Game;
import game.entity.Damageable;
import game.entity.Entity;
import java.util.List;

public class DeltaHealthEvent extends AbstractEvent {

	private int delta;
	
	public DeltaHealthEvent(int _delta){
		delta = _delta;
	}
	
	@Override
	protected boolean applyEvent(long _timeStamp, List<Entity> _trigger) {
		for(Entity e : _trigger){
			if(e instanceof Damageable){
				Game.getInstance().setEntityHealth(e.id, ((Damageable)e).getCurrentHealth() + delta);
			}
		}
		return true;
	}

}
