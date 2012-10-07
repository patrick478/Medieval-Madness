package game;

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
	
	private final Vec3 pos1;
	private final Vec3 pos2;
	private final double width;
	private final double height;
	
	private Bound bound;
	
	public WallEntity(Vec3 _pos1, Vec3 _pos2, double _width, double _height)
	{
		pos1 = _pos1;
		pos2 = _pos2;
		width = _width;
		height = _height;
		
		Vec3 width = Vec3.create(_width, _height, _width);
		if(pos1.x < pos2.x || pos1.z < pos2.z){
			width.neg();
		}
		bound = new BoundingBox(_pos1.add(width), _pos2.sub(width));
		
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
}
