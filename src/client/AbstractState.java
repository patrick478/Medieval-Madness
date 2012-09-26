package client;

import initial3d.engine.Scene;

public abstract class AbstractState {
	protected Client client;
	protected Game gameWorld;
	protected Scene myScene = new Scene();
	
	public AbstractState(){
	}
	
	public void changeState(AbstractState newState){
		this.client.setState(newState);
		
	}
	
	public void setup(Client pClient, Game gameWorld){
		this.client = pClient;
		this.gameWorld = gameWorld;
	}
	
	public Scene getScene(){
		return myScene;
	}
	
	public Scene getGame(){
		return myScene;
	}
	
	public abstract void update(long updateTime);
}
