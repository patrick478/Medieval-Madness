package game.entity;

import game.bound.Bound;
import game.bound.BoundingBox;
import game.bound.BoundingSphere;
import game.modelloader.Content;
import game.modelloader.WavefrontLoader;
import initial3d.engine.Color;
import initial3d.engine.Material;
import initial3d.engine.Mesh;
import initial3d.engine.MeshContext;
import initial3d.engine.Vec3;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import common.Timer;
import comp261.modelview.MeshLoader;

public class WallEntity extends Entity {
	
	private static final Vec3 wallSize = Vec3.create(1, 1, 1);
	
	public WallEntity(Vec3 _pos)
	{
		position = _pos;
//		bound = new BoundingBox(_pos.sub(wallSize.scale(0.5)), wallSize);
		bound = new BoundingSphere(_pos, 0.5);
		
		this.addMeshContexts(this.getWall());
		
		
	}
	
	public Bound getBound(){
		return bound;
	}
	
	// TODO: Work-in-progress
	public List<MeshContext> getWall()
	{
		Material mat = new Material(Color.WHITE, new Color(0.9f, 0.9f, 0.9f), new Color(0.8f, 0.8f, 0.8f), new Color(0f, 0f, 0f), 1f, 1f);		
		Mesh m = Content.loadContent("cube2.obj");
		MeshContext mc = new MeshContext(m, mat, this);
//		mc.setScale(1.001d);
		List<MeshContext> meshes = new ArrayList<MeshContext>();
		meshes.add(mc);
		
		return meshes;
	}

	@Override
	public void poke() {
		// TODO Auto-generated method stub
		
	}
}
