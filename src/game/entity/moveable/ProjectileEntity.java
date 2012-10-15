package game.entity.moveable;

import java.util.ArrayList;
import java.util.List;

import game.Game;
import game.bound.Bound;
import game.bound.BoundingSphere;
import game.entity.trigger.DynamicTriggerEntity;
import game.entity.trigger.TriggerEntity;
import game.event.AvoidEvent;
import game.event.ContactEvent;
import game.event.DeltaHealthEvent;
import game.event.RemoveEntityEvent;
import game.level.Level;
import game.modelloader.Content;
import initial3d.engine.Color;
import initial3d.engine.Material;
import initial3d.engine.Mesh;
import initial3d.engine.MeshContext;
import initial3d.engine.Quat;
import initial3d.engine.Vec3;

public class ProjectileEntity extends MoveableEntity {

	private final TriggerEntity trigger;
	
	public ProjectileEntity(long _id, long _parid, int _delta, Vec3 _pos, Vec3 _vel, Quat _ori){
		super(_id);
		position = _pos;
		linVelocity = _vel;
		orientation = _ori;
		trigger = new DynamicTriggerEntity(new AvoidEvent(_parid), this);
		trigger.addEvent(new ContactEvent());
		trigger.addEvent(new RemoveEntityEvent(this.id));
		trigger.addEvent(new RemoveEntityEvent(trigger.id));
		trigger.addEvent(new DeltaHealthEvent(_delta));
		
		this.addMeshContexts(this.getBall());
	}
	
	public ProjectileEntity(long _parid, int _delta, Vec3 _pos, Vec3 _vel, Quat _ori){
		super();
		position = _pos;
		linVelocity = _vel;
		orientation = _ori;
		trigger = new DynamicTriggerEntity(new AvoidEvent(_parid), this);
		trigger.addEvent(new ContactEvent());
		trigger.addEvent(new RemoveEntityEvent(this.id));
		trigger.addEvent(new RemoveEntityEvent(trigger.id));
		trigger.addEvent(new DeltaHealthEvent(_delta));
		
		this.addMeshContexts(this.getBall());
	}
	
	private List<MeshContext> getBall(){
		Material mat = new Material(Color.BLACK, Color.BLACK, Color.BLACK, Color.YELLOW, 1f, 1f);		
		Mesh m = Content.loadContent("sphere.obj");
		MeshContext mc = new MeshContext(m, mat, this);
		mc.setScale(0.025);
		List<MeshContext> meshes = new ArrayList<MeshContext>();
		meshes.add(mc);
		
		return meshes;
	}

	@Override
	public void addToLevel(Level _level){
		super.addToLevel(_level);
		trigger.addToLevel(_level);
	}
	
	@Override
	public void poke() {}

	@Override
	protected Bound getBound(Vec3 position) {
		return new BoundingSphere(this.getPosition(), 0.1);
	}

	@Override
	public boolean isSolid() {
		return false;
	}
}
