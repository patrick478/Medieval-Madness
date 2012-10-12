package game.entity.moveable;

import game.Game;
import game.bound.Bound;
import game.entity.Entity;
import initial3d.engine.Quat;
import initial3d.engine.Vec3;

/**
 * An abstract representation of an Entity that has the capability to move and
 * update itself in the world.
 * 
 * @author scottjosh
 * 
 */
public abstract class MoveableEntity extends Entity {

	protected Vec3 linVelocity = Vec3.zero;
	protected Vec3 angVelocity = Vec3.zero;
	private long lastUpdate = 0;

	public MoveableEntity(long _id) {
		super(_id);
	}

	protected double updateDelta() {
		return (Game.time() - lastUpdate) / 1000d;
	}

	@Override
	public void poke() {
		updateMotion(getPosition(), linVelocity, getOrientation(), angVelocity, Game.time());
	}

	@Override
	public Vec3 getPosition(){
		return position.add(linVelocity.scale(updateDelta()));
	}

	@Override
	public Quat getOrientation() {
		return orientation.mul(Quat.create(angVelocity.scale(updateDelta())));
	}

	@Override
	public void setPosition(Vec3 _pos) {
		updateMotion(_pos, linVelocity, getOrientation(), angVelocity, Game.time());
	}

	@Override
	public void setOrientation(Quat _orient) {
		updateMotion(getPosition(), linVelocity, _orient, angVelocity, Game.time());
	}

	public Vec3 getLinVelocity() {
		return linVelocity;
	}

	public Vec3 getAngVelocity() {
		return angVelocity;
	}

	/**
	 * Stops the motion of the MoveableEntity, should be called when the
	 * MoveableEntity's last position is the intended position.
	 */
	public void fix() {
		linVelocity = Vec3.zero;
	}

	/**
	 * Updates the position, linear velocity, orientation, angular velocity of
	 * the entity and allows it to regulate it's position by supplying it with
	 * timestamp. Does not accept null for any parameter.
	 * 
	 * @param _pos
	 *            The position of the MoveableEntity at the given time
	 * @param _linvel
	 *            The linear velocity of the MoveableEntity at the given time
	 * @param _orient
	 *            The orientation of the MoveableEntity at the given time
	 * @param _angvel
	 *            The angular velocity of the MoveableEntity at the given time
	 * @param _timeStamp
	 *            The time in which the update originally occurred.
	 */
	public void updateMotion(Vec3 _pos, Vec3 _linvel, Quat _orient, Vec3 _angvel, long _timeStamp) {
		if (_pos == null || _linvel == null || _orient == null || _angvel == null) {
			throw new IllegalArgumentException("Values passed cannot be null");
		}
		position = _pos;
		linVelocity = _linvel;
		orientation = _orient;
		angVelocity = _angvel;
		lastUpdate = _timeStamp;
	}

	/**
	 * Returns the bounding box of the next position of this particular entity.
	 * 
	 * @return The bounding volume at the next position for this movable entity
	 */
	public Bound getNextBound() {
		return getBound(getPosition());
	}
}
