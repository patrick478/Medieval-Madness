package game.bound;

import initial3d.engine.Vec3;

/**
 * An immutable representation of a bounding volume
 * 
 * @author scottjosh
 *
 */
public abstract class Bound {
	
	public final boolean intersects(Bound b){
		if(b instanceof BoundingBox){
			return intersects((BoundingBox)b);
		}else if(b instanceof BoundingSphere){
			return intersects((BoundingSphere)b);
		}
		throw new UnsupportedOperationException("Cannot collide these 2 implementations of bound");
	}
	
	public abstract boolean intersects(BoundingBox b);
	public abstract boolean intersects(BoundingSphere b);
	public abstract boolean contains(Vec3 v);
	public abstract Vec3 getPosition();
	public abstract Bound setPosition(Vec3 v);
}
