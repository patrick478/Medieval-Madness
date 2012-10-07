package game;

import initial3d.engine.Color;
import initial3d.engine.Material;
import initial3d.engine.Mesh;
import initial3d.engine.MeshContext;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import comp261.modelview.MeshLoader;


public class WallEntity extends Entity {
	public WallEntity()
	{
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
	
	// TODO: Work-in-progress
//	public List<MeshContext> getWall()
//	{
//		Material mat = new Material(Color.BLACK, new Color(0.6f, 0.1f, 0.1f), new Color(0.3f, 0.3f, 0.3f), new Color(0f, 0f, 0f), 1f, 1f);
//		List<Mesh> m = null;
//		List<MeshContext> meshes = new ArrayList<MeshContext>();
//		FileInputStream fis;
//		try {
//			fis = new FileInputStream("wall.txt");
//			 m = MeshLoader.loadComp261(fis);
//			fis.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		for(Mesh msh : m)
//			meshes.add(new MeshContext(msh, mat, this));
//
//		return meshes;
//	}
}
