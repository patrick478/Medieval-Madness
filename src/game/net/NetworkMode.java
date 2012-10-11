package game.net;

import common.DataPacket;

import game.Game;

public abstract class NetworkMode
{	
	public void start()
	{
		this.modeStart();
	}
	protected abstract void modeStart();
	public abstract void destroy();
}
