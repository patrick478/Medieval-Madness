package game.event;

import game.Game;
import game.entity.Entity;
import game.entity.moveable.PlayerEntity;

import java.util.List;

public class LevelFinishEvent extends AbstractEvent {

	@Override
	protected boolean applyEvent(long _timeStamp, List<Entity> _trigger) {
		for(PlayerEntity p : Game.getInstance().getPlayers()){
			if(!_trigger.contains(p)){
				return false;
			}
		}
		Game.getInstance().finishLevel();
		return true;
	}
}
