package game.event;

import initial3d.engine.Quat;
import initial3d.engine.Vec3;
import game.Game;
import game.entity.Entity;

public class MoveEvent extends AbstractEvent{

	private final long target_id;
	private final Vec3 target_pos;
	private final Quat target_orient;
	
	public MoveEvent(Game _game, long _target_id, Vec3 _target_pos, Quat _target_orient) {
		target_id = _target_id;
		target_pos = _target_pos;
		target_orient = _target_orient;
	}
	
	@Override
	protected boolean applyEvent(long _timeStamp, Entity _trigger) {
		Game.getInstance().moveEntity(target_id, target_pos);
		Game.getInstance().turnEntity(target_id, target_orient);
		return true;
	}
}
