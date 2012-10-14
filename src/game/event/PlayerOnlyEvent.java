package game.event;

import game.entity.Entity;
import game.entity.moveable.PlayerEntity;

import java.util.Iterator;
import java.util.List;

public class PlayerOnlyEvent extends AbstractEvent{

	@Override
	protected boolean applyEvent(long _timeStamp, List<Entity> _trigger) {
		Iterator<Entity> i = _trigger.iterator();
		while(i.hasNext()){
			Entity e = i.next();
			if(!(e instanceof PlayerEntity)){
				i.remove();
			}
		}
		return !_trigger.isEmpty();
	}

}
