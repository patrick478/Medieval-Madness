package game.states;

import java.util.*;
import game.Game;
import game.GameState;
import game.modelloader.Content;
import game.modelloader.ContentRequest;

/***
 * This is the state required to load any character models, etc before playing the actual game
 * @author Ben
 *
 */
public class LoadingGameState extends GameState implements ContentRequest {
	public LoadingGameState(Game parent) {
		super(parent);
	}
	
	private List<String> waitingOn = new ArrayList<String>();
	private String[] models = new String[] {
			"cube.obj",
			"sphere.obj",
			"human.obj",
			"plane.obj",
			"trumpet.obj"
	};

	@Override
	public void initalise() {
		for(int i = 0; i < models.length; i++)
		{
			this.waitingOn.add(models[i]);
			Content.preloadContent(models[i], this);
		}
		System.out.println("Loading content..");
	}

	@Override
	public void update(double delta) {
		double loadProgress = Math.ceil((this.waitingOn.size() / models.length) * 100f);
		// for now - no loading
		if(this.waitingOn.size() == 0)
		{
			this.game.changeState(new PlayState(this.game));
			System.out.println("Finished waiting for content");
		}
		else
			System.out.printf("Loading %.2f complete..\n", loadProgress);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadComplete(String filename) {
		this.waitingOn.remove(filename);
	}	
}
