package game.states;

import java.awt.event.KeyEvent;

import initial3d.engine.*;
import initial3d.renderer.Util;
import game.Game;
import game.GameState;
import game.bound.BoundingSphere;
import game.entity.Entity;
import game.entity.PlayerEntity;
import game.entity.WallEntity;
import game.floor.Floor;
import game.floor.FloorGenerator;
import game.net.packets.MovementPacket;

/***
 * The game!
 * 
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

	private double cam_pitch = 0;
	private double player_yaw = 0;
	
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
		mlod.addPolygon(new int[] { 1, 2, 4, 3 }, null, null, null);
		Mesh floorMesh = new Mesh();
		floorMesh.add(mlod);

		Material mat = new Material(Color.BLACK, new Color(0.1f, 0.1f, 0.1f), new Color(0.3f, 0.3f, 0.3f), new Color(
				0f, 0f, 0f), 1f, 1f);
		MovableReferenceFrame floorRf = new MovableReferenceFrame(ReferenceFrame.SCENE_ROOT);
		floorRf.setPosition(Vec3.create(5, -0.5, 5));
		MeshContext mc = new MeshContext(floorMesh, mat, floorRf);
		mc.setScale(10);
		scene.addDrawable(mc);

		MovableReferenceFrame cameraRf = new MovableReferenceFrame(player);
		scene.getCamera().trackReferenceFrame(cameraRf);
		cameraRf.setPosition(Vec3.create(0, 0.3, -0.5));
//		cameraRf.setOrientation(Quat.create(Math.PI / 3.6f, Vec3.i));
		
		game.getWindow().setMouseCapture(true);
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
		
		player.setPosition(player.getPosition().add(intent));
		
		if(!intent.equals(Vec3.zero))
		{
			MovementPacket mp = new MovementPacket(player.getPosition(), player.getVelocity());
			this.game.getNetwork().send(mp.toData());
		}

		RenderWindow rwin = game.getWindow();

		double speed = 1;

		int mx = rwin.pollMouseTravelX();
		int my = rwin.pollMouseTravelY();

		// 200px == pi / 4 ??

		// these are confusing names, aren't they?
		double rotx = mx / 800d * Math.PI;
		double roty = my / 800d * Math.PI;

		cam_pitch = Util.clamp(cam_pitch + roty, -Math.PI * 0.499, Math.PI * 0.499);
		player_yaw -= rotx;

		Camera cam = scene.getCamera();

		MovableReferenceFrame rf = (MovableReferenceFrame) cam.getTrackedReferenceFrame();
		rf.setOrientation(Quat.create(cam_pitch, Vec3.i));
		
		player.setOrientation(Quat.create(player_yaw, Vec3.j));

		Vec3 cnorm = cam.getNormal().flattenY().unit();
		Vec3 cup = Vec3.j;
		Vec3 cside = Vec3.j.cross(cnorm);

		Vec3 v = Vec3.zero;

		if (rwin.getKey(KeyEvent.VK_W)) {
			v = v.add(cnorm);
		}
		if (rwin.getKey(KeyEvent.VK_S)) {
			v = v.add(cnorm.neg());
		}
		if (rwin.getKey(KeyEvent.VK_A)) {
			v = v.add(cside);
		}
		if (rwin.getKey(KeyEvent.VK_D)) {
			v = v.add(cside.neg());
		}

		v = v.unit().scale(speed);
		player.setPosition(player.getPosition().add(v));
		for(WallEntity w : floor.getWalls()){
			if(w.getBound().intersects(player.getBound())){
				System.out.println("denied");
				player.setPosition(player.getPosition().sub(intent));
				break;
			}
		}
	}

	@Override
	public void destroy() {
	}

	private void setFirstPerson(boolean _val) {
		MovableReferenceFrame cameraRf = (MovableReferenceFrame) scene.getCamera().getTrackedReferenceFrame();
		if (_val) {
			cameraRf.setPosition(Vec3.create(0, 0.5, 0));
			cameraRf.setOrientation(player.getOrientation());
		} else {
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
