package game.states;

import game.Game;
import game.GameState;
import game.net.NetworkingClient;

/***
 * This state will display a list of games to join and allow a user to join a game
 * @author Ben
 *
 */
public class FindGameState extends GameState {
	public FindGameState(Game parent) {
		super(parent);
	}

	@Override
	public void initalise() {
		this.game.setNetwork(new NetworkingClient());
	}

	@Override
	public void update(double delta) {
	}

	@Override
	public void destroy() {		
	}	
}
