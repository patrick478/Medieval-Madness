package game.entity.moveable;

import java.util.ArrayList;
import java.util.List;

import game.bound.Bound;
import game.bound.BoundingSphere;
import game.modelloader.Content;
import initial3d.engine.Color;
import initial3d.engine.Material;
import initial3d.engine.Mesh;
import initial3d.engine.MeshContext;
import initial3d.engine.Vec3;

public class ProjectileEntity extends MoveableEntity {

	public ProjectileEntity(long _id, Vec3 _pos){
		super(_id);
		position = _pos;
		
		
		this.addMeshContexts(this.getBall());
	}
	
	
	private List<MeshContext> getBall(){
		Material mat = new Material(Color.RED, Color.RED, new Color(0.5f, 0.5f, 0.5f), new Color(0f, 0f, 0f), 20f, 1f);		
		Mesh m = Content.loadContent("sphere.obj");
		MeshContext mc = new MeshContext(m, mat, this);
		mc.setScale(0.05);
		List<MeshContext> meshes = new ArrayList<MeshContext>();
		meshes.add(mc);
		
		mc.setHint(MeshContext.HINT_SMOOTH_SHADING);
		
		return meshes;
	}

	@Override
	public void poke() {
		
	}

	@Override
	protected Bound getBound(Vec3 position) {
		return new BoundingSphere(this.getPosition(), 0.1);
	}

	@Override
	public boolean isSolid() {
		return false;
	}
}
