package game;

import initial3d.engine.Scene;

public abstract class GameState
{
	protected Game game;
	protected Scene scene;
	
	public GameState(Game parent)
	{
		this.game = parent;
		this.scene = new Scene();
	}
	
	public abstract void initalise();
	public abstract void update(double delta);
	public abstract void destroy();
}