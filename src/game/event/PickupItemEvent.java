package game.event;

import java.util.List;

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
	protected boolean applyEvent(long _timeStamp, List<Entity> _trigger) {
		for(Entity e : _trigger){
			if(e instanceof PlayerEntity){
				PlayerEntity p = (PlayerEntity)e;
				if(!p.getInventory().isFull()){
					Game.getInstance().addItemToPlayer(e.id, item.id);
					return true;
				}
			}
		}
		return false;
	}
}
