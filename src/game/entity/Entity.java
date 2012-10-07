package game.entity;

import java.util.*;

import initial3d.engine.*;

public abstract class Entity implements ReferenceFrame {
	
	protected Vec3 position = Vec3.zero;
	protected Quat orientation = Quat.one;
	
	protected Vec3 radius;
	
	private List<MeshContext> meshes = new ArrayList<MeshContext>();
	
	public abstract void poke();
	
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
	
	public void addMeshContext(MeshContext mesh)
	{
		if(mesh == null)
			throw new IllegalArgumentException();
		
		this.meshes.add(mesh);
	}
	
	public void addMeshContexts(List<MeshContext> newMeshes)
	{
		for(MeshContext m : newMeshes)
			this.addMeshContext(m);
	}
	
	public List<MeshContext> getMeshContexts()
	{
		return meshes;
	}
	
	public void addToScene(Scene s)
	{
		for(MeshContext m : meshes)
		{
			s.addDrawable(m);
		}
	}
}
