package game.states;

import java.awt.event.KeyEvent;

import initial3d.engine.*;
import game.Game;
import game.GameState;
import game.bound.BoundingSphere;
import game.entity.Entity;
import game.entity.PlayerEntity;
import game.entity.WallEntity;
import game.floor.Floor;
import game.floor.FloorGenerator;

/***
 * The game!
 * @author Ben
 *
 */
public class PlayState extends GameState {	
	public PlayState(Game parent) {
		super(parent);
	}
	
	private PlayerEntity player = null;
	MovableReferenceFrame cameraRf = null;
	private boolean mouseLock = false;
	
	private Floor floor;
	
	@Override
	public void initalise() {
		player = new PlayerEntity(Vec3.create(1, 0, 1), 0.1);
		player.addToScene(scene);
		
		FloorGenerator fg = new FloorGenerator(123873123312l);
		floor = fg.getFloor(0);
		
		for(WallEntity we : floor.getWalls())
		{
			we.addToScene(scene);
		}
		
		MeshLOD mlod = new MeshLOD(1, 5, 5, 5, 5, 5);
		mlod.addVertex(-0.5, 0, -0.5);
		mlod.addVertex(-0.5, 0, 0.5);
		mlod.addVertex(0.5, 0, -0.5);
		mlod.addVertex(0.5, 0, 0.5);
		mlod.addPolygon(new int[] { 1, 2, 4, 3}, null, null, null);
		Mesh floorMesh = new Mesh();
		floorMesh.add(mlod);
		
		Material mat = new Material(Color.BLACK, new Color(0.1f, 0.1f, 0.1f), new Color(0.3f, 0.3f, 0.3f), new Color(0f, 0f, 0f), 1f, 1f);
		MovableReferenceFrame floorRf = new MovableReferenceFrame(ReferenceFrame.SCENE_ROOT);
		floorRf.setPosition(Vec3.create(5, -0.5, 5));
		MeshContext mc = new MeshContext(floorMesh, mat, floorRf);
		mc.setScale(10);
		scene.addDrawable(mc);
		
		
		MovableReferenceFrame cameraRf = new MovableReferenceFrame(player);
		scene.getCamera().trackReferenceFrame(cameraRf);
		cameraRf.setPosition(Vec3.create(0, 9, -10));
		cameraRf.setOrientation(Quat.create(Math.PI / 3.6f, Vec3.i));
		
//		cameraRf = new MovableReferenceFrame(player);
//		cameraRf.setPosition(Vec3.create(0, 0, 0));
//		cameraRf.setOrientation(player.getOrientation());
	}

	@Override
	public void update(double delta) {
		Vec3 intent = Vec3.zero;
		
		if(game.getWindow().getKey(KeyEvent.VK_UP))
			intent = intent.add(Vec3.create(0, 0, 0.1));
		if(game.getWindow().getKey(KeyEvent.VK_DOWN))
			intent = intent.add(Vec3.create(0, 0, -0.1));
		if(game.getWindow().getKey(KeyEvent.VK_LEFT))
			intent = intent.add(Vec3.create(0.1, 0, 0));
		if(game.getWindow().getKey(KeyEvent.VK_RIGHT))
			intent = intent.add(Vec3.create(-0.1, 0, 0));
		if(game.getWindow().getKey(KeyEvent.VK_F))
			setFirstPerson(true);
		if(game.getWindow().getKey(KeyEvent.VK_T))
			setFirstPerson(false);
		
		intent = intent.unit().scale(player.getSpeed());
		
		player.moveTo(player.getPosition().add(intent));
		
		for(WallEntity w : floor.getWalls()){
			if(w.getBound().intersects((BoundingSphere)player.getBound())){
				System.out.println("denied");
				player.moveTo(player.getPosition().sub(intent));
				break;
			}
		}
		
//		if(game.getWindow().pollMouseTravelX()<0){
//			
//		}
//		
//		//radiains of rotation
//		double radRot;
//		
//		player.getOrientation();
//		player.setOrientation(_dir);
	}

	@Override
	public void destroy() {
	}	
	
	private void setFirstPerson(boolean _val){
		MovableReferenceFrame cameraRf = new MovableReferenceFrame(player);
		scene.getCamera().trackReferenceFrame(cameraRf);
		if(_val){
			cameraRf.setPosition(Vec3.create(0, 0.5, 0));
			cameraRf.setOrientation(player.getOrientation());
		}else{
			cameraRf.setPosition(Vec3.create(0, 9, -10));
			cameraRf.setOrientation(Quat.create(Math.PI / 3.6f, Vec3.i));
		}
	}
	
	private void scroll(double val){
		if ((val < 0 && cameraRf.getPosition().mag() < 1) || 
			(val > 0 && cameraRf.getPosition().mag() > 20) || 
			(val==0)){
			return;
		}
		
		cameraRf.setPosition(cameraRf.getPosition().scale(val));
	}
}
