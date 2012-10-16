package game.entity;

import java.util.*;

import game.bound.Bound;
import game.level.Level;
import initial3d.engine.*;

public abstract class Entity extends ReferenceFrame {
	
	private static HashMap<Long, Entity> entityID = new HashMap<Long, Entity>();
	private static long nextID = 0;
	
	public final long id;
	
	protected Vec3 position = Vec3.zero;
	protected Quat orientation = Quat.one;
	
	private boolean dead = false;
	private List<MeshContext> meshes = new ArrayList<MeshContext>();
	
	public static synchronized long freeID(){
		long id = System.nanoTime();
		while(entityID.containsKey(id)){
			id = System.nanoTime();
		}
		return id;
	}
	
	public Entity(long _id){
		super(ReferenceFrame.SCENE_ROOT);
		if(entityID.containsKey(_id)){
			throw new IllegalArgumentException("Cannot create entity with existing ID");
		}
		id = _id;
		entityID.put(id, this);
	}
	
	@Override
	public Vec3 getPosition() {
		return this.position;
	}
	
	@Override
	public Quat getOrientation() {
		return this.orientation;
	}
	
	/**
	 * Sets the position of the entity to the specified position.
	 * If parameter is null, does nothing.
	 * 
	 * @param _pos The position to set the entity to.
	 */
	public void setPosition(Vec3 _pos) {
		if(_pos!=null){
			position = _pos;
		}
	}
	
	/**
	 * Sets the absolute orientation of the entity to the specified 
	 * orientation. If parameter is null, does nothing.
	 * 
	 * @param _orient The orientation to set the entity to.
	 */
	public void setOrientation(Quat _orient) {
		if(_orient!=null){
			orientation = _orient;
		}
	}
	
	public void addMeshContext(MeshContext mesh)
	{
		if(mesh == null)
			throw new IllegalArgumentException();
		
		this.meshes.add(mesh);
	}
	
	//TODO hackish at best please remove at some point cheers (dj)
	public void addMeshContexts(List<MeshContext> newMeshes)
	{
		for(MeshContext m : newMeshes)
			this.addMeshContext(m);
	}
	
	public List<MeshContext> getMeshContexts()
	{
		return meshes;
	}
	
	/**
	 * Adds all Drawable components that make up the 
	 * graphical part of the entity to the given scene
	 * 
	 * @param s A non-null scene object
	 */
	public void addToScene(Scene s){
		if(s==null){
			throw new IllegalArgumentException("Paramter Scene cannot be null");
		}
		for(MeshContext m : meshes)
		{
			s.addDrawable(m);
		}
	}
	
	/**
	 * Adds all Drawable components that make up the 
	 * graphical part of the entity to the given scene
	 * 
	 * @param s A non-null scene object
	 */
	public void addToLevel(Level l){
		if(l==null){
			throw new IllegalArgumentException("Paramter Level cannot be null");
		}
		l.addEntity(this);
	}
	
	/**
	 * Returns the bound of the current position of this entity.
	 *  
	 * @return The bounding volume for this entity
	 */
	public Bound getBound(){
		return getBound(position);
	}
	
	/**
	 * Updates the current entity. Relies on being called regularly
	 * on every entity to perform their operations regularly.
	 */
	public abstract void poke();
	

	/**
	 * Returns whether the Entity is solid and should be used
	 * for collision detection on the physical level.
	 * 
	 * @return Whether the Entity is solid/impassable
	 */
	public abstract boolean isSolid();
	
	
	/**
	 * Returns the bound of the given position of this entity.
	 * Allows the caller to specify the position of the entity
	 * and get the bounding volume at the point.
	 * 
	 * @return The bounding volume for this entity at given location
	 */
	protected abstract Bound getBound(Vec3 position);

	public void kill() {
		this.dead = true;
	}
}
