package game.entity.moveable;

import game.Game;
import game.bound.Bound;
import game.bound.BoundingSphere;
import game.entity.trigger.DynamicTriggerEntity;
import game.entity.trigger.TriggerEntity;
import game.event.DeltaHealthEvent;
import game.event.PlayerOnlyEvent;
import game.level.Level;
import initial3d.engine.Vec3;

public class SpikeBall extends EnemyEntity{

	private static final double radius = 0.25;
	
	private final Vec3 start;
	private final Vec3 goal;
	private boolean outgoing = false;
	
	private final double speed;
	
	private final int damage;
	private final int maxHealth;
	private int currentHealth;
	
	private TriggerEntity trigger;
	
	public SpikeBall(long _id, int _health, int _damage, Vec3 _start, Vec3 _goal,  double _speed){
		super(_id);
		maxHealth = _health;
		currentHealth = _health;
		damage = _damage;
		position = _start;
		start = _start;
		goal = _goal;
		speed = _speed;
		
		trigger = new DynamicTriggerEntity(new PlayerOnlyEvent(), this);
//		trigger.addEvent(new RemoveEntityEvent(trigger));
		trigger.addEvent(new DeltaHealthEvent(damage));
	}
	
	public SpikeBall(int _health, int _damage, Vec3 _start, Vec3 _goal, double _speed){
		super();
		maxHealth = _health;
		currentHealth = _health;
		damage = _damage;
		position = _start;
		start = _start;
		goal = _goal;
		speed = _speed;
		
		trigger = new DynamicTriggerEntity(new PlayerOnlyEvent(), this);
//		trigger.addEvent(new RemoveEntityEvent(trigger));
		trigger.addEvent(new DeltaHealthEvent(damage));
	}
	
	@Override
	public void poke(){
		super.poke();
		if(outgoing && getPosition().sub(goal).mag()<radius){
			updateMotion(getPosition(), start.sub(goal).unit().scale(speed), getOrientation(), getAngVelocity(), Game.time());
			outgoing = false;
		}else if(!outgoing && getPosition().sub(start).mag()<radius){
			updateMotion(getPosition(), goal.sub(start).unit().scale(speed), getOrientation(), getAngVelocity(), Game.time());
			outgoing = true;
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
		if (currentHealth <=0 ){
			Game.getInstance().removeEntity(id);
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
