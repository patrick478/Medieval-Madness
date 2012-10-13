package game.bound;

import initial3d.engine.Vec3;

/**
 * An immutable representation of a bounding volume
 * 
 * @author scottjosh
 *
 */
public abstract class Bound {
	
	/**
	 * Returns the normal of the intersection from this bound to
	 * the bound given. If there is no intersection between these
	 * 2 bounds, returns null.
	 * 
	 * @param _b Bound to intersect with
	 * @return The normal of the intersection or null
	 */
	public final Vec3 intersectNorm(Bound b){
		if(b==null){
			return null;
		}
		if(b instanceof BoundingBox){
			return intersects((BoundingBox)b);
		}else if(b instanceof BoundingSphere){
			return intersects((BoundingSphere)b);
		}
		throw new UnsupportedOperationException("Cannot collide these 2 implementations of bound");
	}
	
	public final boolean intersects(Bound b){
		if(b==null){
			return false;
		}
		if(b instanceof BoundingBox){
			return intersects((BoundingBox)b) != null;
		}else if(b instanceof BoundingSphere){
			return intersects((BoundingSphere)b) != null;
		}
		throw new UnsupportedOperationException("Cannot collide these 2 implementations of bound");
	}
	
	public abstract boolean contains(Vec3 v);
	
	/**
	 * Returns the normal of the intersection from this bound to
	 * the bound given. If there is no intersection between these
	 * 2 bounds, returns null.
	 * 
	 * @param _b Bound to intersect with
	 * @return The normal of the intersection or null
	 */
	public abstract Vec3 intersects(BoundingBox _b);
	
	/**
	 * Returns the normal of the intersection from this bound to
	 * the bound given. If there is no intersection between these
	 * 2 bounds, returns null.
	 * 
	 * @param _b Bound to intersect with
	 * @return The normal of the intersection or null
	 */
	public abstract Vec3 intersects(BoundingSphere b);
	public abstract Vec3 getPosition();
	public abstract Bound setPosition(Vec3 v);
}
