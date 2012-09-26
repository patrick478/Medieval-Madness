package client;

import common.entity.Player;

import initial3d.engine.Scene;

public class PlayState extends AbstractState {
	private Player player = null;
	
	public PlayState() {
		// construct the scene
		// scene magic goes here
		gameWorld.getScene().getCamera().trackReferenceFrame(player);
		
	}

	@Override
	public Scene getScene(){
		return gameWorld.getScene();
	}
	
	@Override
	public void update(long updateTime) {
		// TODO Auto-generated method stub

	}
}
