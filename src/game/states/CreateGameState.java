package game.states;

import game.Game;
import game.GameState;

/***
 * The create-a-game state, should show all the options then when "next" is pressed, goto the LobbyState.
 * @author Ben
 *
 */
public class CreateGameState extends GameState {
	public CreateGameState(Game parent) {
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
