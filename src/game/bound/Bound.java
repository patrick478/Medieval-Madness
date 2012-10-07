package game.bound;

import initial3d.engine.Vec3;

public abstract class Bound {
	
	//ideas TODO
//	
//	/**
//	 * Returns the a vector such that no other vector is closer to 
//	 * the given point and is still contained inside this bound. 
//	 * 
//	 * @param point the point in which to get the closest point to
//	 * @return the closest point toward the point
//	 */
//	public abstract Vec3 getNearestPoint(Vec3 point);
//	
//	/**
//	 * Returns whether this bound implementation intersects the
//	 * given bound implementation.
//	 * 
//	 * @param point
//	 * @return
//	 */
//	public abstract boolean intersects(Bound b);
	
	public abstract boolean intersects(BoundingBox b);
	public abstract boolean intersects(BoundingSphere b);
	public abstract boolean contains(Vec3 v);
}
