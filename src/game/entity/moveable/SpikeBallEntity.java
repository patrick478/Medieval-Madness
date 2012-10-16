package game.entity.moveable;

import game.Game;
import game.bound.Bound;
import game.bound.BoundingSphere;
import game.entity.Entity;
import game.entity.trigger.DynamicTriggerEntity;
import game.entity.trigger.TriggerEntity;
import game.event.DeltaHealthEvent;
import game.event.PlayerOnlyEvent;
import game.level.Level;
import game.modelloader.Content;
import initial3d.engine.Color;
import initial3d.engine.Material;
import initial3d.engine.Mesh;
import initial3d.engine.MeshContext;
import initial3d.engine.Vec3;

public class SpikeBallEntity extends EnemyEntity{

	private static final double radius = 0.45;

	private final Vec3 origin;
	private final double speed;
	private final int damage;
	private final int maxHealth;
	private int currentHealth;
	
	private static final long updateTime = 500;
	private long attachTime;
	
	private TriggerEntity trigger;
	
	public SpikeBallEntity(long _id, int _health, int _damage, Vec3 _pos, double _speed){
		super(_id);
		maxHealth = _health;
		currentHealth = _health;
		damage = _damage;
		origin = _pos;
		position = _pos;
		speed = _speed;
		
		trigger = new DynamicTriggerEntity(Entity.freeID(), new PlayerOnlyEvent(), this);
		trigger.addEvent(new DeltaHealthEvent(damage));
		
		Material mat = new Material(Color.GRAY, new Color(0.3f, 0.25f, 0.3f), new Color(0.65f, 0.2f, 0.65f), new Color(0.3f, 0f,
				0.3f), 1f, 1f);
		Mesh m = Content.loadContent("resources/models/spikeball/spikeball.obj");
		MeshContext mc = new MeshContext(m, mat, this);
		mc.setHint(MeshContext.HINT_SMOOTH_SHADING);
		this.addMeshContext(mc);
	}
	
	@Override
	public void poke(){
		super.poke();
		if(Game.time() > attachTime + updateTime){
			attachTime = Game.time();
			double distance = Double.MAX_VALUE;
			Vec3 target = Vec3.zero;
			for(PlayerEntity p : Game.getInstance().getPlayers()){
				if(p.getPosition().sub(this.getPosition()).mag()<distance){
					target = p.getPosition();
				}
			}
			//TODO network issues?
			this.updateMotion(getPosition(), target.sub(getPosition()).unit().scale(speed), getOrientation(), getAngVelocity(), attachTime);
		}
	}
	
	@Override
	public void addToLevel(Level _level){
		_level.addEntity(this);
		_level.addEntity(trigger);
	}
	
	@Override
	public void applyHealthDelta(int _deltaHealth) {
		currentHealth += _deltaHealth;
		System.out.println("spike health :: "+currentHealth);
		if (currentHealth <=0 ){
			System.out.println("removing spikeball???");
			Game.getInstance().removeEntity(id);
			Game.getInstance().removeEntity(trigger.id);
		}
	}

	@Override
	public int getTotalHealth() {
		return maxHealth;
	}

	@Override
	public int getCurrentHealth() {
		return currentHealth;
	}

	@Override
	protected Bound getBound(Vec3 position) {
		return new BoundingSphere(position, radius);
	}

	@Override
	public void setCurrentHealth(int i) {
		this.currentHealth = i;
	}
}
