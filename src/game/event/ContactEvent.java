package game.event;

import java.util.List;

import game.entity.Entity;

public class ContactEvent extends AbstractEvent {

	public ContactEvent(){}
	
	@Override
	protected boolean applyEvent(long _timeStamp, List<Entity> _trigger) {
		for(Entity e : _trigger){
			if(e.isSolid()) return true;
		}
		return false;
	}

}
