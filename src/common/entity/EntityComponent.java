package common.entity;

import initial3d.engine.Quat;
import initial3d.engine.ReferenceFrame;
import initial3d.engine.Vec3;

public class EntityComponent implements ReferenceFrame{
	
	protected Vec3 basePosition = Vec3.zero;
	protected Quat baseOrientation = Quat.one;
	protected Quat orientation = Quat.one;
	protected ReferenceFrame parent;
	
	//takes the base position, base orientation and parent
	public EntityComponent(Vec3 _position, Quat _orientation, ReferenceFrame _parent){
		basePosition = _position;
		baseOrientation = _orientation;
		orientation = _orientation;
		parent = _parent;
	}
	
	@Override
	public ReferenceFrame getParent() {
		return parent;
	}
	@Override
	public Vec3 getPosition() {
		return basePosition;
	}
	@Override
	public Quat getOrientation() {
		return orientation;
	}
	
	
}
