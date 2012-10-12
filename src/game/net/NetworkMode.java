package game.net;

public abstract class NetworkMode
{	
	public void start()
	{
		this.modeStart();
	}
	protected abstract void modeStart();
	public abstract void destroy();
}
