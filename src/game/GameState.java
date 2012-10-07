package game;

public abstract class GameState
{
	protected Game game;
	public GameState(Game parent)
	{
		this.game = parent;
	}
	
	public abstract void initalise();
	public abstract void update(double delta);
	public abstract void destroy();
}
