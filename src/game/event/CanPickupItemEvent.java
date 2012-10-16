package game.event;

import game.entity.Entity;
import game.entity.moveable.PlayerEntity;
import game.item.Item;

import java.util.List;

public class CanPickupItemEvent extends AbstractEvent{

	private final Item item;
	
	public CanPickupItemEvent(Item _item){
		item = _item;
	}
	
	@Override
	protected boolean applyEvent(long _timeStamp, List<Entity> _trigger) {
		for(Entity e : _trigger){
			if(e instanceof PlayerEntity){
				PlayerEntity p = (PlayerEntity)e;
				if(!p.getInventory().isFull() 
						&& !p.getInventory().containsItem(item)){
					_trigger.clear();
					_trigger.add(e);
					return true;
				}
			}
		}
		return false;
	}
}
