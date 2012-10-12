package game.entity.moveable;

import game.bound.Bound;
import game.bound.BoundingSphere;
import game.modelloader.Content;
import initial3d.engine.Color;
import initial3d.engine.Material;
import initial3d.engine.Mesh;
import initial3d.engine.MeshContext;
import initial3d.engine.Vec3;
import java.util.ArrayList;
import java.util.List;

public class PlayerEntity extends MoveableEntity {
	
	private static final double baseSpeed = 0.1;
	private final double radius;
	
	public PlayerEntity(long _id, Vec3 _pos, double _radius){
		super(_id);
		position = _pos;
		radius = _radius;
		this.addMeshContexts(this.getBall());
	}
	
	@Override
	protected Bound getBound(Vec3 position) {
		return new BoundingSphere(position, radius);
	}
	
	@Override
	public boolean isSolid() {
		return true;
	}
	
	public double getSpeed(){
		return baseSpeed;
	}
	
	private List<MeshContext> getBall(){
		Material mat = new Material(Color.RED, new Color(0.5f, 0.5f, 0.5f), new Color(0.5f, 0.5f, 0.5f), new Color(0f, 0f, 0f), 1f, 1f);		
		Mesh m = Content.loadContent("sphere.obj");
		MeshContext mc = new MeshContext(m, mat, this);
		mc.setScale(0.125);
		List<MeshContext> meshes = new ArrayList<MeshContext>();
		meshes.add(mc);
		
		mc.setHint(MeshContext.HINT_SMOOTH_SHADING | MeshContext.HINT_TWO_SIDED_LIGHTING);
		
		return meshes;
	}	
}
