package game.GameStates;

import game.Game;
import game.GameState;

/***
 * Pretty obvious, this is the main menu state.
 * 
 * From this state, the user chooses to create a game, or join an existing game. It should switch to the corrosponding state.
 * @author Ben
 *
 */
public class MainMenuState extends GameState {
	public MainMenuState(Game parent) {
		super(parent);
	}

	@Override
	public void update(double delta) {
	}

	@Override
	public void initalise() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}	
}
