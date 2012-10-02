package common.entity;

import java.util.ArrayList;
import java.util.List;

import initial3d.engine.Color;
import initial3d.engine.Material;
import initial3d.engine.Mesh;
import initial3d.engine.MeshContext;
import initial3d.engine.Quat;
import initial3d.engine.ReferenceFrame;
import initial3d.engine.Vec3;

public abstract class Entity implements ReferenceFrame{
	public final long id;
	
	//position of the very center of the entity much like
	//the center of mass if mass was evenly distributed
	protected Vec3 position = Vec3.zero;
	protected Quat orientation = Quat.one;
	
	//radius to determine the bounds (box and sphere)
	//y-component determines half the height of the entity
	protected Vec3 radius;
	protected EntityType type;
	
	public Entity(Vec3 _radius, long newid) {
		radius = _radius;
		
		//TODO seriously need some change...
		id = newid;
	}
	
	@Override
	public ReferenceFrame getParent() {
		return SCENE_ROOT;
	}

	@Override
	public Vec3 getPosition() {
		return position;
	}
	
	@Override
	public Quat getOrientation() {
		return orientation;
	}
	
	public BoundingBox getBound(){
		return null;
	}
	
	private List<MeshContext> meshes = new ArrayList<MeshContext>();
	
	//TODO hackish method to get things moving
	public void setMeshContexts(List<Mesh> _meshes){
		Material mtl = new Material(Color.BLACK, new Color(0.6f, 0.1f, 0.1f), new Color(0.3f, 0.3f, 0.3f),
				new Color(0f, 0f, 0f), 1f, 1f);
		for(Mesh m : _meshes){
			meshes.add(new MeshContext(m, mtl, this ));
		}
	}
	
	public List<MeshContext> getMeshContexts(){
		return meshes; //TODO
	}
}
