package game.event;

import game.entity.Entity;

import java.util.Iterator;
import java.util.List;

public class AvoidEvent extends AbstractEvent{

	private final long entity_avoid;
	
	public AvoidEvent(long _eid){
		entity_avoid = _eid;
	}
	
	@Override
	protected boolean applyEvent(long _timeStamp, List<Entity> _trigger) {
		Iterator<Entity> i = _trigger.iterator();
		while(i.hasNext()){
			Entity e = i.next();
			if(e.id==entity_avoid){
				i.remove();
			}
		}
		return true;
	}

}
