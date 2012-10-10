package game.entity;

import java.util.*;

import game.bound.Bound;
import initial3d.engine.*;

public abstract class Entity implements ReferenceFrame {
	
	public final long id;
	
	protected Vec3 position = Vec3.zero;
	protected Quat orientation = Quat.one;
	
	private List<MeshContext> meshes = new ArrayList<MeshContext>();
	
	public Entity(long _id){
		id = _id;
	}
	
	@Override
	public ReferenceFrame getParent() {
		return SCENE_ROOT;
	}

	@Override
	public Vec3 getPosition() {
		return this.position;
	}

	@Override
	public Quat getOrientation() {
		return this.orientation;
	}
	
	public void setPosition(Vec3 _pos) {
		position = _pos;
	}
	
	public void setOrientation(Quat _orient) {
		orientation = _orient;
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
	 * Returns the bound of the current position of this entity. 
	 * @return The bounding volume for this entity
	 */
	public Bound getBound(){
		return getBound(position);
	}
	
	/**Intended as an update method TODO fill in the description later*/
	public abstract void poke();
	
	/**
	 * Returns the bound of the given position of this entity.
	 * Allows the caller to specify the position of the entity
	 * and get the bounding volume at the point.
	 * 
	 * @return The bounding volume for this entity at given location
	 */
	protected abstract Bound getBound(Vec3 position);
	
	public abstract boolean isSolid();
}
