package game.net;

import game.Game;

public abstract class NetworkMode
{
	protected Game game = null;
	
	public void start(Game g)
	{
		this.game = g;
		this.modeStart();
	}
	public abstract void modeStart();
	public abstract void destroy();
}
