package game.states;

import game.Game;
import game.GameState;
import game.Healthbar;
import game.InventorySelector;
import game.ItemType;
import game.MapPane;
import game.StatPane;
import game.entity.Entity;
import game.entity.moveable.DoorEntity;
import game.entity.moveable.PlayerEntity;
import game.entity.moveable.SpikeBallEntity;
import game.entity.trigger.TriggerEntity;
import game.item.Battery;
import game.modelloader.Content;
import initial3d.engine.Camera;
import initial3d.engine.Color;
import initial3d.engine.Material;
import initial3d.engine.Mesh;
import initial3d.engine.MeshContext;
import initial3d.engine.MovableReferenceFrame;
import initial3d.engine.Quat;
import initial3d.engine.RenderWindow;
import initial3d.engine.Vec3;
import initial3d.engine.xhaust.DialogPane;
import initial3d.engine.xhaust.EquippedInventoryContainer;
import initial3d.engine.xhaust.Pane;
import initial3d.renderer.Util;

import java.awt.event.KeyEvent;
import java.util.List;

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

	private int selectedInvenPos = 0;
	private EquippedInventoryContainer equippedIC;
	private InventorySelector invenSelector;

	private Healthbar hp = null;

	private Pane invenPopUp;

	private MovableReferenceFrame cameraRf_3, cameraRf_1;

	private MapPane mappane;

	@Override
	public void initalise() {
		for (PlayerEntity pe : Game.getInstance().getPlayers()) {
			pe.addToScene(scene);
		}
		RenderWindow rwin = Game.getInstance().getWindow();
		rwin.setCrosshairVisible(true);
		rwin.setCursorVisible(false);

		Game.getInstance().getLevel().init(scene);

		cameraRf_3 = new MovableReferenceFrame(Game.getInstance().getPlayer());
		scene.getCamera().trackReferenceFrame(cameraRf_3);
		cameraRf_3.setPosition(Vec3.create(0, 0.5, -0.8));

		cameraRf_1 = new MovableReferenceFrame(Game.getInstance().getPlayer());
		cameraRf_1.setPosition(Vec3.create(0, 0.15, 0));


		//set up the inventory selector stuff
		Pane invenEquippedPane = new Pane(250, 50);
		equippedIC = new EquippedInventoryContainer(Game.getInstance().getPlayer());
		invenEquippedPane.getRoot().add(equippedIC);

		invenEquippedPane.requestVisible(true);
		invenEquippedPane.setPosition(-275, -275);
		invenEquippedPane.getRoot().setOpaque(false);
		scene.addDrawable(invenEquippedPane);
		Game.getInstance().setInventoryHolder(invenEquippedPane);
		//sets up the popup inventory stuff
		Game.getInstance().setInvenPopUp(new DialogPane(400, 200, invenEquippedPane, false));
		invenSelector = new InventorySelector(400, 200, Game.getInstance().getPlayer(), equippedIC, selectedInvenPos);
		Game.getInstance().getInvenPopUp().getRoot().add(invenSelector);
		invenSelector.setOpaque(false);

		Game.getInstance().getInvenPopUp().setPosition(0, 0);
		Game.getInstance().getInvenPopUp().getRoot().setOpaque(false);
		scene.addDrawable(Game.getInstance().getInvenPopUp());
		scene.addDrawable(Game.getInstance().getInventoryHolder());


		// stats
		Pane statpane = new StatPane();
		statpane.setPosition(-100, 240);
		statpane.requestVisible(true);
		scene.addDrawable(statpane);

		// minimap
		mappane = new MapPane();
		mappane.setPosition(300, 200);
		mappane.requestVisible(true);
		scene.addDrawable(mappane);

		Game.getInstance().getWindow().setMouseCapture(true);

		scene.setAmbient(new Color(0.1f, 0.1f, 0.1f));
		scene.setFogColor(Color.BLACK);
		scene.setFogParams(255f * 1.5f, 1024f * 1.5f);
		scene.setFogEnabled(true);


		Game.getInstance().startTimer();
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
		if (rwin.pollKey(KeyEvent.VK_1)) {
			invenSelector.setSelectedPos(0);
			this.selectedInvenPos = 0;

		}
		if (rwin.pollKey(KeyEvent.VK_2)) {
			invenSelector.setSelectedPos(1);
			this.selectedInvenPos = 1;
		}
		if (rwin.pollKey(KeyEvent.VK_3)) {
			invenSelector.setSelectedPos(2);
			this.selectedInvenPos = 2;
		}
		if (rwin.pollKey(KeyEvent.VK_4)) {
			invenSelector.setSelectedPos(3);
			this.selectedInvenPos = 3;
		}
		if (rwin.pollKey(KeyEvent.VK_5)) {
			invenSelector.setSelectedPos(4);
			this.selectedInvenPos = 4;

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
		if (rwin.pollKey(KeyEvent.VK_Q)) {
			Game.getInstance().getPlayer().getInventory().removeItem(Game.getInstance().getPlayer().getInventory().getItem(selectedInvenPos));
			Game.getInstance().getPlayer().getEquippedItems()[selectedInvenPos] = null;
			equippedIC.repaint();
			invenSelector.repaint();
		}

		if (rwin.pollKey(KeyEvent.VK_F1)) {
			cam.trackReferenceFrame(cameraRf_1);
		}
		if (rwin.pollKey(KeyEvent.VK_F3)) {
			cam.trackReferenceFrame(cameraRf_3);
		}
		if (rwin.pollKey(KeyEvent.VK_R)) {
			mappane.incScale();
		}
		if (rwin.pollKey(KeyEvent.VK_F)) {
			mappane.decScale();
		}



		if (rwin.getKey(KeyEvent.VK_ESCAPE)) {
			rwin.setMouseCapture(!rwin.isMouseCaptured());
		}

		// fire gun
		if (!Game.getInstance().getInvenPopUp().isVisible()
				&& rwin.getMouseButton(1)
				&& (System.currentTimeMillis() - lastShot) > ((PlayerEntity.defaultEnergy - Game.getInstance()
						.getPlayer().getCurrentEnergy()) / 4 + 100) && !Game.getInstance().getPlayer().isDead()
						&& Game.getInstance().getPlayer().getCurrentEnergy() > 0) {
			Game.getInstance().createProjectile();
			Game.getInstance().getPlayer().applyEnergyDelta(-4);
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
		Vec3 colNorm = Game.getInstance().getLevel().preCollisionNorm(player, true);

		// if there was a collision set the velocity appropriately (if also not dead)
		 if(colNorm != null && !Game.getInstance().getPlayer().isDead()){ //vector magic 
			 colNorm = colNorm.unit().flattenY();
			 if(!intent_vel.equals(Vec3.zero)){
				 colNorm = colNorm.unit().flattenY();
				 Vec3 intentUnit = intent_vel.unit();
				 if(intentUnit.dot(colNorm)<0){
					 double scale = -1 * (colNorm.dot(intentUnit)) * intent_vel.mag(); 
					 intent_vel = ((colNorm.cross(intentUnit)).cross(colNorm)).unit().scale(scale); 
				 }
				 
			 }
		}

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

		if(Game.getInstance().getRemainingMs() <= 0)
			Game.getInstance().gameOver();

		if(Game.getInstance().alivePlayers() <= 0)
			Game.getInstance().gameOver();


	}


	@Override
	public void destroy() {
	}
}
