package game.entity.moveable;

import game.bound.Bound;
import game.bound.BoundingSphere;
import game.entity.trigger.DynamicTriggerEntity;
import game.entity.trigger.TriggerEntity;
import game.event.PickupItemEvent;
import game.item.Item;
import game.level.Level;
import initial3d.engine.Vec3;

public class ItemEntity extends MoveableEntity{

	private final Bound bound;
	private final TriggerEntity trigger;
	
	public ItemEntity(long _id, Vec3 _position, double _radius, Item _item){
		super(_id);
		position = _position;
		bound = new BoundingSphere(_position, _radius);
		trigger = new DynamicTriggerEntity(new PickupItemEvent(_item), this);
	}
	
	public ItemEntity(Vec3 _position, double _radius, Item _item){
		super();
		position = _position;
		bound = new BoundingSphere(_position, _radius);
		trigger = new DynamicTriggerEntity(new PickupItemEvent(_item), this);
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
