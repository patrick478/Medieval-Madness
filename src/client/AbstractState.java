package client;

import initial3d.engine.RenderWindow;
import initial3d.engine.Scene;

/**
 * @author "Ben Anderson (BageDevimo)"
 * A abstract class designed to help resent the state the client is currently attempting to execute/update/draw
 */
public abstract class AbstractState {
	protected Client client;
	protected Game gameWorld;
	protected Scene myScene = new Scene();
	
	/**
	 * Changes the current parent client state to the state specified in newState
	 * @param newState The state to be changed to
	 */
	public void changeState(AbstractState newState){
		this.client.setState(newState);
		
	}
	
	
	/**
	 * Sets the state up to be used by the game client in its look.
	 * @param pClient The parent client which contains the instance of AbstractState
	 * @param gameWorld The games current world state
	 */
	public void setup(Client pClient, Game gameWorld){
		this.client = pClient;
		this.gameWorld = gameWorld;
		
//		gameWorld.loadScene(myScene);
	}
	
	/**
	 * Gets the current scene for the state
	 * @return The state scene
	 */
	public Scene getScene(){
		return myScene;
	}
	
	
	/**
	 * Gets the current game for the state. The game for the state should always be the same as the main clients game
	 * @return The game from the state
	 */
	public Scene getGame(){
		return myScene;
	}
	
	/**
	 * Updates the state which gives the state the opportunity to make any changes to the scene such as updating any animations, etc.
	 * @param updateTime The number of milliseconds since update() was last called on ANY AbstractState
	 */
	public abstract void update(long updateTime, RenderWindow window);
}
