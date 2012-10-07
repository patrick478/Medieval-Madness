package game.GameStates;

import game.Game;
import game.GameState;

/***
 * This state is for showing pre-menu credits, etc, we can just skip over it for now - its time dependant.
 * @author Ben
 *
 */
public class IntroGameState extends GameState {

	public IntroGameState(Game parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(double delta) {
		this.game.changeState(new MainMenuState(this.game));
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
