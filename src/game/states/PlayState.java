package game.states;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.Identity;
import java.util.List;

import javax.imageio.ImageIO;

import soundengine.SimpleAudioPlayer;

import initial3d.Initial3D;
import initial3d.Texture;
import initial3d.engine.*;
import initial3d.engine.xhaust.DialogPane;
import initial3d.engine.xhaust.Pane;
import initial3d.renderer.Util;
import game.Game;
import game.GameState;
import game.Healthbar;
import game.bound.BoundingSphere;
import game.InventorySelector;
import game.entity.Entity;
import game.entity.moveable.EnemyEntity;
import game.entity.moveable.ItemEntity;
import game.entity.moveable.PlayerEntity;
import game.entity.moveable.ProjectileEntity;
import game.entity.moveable.SpikeBall;
import game.entity.trigger.DynamicTriggerEntity;
import game.entity.trigger.StaticTriggerEntity;
import game.entity.trigger.TriggerEntity;
import game.event.AvoidEvent;
import game.event.ContactEvent;
import game.event.DeltaHealthEvent;
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

	private boolean transmittedStop = false;

	private double cam_pitch = 0;
	private double player_yaw = 0;

	private double targetFov = -1;
	private long lastShot = System.currentTimeMillis();

	private Healthbar hp = null;

	private Pane invenPopUp;

	MovableReferenceFrame cameraRf_3, cameraRf_1;

	@Override
	public void initalise() {
		for (PlayerEntity pe : Game.getInstance().getPlayers()) {
			pe.addToScene(scene);
		}
		RenderWindow rwin = Game.getInstance().getWindow();
		rwin.setCrosshairVisible(true);
		rwin.setCursorVisible(false);

		System.out.println("Creating test object");
		BufferedImage battery = null;
		try {
			battery = ImageIO.read(new File("resources/inventory/battery.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Item it = new Item(battery, "Battery") {
		};
		ItemEntity ie = new ItemEntity(Vec3.create(3, 0.125, 3), it);
		// ie.updateMotion(Vec3.create(3, 0.125, 3), Vec3.zero, Quat.one, Vec3.zero, System.currentTimeMillis());

		// battery
		Material mat = new Material(Color.GRAY, Color.GRAY, Color.GRAY, Color.BLACK, 20f, 1f);
		Texture tex_kd = Initial3D.createTexture(Content
				.<BufferedImage> loadContent("resources/models/battery/battery_kd.png"));
		Texture tex_ke = Initial3D.createTexture(Content
				.<BufferedImage> loadContent("resources/models/battery/battery_ke.png"));
		mat = new Material(mat, tex_kd, null, tex_ke);

		Mesh m = Content.loadContent("resources/models/battery/battery.obj");
		MeshContext mc = new MeshContext(m, mat, ie);
		mc.setScale(4);
		mc.setHint(MeshContext.HINT_SMOOTH_SHADING);
		// key
		mat = new Material(Color.GRAY, new Color(0.081f, 0.064f, 0.036f), new Color(0.81f, 0.72f, 0.54f), new Color(
				0.2f, 0.2f, 0f), 1f, 1f);
		m = Content.loadContent("resources/models/key/key.obj");
		mc = new MeshContext(m, mat, ie);
		mc.setHint(MeshContext.HINT_SMOOTH_SHADING);
		mc.setScale(4);

		// spikeball
		mat = new Material(Color.GRAY, new Color(0.3f, 0.25f, 0.3f), new Color(0.65f, 0.2f, 0.65f), new Color(0.3f, 0f,
				0.3f), 1f, 1f);
		m = Content.loadContent("resources/models/spikeball/spikeball.obj");
		mc = new MeshContext(m, mat, ie);
		mc.setHint(MeshContext.HINT_SMOOTH_SHADING);

		// box
		mat = new Material(Color.GRAY, Color.WHITE, Color.BLACK, Color.BLACK, 1f, 1f);
		tex_kd = Initial3D.createTexture(Content.<BufferedImage> loadContent("resources/models/box/box_kd.png"));
		mat = new Material(mat, tex_kd, null, null);
		m = Content.loadContent("resources/models/box/box.obj");
		mc = new MeshContext(m, mat, ie);
		mc.setHint(MeshContext.HINT_SMOOTH_SHADING);
		
		// doorbars
		mat = new Material(Color.GRAY, new Color(0.3f, 0.3f, 0.3f), new Color(0.65f, 0.65f, 0.65f), Color.BLACK, 1f, 1f);
		m = Content.loadContent("resources/models/doorbars/doorbars.obj");
		mc = new MeshContext(m, mat, ie);
		mc.setHint(MeshContext.HINT_SMOOTH_SHADING);
		
		ie.setPosition(Vec3.create(3, 0, 3));

		ie.addMeshContext(mc);

		ie.addToLevel(Game.getInstance().getLevel());
		// ie.addToScene(scene);

		System.out.println("Added Test object to level");

		// EnemyEntity e = new SpikeBall(100, -1, Vec3.create(3, 0.125, 3), Vec3.create(5, 0.125, 5), 0.15);
		// Material mat1 = new Material(Color.RED, Color.RED, new Color(0.5f, 0.5f, 0.5f), new Color(0f, 0f, 0f), 20f,
		// 1f);
		// Mesh m1 = Content.loadContent("sphere.obj");
		// MeshContext mc1 = new MeshContext(m1, mat1, e);
		// mc1.setScale(0.1);
		// mc1.setHint(MeshContext.HINT_SMOOTH_SHADING);
		//
		// e.addMeshContext(mc1);
		// e.addToLevel(Game.getInstance().getLevel());

		System.out.println(Game.getInstance().getLevel());
		Game.getInstance().getLevel().addToScene(scene);

		cameraRf_3 = new MovableReferenceFrame(Game.getInstance().getPlayer());
		scene.getCamera().trackReferenceFrame(cameraRf_3);
		cameraRf_3.setPosition(Vec3.create(0, 0.5, -0.8));

		cameraRf_1 = new MovableReferenceFrame(Game.getInstance().getPlayer());
		cameraRf_1.setPosition(Vec3.create(0, 0.15, 0));

		// cameraRf.setOrientation(Quat.create(Math.PI / 3.6f, Vec3.i));
		Pane p = new Pane(250, 50);
		// EquippedInventoryContainer i = new
		// EquippedInventoryContainer(Game.getInstance().getPlayer().getEquippedItems());
		// p.getRoot().add(i);

		p.requestVisible(true);
		p.setPosition(-275, -275);
		p.getRoot().setOpaque(false);
		// scene.addDrawable(p);
		Game.getInstance().setInventoryHolder(p);

		Game.getInstance().setInvenPopUp(new DialogPane(400, 200, p, false));
		InventorySelector i2 = new InventorySelector(400, 200, Game.getInstance().getPlayer());
		Game.getInstance().getInvenPopUp().getRoot().add(i2);
		i2.setOpaque(false);
		// invenPopUp.requestVisible(false);
		Game.getInstance().getInvenPopUp().setPosition(0, 0);
		Game.getInstance().getInvenPopUp().getRoot().setOpaque(false);
		scene.addDrawable(Game.getInstance().getInvenPopUp());
		scene.addDrawable(Game.getInstance().getInventoryHolder());

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
		scene.setFogParams(255f * 1.5f, 1024f * 1.5f);
		scene.setFogEnabled(true);

		// for(PlayerEntity pe : Game.getInstance().getPlayers())
		// {
		// MovableReferenceFrame lrf = new MovableReferenceFrame(pe);
		// lrf.setPosition(Vec3.create(0, 0, 0.25));
		// //lrf.setOrientation(Quat.create(Math.PI / 12, Vec3.i));
		// Light l2 = new Light.SpotLight(lrf, Color.WHITE, 3f, (float) (Math.PI / 4), 10f);
		// scene.addLight(l2);
		//
		// MovableReferenceFrame elrf = new MovableReferenceFrame(pe);
		// elrf.setPosition(Vec3.create(0, 0.5, 0));
		// Light el = new Light.SphericalPointLight(elrf, Color.DARK_RED, 0.10f);
		// scene.addLight(el);
		// }
		//

		// SimpleAudioPlayer.play("resources/music/levelMusic.wav", true);
	}

	@Override
	public void update(double delta) {

		Game.getInstance().getLevel().pokeAll();

		RenderWindow rwin = Game.getInstance().getWindow();
		if (targetFov < 0) targetFov = scene.getCamera().getFOV();

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
		if (rwin.pollKey(KeyEvent.VK_E)) {
			if (Game.getInstance().getInvenPopUp().isVisible() == false) {
				Game.getInstance().getInvenPopUp().requestVisible(true);
				rwin.setMouseCapture(false);
				rwin.setCursorVisible(true);
				rwin.setCrosshairVisible(false);

			} else if (Game.getInstance().getInvenPopUp().isVisible() == true) {
				Game.getInstance().getInvenPopUp().requestVisible(false);
				rwin.setMouseCapture(true);
				rwin.setCursorVisible(false);
				rwin.setCrosshairVisible(true);

			}

		}

		if (rwin.pollKey(KeyEvent.VK_F1)) {
			cam.trackReferenceFrame(cameraRf_1);
		}
		if (rwin.pollKey(KeyEvent.VK_F3)) {
			cam.trackReferenceFrame(cameraRf_3);
		}

		// temp
		// if(rwin.getKey(KeyEvent.VK_O)) {
		// this.scene.getCamera().setFOV(this.scene.getCamera().getFOV() + 0.01);
		// } else if(rwin.getKey(KeyEvent.VK_P)) {
		// this.scene.getCamera().setFOV(this.scene.getCamera().getFOV() - 0.01);
		// }

		if (rwin.getKey(KeyEvent.VK_ESCAPE)) {
			rwin.setMouseCapture(!rwin.isMouseCaptured());
		}

		if (!Game.getInstance().getInvenPopUp().isVisible() && rwin.getMouseButton(1)
				&& (System.currentTimeMillis() - lastShot) > 50) {
			Game.getInstance().createProjectile();
			this.lastShot = System.currentTimeMillis();
			Game.getInstance().getPlayer().muzzleFlash(true);
		}

		// sprinting
		double maxDelta = 0.01;
		boolean sprinting = rwin.getKey(KeyEvent.VK_SHIFT);
		if (sprinting)
			targetFov = Math.PI / 2.2;
		else
			targetFov = Math.PI / 3;

		double dta = this.scene.getCamera().getFOV() - targetFov;
		if (Math.abs(dta) > 0.1) {
			if (dta > maxDelta)
				dta = maxDelta;
			else if (dta < -maxDelta) dta = -maxDelta;

			this.scene.getCamera().setFOV(this.scene.getCamera().getFOV() - dta);
		}

		if (intent_vel.mag() > 0.0001) {
			intent_vel = intent_vel.unit().scale(speed * (sprinting ? sprintMulti : 1));
		}

		// Retrieve the player and set the velocity
		PlayerEntity player = Game.getInstance().getPlayer();
		player.setIntVelocity(intent_vel);

		// get the collision normals (if any)
		Vec3 colNorm = Game.getInstance().getLevel().preCollision(player, true);

		// if there was a collision set the velocity appropriately
		/*
		 * if(colNorm != null){ //vector magic colNorm = colNorm.flattenY(); Vec3 intentUnit = intent_vel.unit(); double
		 * scale = -1 * (colNorm.dot(intentUnit)) * intent_vel.mag(); intent_vel =
		 * ((colNorm.cross(intentUnit)).cross(colNorm)).scale(scale); }
		 */

		// poke all players and set the velocity
		player.updateMotion(player.getPosition(), intent_vel, Quat.create(player_yaw, Vec3.j), Vec3.zero, Game.time());

		if (Game.getInstance().isHost()) {
			for (TriggerEntity t : Game.getInstance().getLevel().getTriggers()) {
				List<Entity> le = Game.getInstance().getLevel().collisions(t);
				if (!le.isEmpty()) {
					t.trigger(le);
				}
			}
		}

		if (!intent_vel.equals(Vec3.zero)) {
			Game.getInstance().transmitPlayerPosition();
			transmittedStop = false;
		} else if (!transmittedStop) {
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
