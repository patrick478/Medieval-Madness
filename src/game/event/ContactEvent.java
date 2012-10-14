package game.event;

import java.util.Iterator;
import java.util.List;

import game.entity.Entity;

public class ContactEvent extends AbstractEvent {

	public ContactEvent(){}
	
	@Override
	protected boolean applyEvent(long _timeStamp, List<Entity> _trigger) {
		Iterator<Entity> i = _trigger.iterator();
		while(i.hasNext()){
			Entity e = i.next();
			if(!e.isSolid()){
				i.remove();
			}
		}
		return !_trigger.isEmpty();
	}

}
