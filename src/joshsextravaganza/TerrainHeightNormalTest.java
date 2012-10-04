package joshsextravaganza;

import java.io.FileInputStream;
import java.io.IOException;

import common.entity.MovableEntity;
import common.entity.Player;
import common.map.Segment;
import common.map.SegmentGenerator;
import comp261.modelview.MeshLoader;

import initial3d.engine.MovableReferenceFrame;
import initial3d.engine.Quat;
import initial3d.engine.RenderWindow;
import initial3d.engine.Scene;
import initial3d.engine.SceneManager;
import initial3d.engine.Vec3;

public class TerrainHeightNormalTest {
	public static void main(String[] args) throws IOException, InterruptedException {
		final int WIDTH = 848;
		final int HEIGHT = 480;
		final long SEED = 32; 
		
		//create window and scene
		RenderWindow rwin = RenderWindow.create(WIDTH, HEIGHT);

		SceneManager sman = new SceneManager(WIDTH, HEIGHT);
		sman.setDisplayTarget(rwin);
		rwin.setLocationRelativeTo(null);
		rwin.setVisible(true);

		Scene scene = new Scene();
		
		//create terrain
		SegmentGenerator sg = new SegmentGenerator(SEED);
		
		
		//create a  moving ball
		MovableEntity ball = new Player(Vec3.zero, 1);
		FileInputStream fis = new FileInputStream("ball.txt");
		ball.setMeshContexts(MeshLoader.loadComp261(fis));
		fis.close();
		ball.updateMotion(Vec3.create(0, 0.5, 0), Vec3.create(1, 0, 1), Quat.one, Vec3.zero, System.currentTimeMillis());
		
		//use camera to track
		MovableReferenceFrame camera_rf = new MovableReferenceFrame(ball);
		scene.getCamera().trackReferenceFrame(camera_rf);
		camera_rf.setPosition(Vec3.create(-10, 10, -10));
		camera_rf.setOrientation(Quat.create(Math.PI / 8, Vec3.i));
		camera_rf.setOrientation(camera_rf.getOrientation().mul(Quat.create(Math.PI / 4, Vec3.j)));
		
		
		//set scene and attach
		scene.addDrawables(ball.getMeshContexts());
		sman.attachToScene(scene);
		
		Segment current = sg.getSegment(0, 0);
		scene.addDrawable(current.getMeshContext());
		
		while(true){
			if(!current.contains(ball.getPosition().x, ball.getPosition().z)){
				scene.removeDrawable(current.getMeshContext());
				current = sg.segmentAt(ball.getPosition().x, ball.getPosition().z);
				scene.addDrawable(current.getMeshContext());
			}
			Thread.sleep(1000);
			
			//new vec direction
			double xPos = ball.getPosition().x;
			double zPos = ball.getPosition().z;
			
			Vec3 vel = Vec3.create(1, 0, 1).add(current.getNormal(xPos, zPos)).unit();
			
			
			
			ball.updateMotion(Vec3.create(xPos, current.getHeight(xPos, zPos)+0.5, zPos), 
					vel.scale(Vec3.create(1, 0, 1).mag()), Quat.one, Vec3.zero, System.currentTimeMillis());
		}
		
	}
}
