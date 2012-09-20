package client;

public abstract class AbstractState {
	protected Client client;
	protected Game gameWorld;
	protected Scene myScene; 
	
	public AbstractState()
	{
		
	}
	
	public void changeState(AbstractState newState)
	{
		this.client.setState(newState);
	}
	
	public void setup(Client pClient, Game gameWorld)
	{
		this.client = pClient;
		this.gameWorld = gameWorld;
	}
	
	public abstract void update(long updateTime);
	public abstract void fetchScene();
}
