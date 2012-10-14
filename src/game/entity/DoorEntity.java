package game.entity;

import game.bound.Bound;
import game.entity.moveable.MoveableEntity;
import game.entity.trigger.TriggerEntity;
import game.level.Level;
import initial3d.engine.Quat;
import initial3d.engine.Vec3;

public class DoorEntity extends MoveableEntity{
	
	private TriggerEntity doorTrigger;
	
	public DoorEntity(long _id, Vec3 _pos, Quat _orient){
		super(_id);
		position = _pos;
		orientation = _orient;
	}
	
	public DoorEntity(Vec3 _pos, Quat _orient){
		position = _pos;
		orientation = _orient;
	}
	
	public void addToLevel(Level level){
		level.addEntity(this);
		level.addEntity(doorTrigger);
	}
	
	@Override
	public void poke() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isSolid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected Bound getBound(Vec3 position) {
		// TODO Auto-generated method stub
		return null;
	}

}
