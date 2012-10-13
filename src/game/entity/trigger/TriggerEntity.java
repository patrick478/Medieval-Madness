package game.entity.trigger;

import java.util.List;

import game.entity.Entity;
import game.event.AbstractEvent;

/**
 * A non-Solid Entity that's used to create objects triggered
 * by collisions that then enact events on the game world.
 * 
 * @author scottjosh
 *
 */
public abstract class TriggerEntity extends Entity{

	//the first event of a linked list of events
	protected final AbstractEvent event;
	
	public TriggerEntity(long _id, AbstractEvent _event) {
		super(_id);
		if(_event == null){
			throw new IllegalArgumentException("Event cannot be null when creating a TriggerEntity");
		}
		event = _event;
	}
	
	public TriggerEntity( AbstractEvent _event) {
		super();
		if(_event == null){
			throw new IllegalArgumentException("Event cannot be null when creating a TriggerEntity");
		}
		event = _event;
	}
	
	@Override
	public void poke() {}
	
	@Override
	public boolean isSolid() {
		return false;
	}
	
	/**
	 * Trigger and enact the list of events onto the game world.
	 * 
	 * @param _trigger the list of entities that triggered the event. Cannot be null but may be empty
	 */
	public void trigger(List<Entity> _trigger){
		event.activate(System.currentTimeMillis(), _trigger);
	}
	
	/**
	 * Adds to the end of the  list of events that are called when this
	 * TriggerEntity is triggered. If event is null, does nothing.
	 * 
	 * @param _event The event to be added to the end of the list
	 */
	public void addEvent(AbstractEvent _event){
		if(_event!=null){
			event.addEvent(_event);
		}
	}
}
