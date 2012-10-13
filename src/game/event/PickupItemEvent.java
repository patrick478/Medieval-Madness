package game.event;

import game.Game;
import game.entity.Entity;
import game.entity.moveable.PlayerEntity;
import game.item.Item;

public class PickupItemEvent extends AbstractEvent{

	private final Item item;
	
	public PickupItemEvent(Item _item){
		if(_item == null){
			throw new IllegalArgumentException("Cannot have a null item for a PickupItemEvent");
		}
		item = _item;
	}
	
	@Override
	protected boolean applyEvent(long _timeStamp, Entity _trigger) {
		if(_trigger instanceof PlayerEntity){
			Game.getInstance().addItemToPlayer(_trigger.id, item);
			return true;
		}
		return false;
	}
}
