package game.states;

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
	}
	
	@Override
	public void initalise() {
	}


	@Override
	public void update(double delta) {
		this.game.changeState(new MainMenuGUIState(this.game));
	}

	@Override
	public void destroy() {
	}

}
