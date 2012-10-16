package game.entity.moveable;

import game.bound.Bound;
import game.bound.BoundingSphere;
import game.entity.Entity;
import game.entity.trigger.DynamicTriggerEntity;
import game.entity.trigger.TriggerEntity;
import game.event.PickupItemEvent;
import game.event.PlayerOnlyEvent;
import game.event.RemoveEntityEvent;
import game.item.Item;
import game.level.Level;
import initial3d.engine.Vec3;

public class ItemEntity extends MoveableEntity{

	private static final double ITEM_RADIUS = 0.25;
	private final Bound bound;
	private final TriggerEntity trigger;
	
	public ItemEntity(long _id, Vec3 _position, Item _item){
		super(_id);
		position = _position;
		bound = new BoundingSphere(_position, ITEM_RADIUS);
		angVelocity = Vec3.create(0, 1.5, 0);
		trigger = new DynamicTriggerEntity(Entity.freeID(), new PlayerOnlyEvent(), this);
		trigger.addEvent(new RemoveEntityEvent(this.id));
		trigger.addEvent(new RemoveEntityEvent(trigger.id));
		trigger.addEvent(new PickupItemEvent(_item));
	}
	
	@Override
	public void addToLevel(Level _l){
		super.addToLevel(_l);
		trigger.addToLevel(_l);
	}
	
	@Override
	public boolean isSolid() {
		return false;
	}

	@Override
	protected Bound getBound(Vec3 position) {
		return bound;
	}

}
