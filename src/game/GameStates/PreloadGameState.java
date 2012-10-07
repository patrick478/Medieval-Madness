package game.GameStates;

import game.Game;
import game.GameState;

/***
 * State for preloading and creating anything required to display the game menus
 * @author Ben
 *
 */
public class PreloadGameState extends GameState {
	public PreloadGameState(Game parent) {
		super(parent);
	}

	@Override
	public void update(double delta) {
		// TODO: lots of stuff to do with creating and loading assets required to create the window
		this.game.changeState(new IntroGameState(this.game));
	}

	@Override
	public void initalise() {
		this.game.createWindow();		
		
		// for now, just move straight to the intro game state
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}	
}
