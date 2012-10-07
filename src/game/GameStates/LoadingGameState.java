package game.GameStates;

import game.Game;
import game.GameState;

/***
 * This is the state required to load any character models, etc before playing the actual game
 * @author Ben
 *
 */
public class LoadingGameState extends GameState {
	public LoadingGameState(Game parent) {
		super(parent);
	}

	@Override
	public void initalise() {
	}

	@Override
	public void update(double delta) {
		// for now - no loading
		this.game.changeState(new PlayState(this.game));
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}	
}
