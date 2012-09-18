package client;

public abstract class GameState {
	protected Client client;
	public GameState()
	{
	}
	
	public void changeState(GameState newState)
	{
		this.client.setState(newState);
	}
	
	public void setup(Client pClient)
	{
		this.client = pClient;
	}
	
	public abstract void update(long sinceLast);
	public abstract void draw();
}
