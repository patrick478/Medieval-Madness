package game.entity.trigger;

import initial3d.engine.Vec3;
import game.Game;
import game.bound.Bound;
import game.entity.Entity;
import game.event.AbstractEvent;

public class DynamicTriggerEntity extends TriggerEntity{

	private Entity parent;
	
	public DynamicTriggerEntity(long _id, Game _game, AbstractEvent _event, Entity _parent) {
		super(_id, _game, _event);
		if(_parent==null){
			throw new IllegalArgumentException("Cannot acceept null as a 'Parent' Entity");
		}
		parent = _parent;
	}

	@Override
	protected Bound getBound(Vec3 position) {
		return parent.getBound();
	}
}
