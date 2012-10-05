package client;

import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;

import common.entity.MovableEntity;
import common.entity.Player;
import comp261.modelview.MeshLoader;

import initial3d.engine.Drawable;
import initial3d.engine.MovableReferenceFrame;
import initial3d.engine.Quat;
import initial3d.engine.RenderWindow;
import initial3d.engine.Scene;
import initial3d.engine.Vec3;

public class PlayState extends AbstractState {
	private Vec3 velocity = Vec3.zero;
	private Vec3 location = Vec3.zero;
	private float speed = 0.006f;
	
	private Scene scene = new Scene();
	
	public PlayState() {
		this.gameWorld.getInstance().loadScene(scene);
	}

	@Override
	public Scene getScene(){
		return scene;
	}
	
	@Override
	public void update(long updateTime, RenderWindow window) {
	}
}
