package game.event;

import game.Game;
import game.entity.Entity;
import game.entity.trigger.TriggerEntity;

public abstract class AbstractEvent {

	protected final Game game;
	private AbstractEvent nextEvent = null;
	
	public AbstractEvent(Game _game){
		game = _game;
	}
	
	public void addEvent(AbstractEvent _event){
		if(nextEvent==null){
			nextEvent = _event;
		}else{
			nextEvent.addEvent(_event);
		}
	}
	
	public void activate(long _timeStamp, Entity _trigger){
		applyEvent(_timeStamp, _trigger);
		if(nextEvent!=null){
			nextEvent.activate(_timeStamp, _trigger);
		}
	}
	
	protected abstract void applyEvent(long _timeStamp, Entity _trigger);
}
