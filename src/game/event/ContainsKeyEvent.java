package game.event;

import game.entity.Entity;
import game.entity.moveable.PlayerEntity;
import game.item.Key;

import java.util.List;

public class ContainsKeyEvent extends AbstractEvent{

	private final Key keyItem;
	
	public ContainsKeyEvent(Key _key){
		keyItem = _key;
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
