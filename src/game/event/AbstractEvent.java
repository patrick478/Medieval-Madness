package game.event;

import game.Game;
import game.entity.Entity;
import game.entity.trigger.TriggerEntity;

public abstract class AbstractEvent {

	private AbstractEvent nextEvent = null;
	
	/**
	 * Adds an event to the end of the list of events.
	 * 
	 * @param _event the event to add to the end of the list.
	 */
	public void addEvent(AbstractEvent _event){
		if(nextEvent==null){
			nextEvent = _event;
		}else{
			nextEvent.addEvent(_event);
		}
	}
	
	/**
	 * Applies the event to the game. If there is another event
	 * after this one in the list, calls activate on that event.
	 * 
	 * @param _timeStamp the time in which the original event was triggered
	 * @param _trigger the entity that triggered the event
	 */
	public void activate(long _timeStamp, Entity _trigger){
		applyEvent(_timeStamp, _trigger);
		if(nextEvent!=null){
			nextEvent.activate(_timeStamp, _trigger);
		}
	}
	
	
	protected abstract void applyEvent(long _timeStamp, Entity _trigger);
}
