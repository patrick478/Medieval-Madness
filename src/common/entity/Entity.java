package common.entity;


public abstract class Entity {
	// I'm kind of just making this up.
	private EntityType type;
	
	protected long Chunk;
	protected float X, Y, Z;
	
	public abstract BoundingBox getBound();
	
}
