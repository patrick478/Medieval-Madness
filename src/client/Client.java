package client;

public class Client {
	
	//current state that determines actions
	private GameState state;
	
	public Client(){
		state = new LogInState();
	}
	
	public GameState getState(){
		return state;
	}
}
