package game.event;

import game.entity.Entity;
import game.entity.moveable.PlayerEntity;
import game.item.Item;

import java.util.List;

public class ContainsItemEvent extends AbstractEvent{

	private final Item keyItem;
	
	public ContainsItemEvent(Item _item){
		keyItem = _item;
	}
	
	@Override
	protected boolean applyEvent(long _timeStamp, List<Entity> _trigger) {
		for(Entity e :_trigger){
			if(e instanceof PlayerEntity){
				if(((PlayerEntity)e).getInventory().containsItem(keyItem)){
					return true;
				}
			}
		}
		return false;
	}

}
