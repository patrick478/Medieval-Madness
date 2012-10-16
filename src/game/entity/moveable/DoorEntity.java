package game.entity.moveable;

import game.bound.Bound;
import game.bound.BoundingBox;
import game.entity.trigger.StaticTriggerEntity;
import game.entity.trigger.TriggerEntity;
import game.event.AbstractEvent;
import game.event.ContainsItemEvent;
import game.event.PlayerOnlyEvent;
import game.event.RemoveEntityEvent;
import game.item.Item;
import game.item.Key;
import game.level.Level;
import game.modelloader.Content;
import initial3d.engine.Color;
import initial3d.engine.Material;
import initial3d.engine.Mesh;
import initial3d.engine.MeshContext;
import initial3d.engine.Quat;
import initial3d.engine.Vec3;

public class DoorEntity extends MoveableEntity{
	
	private TriggerEntity doorTrigger;
	private static final Vec3 radius = Vec3.create(0.5, 0.5, 0.5);
	
	public DoorEntity(long _id, Vec3 _pos){
		super(_id);
		position = _pos;
		doorTrigger = new StaticTriggerEntity(new PlayerOnlyEvent(), new BoundingBox(position, radius.scale(2)));
		doorTrigger.addEvent(new RemoveEntityEvent(id));
		doorTrigger.addEvent(new RemoveEntityEvent(doorTrigger.id));
		
		
		Material mat = new Material(Color.GRAY, new Color(0.3f, 0.3f, 0.3f), new Color(0.65f, 0.65f, 0.65f), Color.BLACK, 1f, 1f);
		Mesh m = Content.loadContent("resources/models/doorbars/doorbars2.obj");
		MeshContext mc = new MeshContext(m, mat, this);
		mc.setHint(MeshContext.HINT_SMOOTH_SHADING);
		addMeshContext(mc);
	}
	
	public DoorEntity(Vec3 _pos){
		position = _pos;
		doorTrigger = new StaticTriggerEntity(new PlayerOnlyEvent(), new BoundingBox(position, radius.scale(2)));
		doorTrigger.addEvent(new RemoveEntityEvent(id));
		doorTrigger.addEvent(new RemoveEntityEvent(doorTrigger.id));
		
		Material mat = new Material(Color.GRAY, new Color(0.3f, 0.3f, 0.3f), new Color(0.65f, 0.65f, 0.65f), Color.BLACK, 1f, 1f);
		Mesh m = Content.loadContent("resources/models/doorbars/doorbars2.obj");
		MeshContext mc = new MeshContext(m, mat, this);
		mc.setHint(MeshContext.HINT_SMOOTH_SHADING);
		addMeshContext(mc);
	}
	
	@Override
	public void addToLevel(Level level){
		level.addEntity(this);
		level.addEntity(doorTrigger);
	}

	@Override
	public boolean isSolid() {
		return true;
	}

	@Override
	protected Bound getBound(Vec3 position) {
		return new BoundingBox(position, radius);
	}
	
	public Item generatekey(){
		Item key = new Key(null, "A key that unlocks a door somewhere...");//TODO change later
		AbstractEvent ae = doorTrigger.getEvent();
		doorTrigger = new StaticTriggerEntity(new ContainsItemEvent(key), new BoundingBox(position, radius.scale(2)));
		doorTrigger.addEvent(ae);
		return key;
	}
}
