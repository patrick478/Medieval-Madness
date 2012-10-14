package game.event;

import game.Game;
import game.entity.Entity;
import game.entity.moveable.*;

import java.util.List;

public class DamageEvent extends AbstractEvent {

	public DamageEvent()
	{
		
	}
	
	@Override
	protected boolean applyEvent(long _timeStamp, List<Entity> _trigger) {
		for(Entity e : _trigger)
		{
			if(e instanceof PlayerEntity)
			{
				Game.getInstance().deltaEntityHealth((PlayerEntity) e, -25);
			}
		}
		return true;
	}

}
