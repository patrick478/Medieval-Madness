package game.GameStates;

import game.Game;
import game.GameState;

/*** 
 * The state before the actual game is played.
 * Will allow for changing gear, etc.
 * This is the state that is returned too inbetween levels.
 * @author Ben
 *
 */
public class LobbyState extends GameState {
	public LobbyState(Game parent) {
		super(parent);
	}

	@Override
	public void initalise() {
	}

	@Override
	public void update(double delta) {
	}

	@Override
	public void destroy() {
	}

}
