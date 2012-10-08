package game.states;

import game.Game;
import game.GameState;
import game.net.NetworkingClient;
import game.net.NetworkingHost;

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
		System.out.printf("Creating game..\n");
		this.game.setHost(new NetworkingHost());
		this.game.setNetwork(new NetworkingClient());
	}

	@Override
	public void update(double delta) {
	}

	@Override
	public void destroy() {
	}	
}
