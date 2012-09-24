package client;

import initial3d.engine.Engine;
import client.networking.*;
import common.Log;

public class Client {
	private AbstractState gameState = null;
	public Log log;
	public NetworkClient net;
	private Engine eng;
	private Game gameWorld;
	
	protected boolean running = false;

	private final int updatesPerSecond = 60;
	
	public Client()
	{
		this.log = new Log("client.log", true, System.out);
		this.net = new NetworkClient(this);
	}
	
	public void setState(AbstractState newState)
	{
		this.log.printf("{GAMESTATE} Changing gamestate to <%s>\n", newState.toString());
		newState.setup(this, this.gameWorld);
		this.gameState = newState;
	}
	
	public void run() {
		this.setState(new StartupState());
		this.running = true;

		long maxTimePerUpdate = (1000 / updatesPerSecond);
		long lastStartTime = System.currentTimeMillis();
		long elapsedTimeSinceLastUpdate = System.currentTimeMillis();

		while(this.running)
		{
			elapsedTimeSinceLastUpdate = System.currentTimeMillis() - lastStartTime;
			lastStartTime = System.currentTimeMillis();

			this.gameState.update(elapsedTimeSinceLastUpdate);

			long currentTimeForTick = System.currentTimeMillis() - lastStartTime;
			if(currentTimeForTick < maxTimePerUpdate)
			{
				try
				{
					Thread.sleep((long) (maxTimePerUpdate - currentTimeForTick));
				}
				catch(Exception e)
				{
					System.out.printf("{ERROR} %s", e.toString());
				}
			}
			else
				this.log.printf("Client isn't updating() fast enough. This is a problem. Last update() took: %d. maxTime=%d\n", currentTimeForTick, maxTimePerUpdate);
		}
	}
}
