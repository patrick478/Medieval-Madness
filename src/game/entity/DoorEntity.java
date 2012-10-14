package game.entity;

import game.bound.Bound;
import game.bound.BoundingBox;
import game.entity.moveable.MoveableEntity;
import game.entity.trigger.StaticTriggerEntity;
import game.entity.trigger.TriggerEntity;
import game.event.AbstractEvent;
import game.event.ContainsItemEvent;
import game.event.PlayerOnlyEvent;
import game.event.RemoveEntityEvent;
import game.item.Item;
import game.item.Key;
import game.level.Level;
import initial3d.engine.Quat;
import initial3d.engine.Vec3;

public class DoorEntity extends MoveableEntity{
	
	private TriggerEntity doorTrigger;
	private final Bound bound; 
	private static final Vec3 radius = Vec3.create(0.5, 0.5, 0.5);
	
	public DoorEntity(long _id, Vec3 _pos){
		super(_id);
		position = _pos;
		bound = new BoundingBox(position, radius);
		doorTrigger = new StaticTriggerEntity(new PlayerOnlyEvent(), new BoundingBox(position, radius.scale(2)));
		doorTrigger.addEvent(new RemoveEntityEvent(id));
		doorTrigger.addEvent(new RemoveEntityEvent(doorTrigger.id));
	}
	
	public DoorEntity(Vec3 _pos){
		position = _pos;
		bound = new BoundingBox(position, radius);
		doorTrigger = new StaticTriggerEntity(new PlayerOnlyEvent(), new BoundingBox(position, radius.scale(2)));
		doorTrigger.addEvent(new RemoveEntityEvent(id));
		doorTrigger.addEvent(new RemoveEntityEvent(doorTrigger.id));
	}
	
	@Override
	public void addToLevel(Level level){
		level.addEntity(this);
		level.addEntity(doorTrigger);
	}
	
	@Override
	public void poke() {}

	@Override
	public boolean isSolid() {
		return true;
	}

	@Override
	protected Bound getBound(Vec3 position) {
		return bound;
	}
	
	public Item generatekey(){
		Item key = new Key(null, "A key that unlocks a door somewhere...");//TODO change later
		AbstractEvent ae = doorTrigger.getEvent();
		doorTrigger = new StaticTriggerEntity(new ContainsItemEvent(key), new BoundingBox(position, radius.scale(2)));
		doorTrigger.addEvent(ae);
		return key;
	}
}
