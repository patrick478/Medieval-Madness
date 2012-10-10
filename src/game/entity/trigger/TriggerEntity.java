package game.entity.trigger;

import game.Game;
import game.entity.Entity;
import game.event.AbstractEvent;

public abstract class TriggerEntity extends Entity{

	protected final Game game;
	protected final AbstractEvent event;
	
	public TriggerEntity(long _id, Game _game, AbstractEvent _event) {
		super(_id);
		game = _game;
		event = _event;
	}
	
	@Override
	public void poke() {}
	
	@Override
	public boolean isSolid() {
		return false;
	}
	
	public void trigger(Entity _trigger){
		event.activate(System.currentTimeMillis(), _trigger);
	}
	
	public void addEvent(AbstractEvent _event){
		event.addEvent(_event);
	}
}
