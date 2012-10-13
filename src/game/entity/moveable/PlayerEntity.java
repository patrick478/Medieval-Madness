package game.entity.moveable;

import game.Game;
import game.bound.Bound;
import game.bound.BoundingSphere;
import game.modelloader.Content;
import initial3d.engine.Color;
import initial3d.engine.Material;
import initial3d.engine.Mesh;
import initial3d.engine.MeshContext;
import initial3d.engine.Vec3;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PlayerEntity extends MoveableEntity {
	
	private final double baseSpeed = 1;
	
	
	private final double radius;
	private int selfIndex = 0;
	
	public PlayerEntity(long _id, Vec3 _pos, double _radius, int pindex){
		super(_id);
		position = _pos;
		radius = _radius;
		this.selfIndex = pindex;
		this.addMeshContexts(this.getBall());
	}
	
	public PlayerEntity(Vec3 _pos, double _radius){
		super();
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
	
	public Color getColor()
	{
		switch(this.selfIndex)
		{
		case 0:
			return Color.GREEN;
		case 1:
			return Color.RED;
		case 2:
			return Color.BLUE;
		case 3:
			return Color.YELLOW;
		}
		return Color.WHITE;
	}
	
	private List<MeshContext> getBall(){
		Material mat = new Material(this.getColor(), this.getColor(), new Color(0.5f, 0.5f, 0.5f), new Color(0f, 0f, 0f), 20f, 1f);		
		Mesh m = Content.loadContent("sphere.obj");
		MeshContext mc = new MeshContext(m, mat, this);
		mc.setScale(0.125);
		List<MeshContext> meshes = new ArrayList<MeshContext>();
		meshes.add(mc);
		
		mc.setHint(MeshContext.HINT_SMOOTH_SHADING);
		
		return meshes;
	}	
}
