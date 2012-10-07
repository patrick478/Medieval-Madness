package game.entity;

import game.bound.Bound;
import game.bound.BoundingBox;
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
	
	private static final Vec3 wallSize = Vec3.create(1, 2, 1);
	private Bound bound;
	
	public WallEntity(Vec3 _pos)
	{
		position = _pos;
		bound = new BoundingBox(_pos, wallSize);
		
		this.addMeshContexts(this.getWall());
		
		
	}
	
	public List<MeshContext> getBall()
	{
		Material mat = new Material(Color.BLACK, new Color(0.6f, 0.1f, 0.1f), new Color(0.3f, 0.3f, 0.3f), new Color(0f, 0f, 0f), 1f, 1f);
		List<Mesh> m = null;
		List<MeshContext> meshes = new ArrayList<MeshContext>();
		FileInputStream fis;
		try {
			fis = new FileInputStream("ball.txt");
			 m = MeshLoader.loadComp261(fis);
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(Mesh msh : m)
			meshes.add(new MeshContext(msh, mat, this));

		return meshes;
	}
	
	public Bound getBound(){
		return bound;
	}
	
	// TODO: Work-in-progress
	public List<MeshContext> getWall()
	{
		Material mat = new Material(Color.BLACK, new Color(0.6f, 0.1f, 0.1f), new Color(0.3f, 0.3f, 0.3f), new Color(0f, 0f, 0f), 1f, 1f);		
		Mesh m = Content.loadContent("trumpet.obj");
		MeshContext mc = new MeshContext(m, mat, this);
		List<MeshContext> meshes = new ArrayList<MeshContext>();
		meshes.add(mc);
		
		return meshes;
	}

	@Override
	public void poke() {
		// TODO Auto-generated method stub
		
	}
}
