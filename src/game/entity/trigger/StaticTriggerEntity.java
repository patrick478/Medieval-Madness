package game.entity.trigger;

import initial3d.engine.Vec3;
import game.Game;
import game.bound.Bound;
import game.bound.BoundingBox;
import game.bound.BoundingSphere;
import game.event.AbstractEvent;

public class StaticTriggerEntity extends TriggerEntity{

	private final Bound bound;
	
	public StaticTriggerEntity(long _id, AbstractEvent _event, Bound _bound) {
		super(_id, _event);
		bound = _bound;
	}

	@Override
	protected Bound getBound(Vec3 position) {
		return bound;
	}
}
