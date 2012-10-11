package game;

import initial3d.engine.Scene;

public abstract class GameState
{
	protected Scene scene;
	
	public GameState()
	{
		this.scene = new Scene();
	}
	
	public abstract void initalise();
	public abstract void update(double delta);
	public abstract void destroy();
}