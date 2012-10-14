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
import initial3d.engine.Material;
import initial3d.engine.Mesh;
import initial3d.engine.MeshContext;
import initial3d.engine.MovableReferenceFrame;
import initial3d.engine.Quat;
import initial3d.engine.Vec3;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class PlayerEntity extends MoveableEntity implements Damageable{
	
	private final double baseSpeed = 1;

	private int defaultDamage = 2;
	private int defaultHealth = 100;
	private int defaultEnergy = 100;

	private final ItemContainer inventory = new ItemContainer(null, "Inventory", 8);
	private final Item[] equippedItems = new Item[5];

	public Item[] getEquippedItems() {
		return equippedItems;
	}

	private int currentHealth = 50;

	
	private final double radius;
	private int selfIndex = 0;
	
	private static final Texture tex_body_kd;

	static {

		BufferedImage bi = Content.loadContent("resources/models/character/char2_test.png");
		tex_body_kd = Initial3D.createTexture(Texture.requiredSize(Math.max(bi.getWidth(), bi.getHeight())));

		tex_body_kd.drawImage(bi);

	}

	private boolean pregameReadyState = false;
	
	public PlayerEntity(long _id, Vec3 _pos, double _radius, int pindex){
		super(_id);
		position = _pos;
		radius = _radius;
		this.selfIndex = pindex;
		this.addMeshContexts(this.getBall());
	}

	public PlayerEntity(Vec3 _pos, double _radius) {
		super();
		position = _pos;
		radius = _radius;
		this.addMeshContexts(this.getBall());
		
		this.currentHealth = this.defaultHealth;
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
	public ItemContainer getInventory(){
		return inventory;
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
		mtl_body = new Material(mtl_body, tex_body_kd, null, null);

		MovableReferenceFrame mrf = new MovableReferenceFrame(this);
//		mrf.setOrientation(Quat.create(Math.PI, Vec3.j));

		MeshContext mc_body = new MeshContext(m_body, mtl_body, mrf);

		meshes.add(mc_body);

		// arms
		Mesh m_arm_l = Content.loadContent("resources/models/character/char2_arm_l.obj");
		Mesh m_arm_r = Content.loadContent("resources/models/character/char2_arm_r.obj");
		Material mtl_arm = new Material(Color.WHITE, new Color(0.2f, 0.2f, 0.2f), new Color(0.7f, 0.7f, 0.7f),
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
}
