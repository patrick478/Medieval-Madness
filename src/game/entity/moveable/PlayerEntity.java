package game.entity.moveable;

import game.Game;
import game.bound.Bound;
import game.bound.BoundingSphere;
import game.entity.Damageable;
import game.item.Item;
import game.item.ItemContainer;
import game.modelloader.Content;
import initial3d.Initial3D;
import initial3d.Texture;
import initial3d.engine.Color;
import initial3d.engine.Light;
import initial3d.engine.Material;
import initial3d.engine.Mesh;
import initial3d.engine.MeshContext;
import initial3d.engine.MovableReferenceFrame;
import initial3d.engine.Quat;
import initial3d.engine.ReferenceFrame;
import initial3d.engine.Scene;
import initial3d.engine.Vec3;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class PlayerEntity extends MoveableEntity implements Damageable {

	private static final Texture tex_body_kd;
	private static final Texture tex_body_ke;

	static {
		BufferedImage bi_kd = Content.loadContent("resources/models/character/char2_kd.png");
		tex_body_kd = Initial3D.createTexture(Texture.requiredSize(Math.max(bi_kd.getWidth(), bi_kd.getHeight())));
		tex_body_kd.drawImage(bi_kd);
		tex_body_kd.composeMipMaps();
		tex_body_kd.useMipMaps(true);

		BufferedImage bi_ke = Content.loadContent("resources/models/character/char2_ke.png");
		tex_body_ke = Initial3D.createTexture(Texture.requiredSize(Math.max(bi_ke.getWidth(), bi_ke.getHeight())));
		tex_body_ke.drawImage(bi_ke);
		tex_body_ke.composeMipMaps();
		tex_body_ke.useMipMaps(true);
	}

	private final double baseSpeed = 1;

	private int defaultDamage = 2;
	private int defaultHealth = 100;
	private int defaultEnergy = 100;

	private final ItemContainer inventory = new ItemContainer(null, "Inventory", 8);
	private final Item[] equippedItems = new Item[5];

	public ItemContainer getInventory() {
		return inventory;
	}
	
	public Item[] getEquippedItems() {
		return equippedItems;
	}

	private int currentHealth = 170;

	private final double radius;
	private int selfIndex = 0;

	private boolean pregameReadyState = false;

	private MovableReferenceFrame spotlight_rf;
	private Light.SpotLight spotlight;
	private MovableReferenceFrame baselight_rf;
	private Light.SpotLight baselight;

	private boolean muzzle_flash_on = false;
	private long last_muzzle_flash = 0;

	public PlayerEntity(long _id, Vec3 _pos, double _radius, int pindex) {
		super(_id);
		position = _pos;
		radius = _radius;
		this.selfIndex = pindex;
		this.addMeshContexts(this.getBall());

		init();
	}

	private void init() {
		// lights
		spotlight_rf = new MovableReferenceFrame(this);
		spotlight_rf.setPosition(Vec3.create(0, 0, 0.25));
		spotlight = new Light.SpotLight(spotlight_rf, Color.WHITE, 3f, (float) (Math.PI / 4), 10f);
		spotlight.setEffectRadius(6f);

		baselight_rf = new MovableReferenceFrame(this);
		baselight_rf.setOrientation(Quat.create(Math.PI / 2, Vec3.i));
		baselight = new Light.SpotLight(baselight_rf, Color.RED, 0.3f, (float) Math.PI / 4, 1f);
		baselight.setEffectRadius(1f);

	}

	public void muzzleFlash(boolean on) {
		if (on) {
			// TODO replace params with custom flash state
			spotlight.setColor(Color.YELLOW);
			spotlight.setRadius(8f);
			spotlight.setSpotExponent(2f);
			muzzle_flash_on = true;
			last_muzzle_flash = System.currentTimeMillis();
		} else {
			// TODO replace params with custom normal state
			spotlight.setColor(Color.WHITE);
			spotlight.setRadius(3f);
			spotlight.setSpotExponent(10f);
			muzzle_flash_on = false;
		}
	}

	@Override
	public void poke() {
		super.poke();
		if (muzzle_flash_on && System.currentTimeMillis() - last_muzzle_flash > 20) {
			muzzleFlash(false);
		}
	}

	@Override
	protected Bound getBound(Vec3 position) {
		return new BoundingSphere(position, radius);
	}

	@Override
	public boolean isSolid() {
		return true;
	}

	public double getSpeed() {
		return baseSpeed;
	}

	public Color getColor() {
		switch (this.selfIndex) {
		case 0:
			return Color.GREEN;
		case 1:
			return Color.RED;
		case 2:
			return Color.BLUE;
		case 3:
			return Color.YELLOW;
		}
		return Color.WHITE;
	}

	private List<MeshContext> getBall() {
		// Material mat = new Material(this.getColor(), this.getColor(), new
		// Color(0.5f, 0.5f, 0.5f), new Color(0f, 0f, 0f), 20f, 1f);
		// Mesh m = Content.loadContent("sphere.obj");
		// MeshContext mc = new MeshContext(m, mat, this);
		// mc.setScale(0.125);

		List<MeshContext> meshes = new ArrayList<MeshContext>();

		// body
		Mesh m_body = Content.loadContent("resources/models/character/char2_body.obj");
		Material mtl_body = new Material(getColor(), getColor(), new Color(0.5f, 0.5f, 0.5f), Color.BLACK, 20f, 1f);
		mtl_body = new Material(mtl_body, tex_body_kd, null, tex_body_ke);

		MovableReferenceFrame mrf = new MovableReferenceFrame(this);
		// mrf.setOrientation(Quat.create(Math.PI, Vec3.j));

		MeshContext mc_body = new MeshContext(m_body, mtl_body, mrf);

		meshes.add(mc_body);

		// arms
		Mesh m_arm_l = Content.loadContent("resources/models/character/char2_arm_l.obj");
		Mesh m_arm_r = Content.loadContent("resources/models/character/char2_arm_r.obj");
		Material mtl_arm = new Material(Color.GRAY, new Color(0.2f, 0.2f, 0.2f), new Color(0.7f, 0.7f, 0.7f),
				Color.BLACK, 64f, 1f);
		MeshContext mc_arm_l = new MeshContext(m_arm_l, mtl_arm, mrf);
		MeshContext mc_arm_r = new MeshContext(m_arm_r, mtl_arm, mrf);

		meshes.add(mc_arm_l);
		meshes.add(mc_arm_r);

		// gun
		Mesh m_gun = Content.loadContent("resources/models/character/char2_gun.obj");
		Material mtl_gun = new Material(Color.BLACK, new Color(0.2f, 0.2f, 0.2f), new Color(0.5f, 0.5f, 0.5f),
				new Color(0.2f, 0f, 0f), 1f, 1f);
		MeshContext mc_gun = new MeshContext(m_gun, mtl_gun, mrf);

		meshes.add(mc_gun);

		mc_body.setHint(MeshContext.HINT_SMOOTH_SHADING);
		mc_arm_l.setHint(MeshContext.HINT_SMOOTH_SHADING);
		mc_arm_r.setHint(MeshContext.HINT_SMOOTH_SHADING);
		mc_gun.setHint(MeshContext.HINT_SMOOTH_SHADING);

		return meshes;
	}

	public int getHealth() {
		return this.currentHealth;
	}

	public void setHealth(int i) {
		this.currentHealth = i;
	}

	public boolean getPregameReadyState() {
		return this.pregameReadyState;
	}

	public void setPregameReadyState(boolean b) {
		this.pregameReadyState = b;
		Game.getInstance().updatePregameScreen();
	}

	@Override
	public void applyHealthDelta(int _damage) {
		currentHealth += _damage;
	}

	@Override
	public int getTotalHealth() {
		return defaultHealth;
	}

	@Override
	public int getCurrentHealth() {
		return currentHealth;
	}

	@Override
	public void setCurrentHealth(int i) {
		this.currentHealth = i;
	}

	@Override
	public void addToScene(Scene s) {
		super.addToScene(s);

		// add lights
		s.addLight(spotlight);
		s.addLight(baselight);
	}
}
