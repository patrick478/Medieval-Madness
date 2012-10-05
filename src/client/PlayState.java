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
	private Player player = null;
	private Vec3 velocity = Vec3.zero;
	private Vec3 location = Vec3.zero;
	
	private Scene scene = new Scene();
	private MovableEntity ball = null;
	
	public PlayState() {
		// construct the scene
		// scene magic goes here

		ball = new Player(Vec3.zero, 1231231);
		FileInputStream fis;
		try {
			fis = new FileInputStream("ball.txt");
			ball.setMeshContexts(MeshLoader.loadComp261(fis));
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (Drawable d : ball.getMeshContexts()) {
			scene.addDrawable(d);
		}
		
		MovableReferenceFrame camera_rf = new MovableReferenceFrame(ball);
		scene.getCamera().trackReferenceFrame(camera_rf);
		camera_rf.setPosition(Vec3.create(-10, 10, -10));
		camera_rf.setOrientation(Quat.create(Math.PI / 8, Vec3.i));
		
		camera_rf.setOrientation(camera_rf.getOrientation().mul(Quat.create(Math.PI / 4, Vec3.j)));
		
//		scene.getCamera().trackReferenceFrame(ball);
	}

	@Override
	public Scene getScene(){
		System.out.println("Hello!");
		return scene;
	}
	
	@Override
	public void update(long updateTime, RenderWindow window) {
		if(window.getKey(KeyEvent.VK_UP))
		{
			this.velocity.setX(0.5);
			this.velocity.setZ(0.5);
		}
		else
			this.velocity = Vec3.zero;
		
		ball.updateMotion(ball.getPosition(), this.velocity, Quat.one, Vec3.zero, System.currentTimeMillis());
	}
}
