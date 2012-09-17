package comp261.modelview;

import initial3d.engine.Color;
import initial3d.engine.Engine;
import initial3d.engine.Material;
import initial3d.engine.Mesh;
import initial3d.engine.MeshContext;

import java.io.FileInputStream;


public class Test {

	public static void main(String[] args) throws Exception {

		FileInputStream fis = new FileInputStream("ball.txt");
		Mesh mesh = MeshLoader.loadComp261(fis).get(0);
		fis.close();

		Material mtl = new Material(new Color(0.6f, 0.1f, 0.1f), new Color(0.4f, 0.4f, 0.4f), 1f);
		
		MeshContext mc = new MeshContext(mesh, mtl);

		final int WIDTH = 1024;
		final int HEIGHT = 576;
		
		Engine eng = new Engine(WIDTH, HEIGHT, true);
		eng.start();
		
		eng.addMeshContext(mc);

		

	}

}
