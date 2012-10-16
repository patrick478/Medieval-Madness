package game.event;

import game.Game;
import game.entity.Entity;
import game.entity.moveable.PlayerEntity;

import java.util.List;

public class EndLevelEvent extends AbstractEvent{

	public EndLevelEvent(){}
	
	@Override
	protected boolean applyEvent(long _timeStamp, List<Entity> _trigger) {
		for(PlayerEntity p : Game.getInstance().getPlayers()){
			if(!_trigger.contains(p)){
				return false;
			}
		}
		//TODO call endLevel here
		throw new UnsupportedException("need another method here");
//		return true;
	}
	
	private class UnsupportedException extends RuntimeException{
		public UnsupportedException(String s){
			super(s);
		}
	}

}
