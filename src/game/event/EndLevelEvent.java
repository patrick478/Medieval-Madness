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
		throw new UnsupportedFaggotException("need another method here");
//		return true;
	}
	
	private class UnsupportedFaggotException extends RuntimeException{
		public UnsupportedFaggotException(String s){
			super(s);
		}
	}

}
