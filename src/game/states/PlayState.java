package game.states;

import java.awt.event.KeyEvent;
import java.security.Identity;
import java.util.List;

import soundengine.SimpleAudioPlayer;

import initial3d.engine.*;
import initial3d.engine.xhaust.Healthbar;
import initial3d.engine.xhaust.InventoryHolder;
import initial3d.engine.xhaust.Pane;
import initial3d.renderer.Util;
import game.Game;
import game.GameState;
import game.bound.BoundingSphere;
import game.entity.Entity;
import game.entity.moveable.ItemEntity;
import game.entity.moveable.PlayerEntity;
import game.entity.moveable.ProjectileEntity;
import game.entity.trigger.DynamicTriggerEntity;
import game.entity.trigger.StaticTriggerEntity;
import game.entity.trigger.TriggerEntity;
import game.event.AvoidEvent;
import game.event.ContactEvent;
import game.event.DamageEvent;
import game.event.RemoveEntityEvent;
import game.item.Item;
import game.modelloader.Content;

/***
 * The game!
 * 
 * @author Ben
 * 
 */
public class PlayState extends GameState {
	public PlayState() {
	}

	MovableReferenceFrame cameraRf = null;
	
	private boolean transmittedStop = false;

	private double cam_pitch = 0;
	private double player_yaw = 0;
	
	private double targetFov = -1;
	private long lastShot = System.currentTimeMillis();
	
	private Healthbar hp = null;

	@Override
	public void initalise() {
		for(PlayerEntity pe : Game.getInstance().getPlayers())
		{
			pe.addToScene(scene);
		}
		
		
		System.out.println("Creating test object");
		
		Item it = new Item(null, null){};
		ItemEntity ie = new ItemEntity(Vec3.create(3, 0.125, 3), it);
//		ie.updateMotion(Vec3.create(3, 0.125, 3), Vec3.zero, Quat.one, Vec3.zero, System.currentTimeMillis());
		
		Material mat = new Material(Color.RED, Color.RED, new Color(0.5f, 0.5f, 0.5f), new Color(0f, 0f, 0f), 20f, 1f);		
		Mesh m = Content.loadContent("sphere.obj");
		MeshContext mc = new MeshContext(m, mat, ie);
		mc.setScale(0.25);
		mc.setHint(MeshContext.HINT_SMOOTH_SHADING);
		ie.addMeshContext(mc);
		
		ie.addToLevel(Game.getInstance().getLevel());
//		ie.addToScene(scene);
		
		System.out.println("Added Test object to level");
		
		DynamicTriggerEntity ste = new DynamicTriggerEntity(new DamageEvent(), ie);
/*		MeshContext testball = new MeshContext(m, mat, ste);
		ste.addMeshContext(testball);*/
		ste.addToLevel(Game.getInstance().getLevel());
		ste.addToScene(scene);
		
		System.out.println(Game.getInstance().getLevel());
		Game.getInstance().getLevel().addToScene(scene);

		MovableReferenceFrame cameraRf = new MovableReferenceFrame(Game.getInstance().getPlayer());
		scene.getCamera().trackReferenceFrame(cameraRf);
		cameraRf.setPosition(Vec3.create(0, 0.5, -0.8));
//		cameraRf.setOrientation(Quat.create(Math.PI / 3.6f, Vec3.i));
		Pane p = new Pane(250, 50);
		InventoryHolder i = new InventoryHolder();
		p.getRoot().add(i);
		p.requestVisible(true);
		p.setPosition(-275, -275);
		p.getRoot().setOpaque(false);
		scene.addDrawable(p);
		
		Pane topPane = new Pane(500, 100);
		hp = new Healthbar();
		topPane.getRoot().add(hp);
		topPane.requestVisible(true);
		topPane.getRoot().setOpaque(false);
		topPane.setPosition(-100, 240);
		scene.addDrawable(topPane);
		
		
		Game.getInstance().getWindow().setMouseCapture(true);
		
		scene.setAmbient(new Color(0.1f, 0.1f, 0.1f));
		scene.setFogColor(Color.BLACK);
		scene.setFogParams(255f * 1.5f, 512f * 1.5f);
		scene.setFogEnabled(true);
		
		for(PlayerEntity pe : Game.getInstance().getPlayers())
		{
			MovableReferenceFrame lrf = new MovableReferenceFrame(pe);
			lrf.setPosition(Vec3.create(0, 0.5, 0));
			Light l2 = new Light.SpotLight(pe, Color.WHITE, 4f, (float) Math.PI / 2, 10f);
			scene.addLight(l2);
			
			MovableReferenceFrame elrf = new MovableReferenceFrame(pe);
			elrf.setPosition(Vec3.create(0, -0.25, 0));
			Light el = new Light.SphericalPointLight(lrf, Color.DARK_RED, 0.10f);
			scene.addLight(el);
		}
		
		Game.getInstance().getPlayer().getMeshContexts().get(0).setHint(MeshContext.HINT_SMOOTH_SHADING);	
//		SimpleAudioPlayer.play("resources/music/levelMusic.wav", true);
	}

	@Override
	public void update(double delta) {
		RenderWindow rwin = Game.getInstance().getWindow();
		if(targetFov < 0) targetFov = scene.getCamera().getFOV();

		double speed = 1;
		double sprintMulti = 1.5f;

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
		
		Vec3 cnorm = cam.getNormal().flattenY().unit();
		Vec3 cside = Vec3.j.cross(cnorm);

		Vec3 intent_vel = Vec3.zero;

		if (rwin.getKey(KeyEvent.VK_W)) {
			intent_vel = intent_vel.add(cnorm);
		}
		if (rwin.getKey(KeyEvent.VK_S)) {
			intent_vel = intent_vel.add(cnorm.neg());
		}
		if (rwin.getKey(KeyEvent.VK_A)) {
			intent_vel = intent_vel.add(cside);
		}
		if (rwin.getKey(KeyEvent.VK_D)) {
			intent_vel = intent_vel.add(cside.neg());
		}
		
		// temp
//		if(rwin.getKey(KeyEvent.VK_O)) {
//			this.scene.getCamera().setFOV(this.scene.getCamera().getFOV() + 0.01);
//		} else if(rwin.getKey(KeyEvent.VK_P)) {
//			this.scene.getCamera().setFOV(this.scene.getCamera().getFOV() - 0.01);
//		}
		
		if(rwin.getKey(KeyEvent.VK_ESCAPE))
		{
			rwin.setMouseCapture(!rwin.isMouseCaptured());
		} else if(rwin.getMouseButton(1) && (System.currentTimeMillis() - lastShot) > 1000)
		{
			Game.getInstance().createProjectile();
			this.lastShot = System.currentTimeMillis();
		}
		
		//sprinting
		double maxDelta = 0.01;
		boolean sprinting = rwin.getKey(KeyEvent.VK_SHIFT);
		if(sprinting) targetFov = Math.PI / 2.2;
		else targetFov = Math.PI / 3;
		
		double dta = this.scene.getCamera().getFOV() - targetFov;
		if(Math.abs(dta) > 0.1)
		{
			if(dta > maxDelta)
				dta = maxDelta;
			else if(dta < -maxDelta)
				dta = -maxDelta;
			
			this.scene.getCamera().setFOV(this.scene.getCamera().getFOV() - dta);
		}
		
		if (intent_vel.mag() > 0.0001) {
			intent_vel = intent_vel.unit().scale(speed * (sprinting ? sprintMulti : 1));
		}
				
		//Retrieve the player and set the velocity
		PlayerEntity player = Game.getInstance().getPlayer();
		player.setIntVelocity(intent_vel);
		
		//get the collision normals (if any)
		Vec3 colNorm = Game.getInstance().getLevel().preCollision(player, true);
		
		//if there was a collision set the velocity appropriately
		if(colNorm != null){
			//vector magic
			Vec3 intentUnit = intent_vel.unit();
			double scale = -1 * (colNorm.dot(intentUnit)) * intent_vel.mag();
			intent_vel = ((colNorm.cross(intentUnit)).cross(colNorm)).scale(scale);
		}
		
		//poke all players and set the velocity
		player.updateMotion(player.getPosition(), intent_vel, Quat.create(player_yaw, Vec3.j), Vec3.zero, Game.time());
		
		if(Game.getInstance().isHost()){
			for(TriggerEntity t : Game.getInstance().getLevel().getTriggers()){
				List<Entity> le = Game.getInstance().getLevel().collisions(t);
				if(!le.isEmpty()){
					t.trigger(le);
				}
			}
		}
		
		
		
		Game.getInstance().getLevel().pokeAll();
		
		if(!intent_vel.equals(Vec3.zero))
		{
			Game.getInstance().transmitPlayerPosition();
			transmittedStop = false;
		}
		else if(!transmittedStop)
		{
			Game.getInstance().transmitPlayerPosition();
			transmittedStop = true;
		}
		
		// update the game UI
		this.hp.update(Game.getInstance().getPlayer().getHealth());
	}

	@Override
	public void destroy() {
	}
}
