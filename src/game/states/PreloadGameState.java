package game.states;

import game.Game;
import game.GameState;

/***
 * State for preloading and creating anything required to display the game menus
 * @author Ben
 *
 */
public class PreloadGameState extends GameState {
	public PreloadGameState() {
	}

	@Override
	public void update(double delta) {
		// TODO: lots of stuff to do with creating and loading assets required to create the window
		Game.getInstance().changeState(new IntroGameState());
	}

	@Override
	public void initalise() {
		Game.getInstance().createWindow();		
		
		// for now, just move straight to the intro game state
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}	
}
