package joshsextravaganza;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import comp261.modelview.MeshLoader;

import initial3d.engine.Engine;
import initial3d.engine.Material;
import initial3d.engine.Mesh;
import initial3d.engine.MeshContext;
import initial3d.engine.MeshLOD;
import initial3d.engine.Vec3;
import initial3d.linearmath.Matrix;
import initial3d.linearmath.TransformationMatrix4D;

public class TerrainTest {

	public static int SIZE = 64;

	private static double mapHeight(Perlin p, double x, double z) {
		return p.getNoise(x / (double) SIZE, z / (double) SIZE, 0, 8) * 8;
	}

	public static Mesh getMesh() {

		Perlin p = new Perlin(32);

		MeshLOD mlod0 = new MeshLOD(SIZE * SIZE * 2, 3, SIZE * SIZE * 2, 1, SIZE * SIZE * 2, 1);

		int[][] ind = new int[(SIZE + 1)][(SIZE + 1)];

		for (int z = 0; z <= SIZE; z++) {
			for (int x = 0; x <= SIZE; x++) {
				double h = mapHeight(p, x, z);
				ind[x][z] = mlod0.addVertex(x * 2, h, z * 2);

				// try to construct normal from noise pseudo-derivative
				Vec3 d0 = Vec3.create(-0.5, mapHeight(p, x - 0.25, z - 0.25) - h, -0.5).unit();
				Vec3 d1 = Vec3.create(-0.5, mapHeight(p, x - 0.25, z + 0.25) - h, 0.5).unit();
				Vec3 d2 = Vec3.create(0.5, mapHeight(p, x + 0.25, z + 0.25) - h, 0.5).unit();
				Vec3 d3 = Vec3.create(0.5, mapHeight(p, x + 0.25, z - 0.25) - h, -0.5).unit();

				Vec3 normal = (d0.cross(d1).add(d1.cross(d2)).add(d2.cross(d3)).add(d3.cross(d0))).unit();

				mlod0.addNormal(normal.x, normal.y, normal.z);
			}
		}

		Random r = new Random(32);

		// add polygons by theorized indexed values ^^;
		for (int z = 0; z < SIZE; z++) {
			for (int x = 0; x < SIZE; x++) {
				int[] tri0, tri1;

				if (r.nextBoolean()) {
					tri0 = new int[] { ind[x + 1][z], ind[x][z], ind[x + 1][z + 1] };
					tri1 = new int[] { ind[x][z], ind[x][z + 1], ind[x + 1][z + 1] };
					mlod0.addPolygon(tri0, null, tri0, null);
					mlod0.addPolygon(tri1, null, tri1, null);
				} else {
					tri0 = new int[] { ind[x + 1][z], ind[x][z], ind[x][z + 1] };
					tri1 = new int[] { ind[x + 1][z], ind[x][z + 1], ind[x + 1][z + 1] };
					mlod0.addPolygon(tri0, null, tri0, null);
					mlod0.addPolygon(tri1, null, tri1, null);
				}

			}
		}

		Mesh mesh = new Mesh();
		mesh.add(mlod0);

		return mesh;
	}

	public static Mesh getBox() {

		MeshLOD mlod0 = new MeshLOD(10, 4, 10, 1, 2, 1);

		mlod0.addVertex(0, 0, 0);
		mlod0.addVertex(1, 0, 0);
		mlod0.addVertex(0, 1, 0);
		mlod0.addVertex(0, 0, 1);
		mlod0.addVertex(1, 1, 0);
		mlod0.addVertex(1, 0, 1);
		mlod0.addVertex(0, 1, 1);
		mlod0.addVertex(1, 1, 1);

		mlod0.addPolygon(new int[] { 2, 1, 3, 5 }, null, null, null);
		mlod0.addPolygon(new int[] { 6, 4, 1, 2 }, null, null, null);
		mlod0.addPolygon(new int[] { 1, 4, 7, 3 }, null, null, null);
		mlod0.addPolygon(new int[] { 6, 2, 5, 8 }, null, null, null);
		mlod0.addPolygon(new int[] { 5, 3, 7, 8 }, null, null, null);
		mlod0.addPolygon(new int[] { 4, 6, 8, 7 }, null, null, null);

		Mesh mesh = new Mesh();
		mesh.add(mlod0);

		return mesh;
	}

	public static void main(String[] args) throws Exception {

		// TERRAIN
		Mesh terr_mesh = TerrainTest.getMesh();
		Material terr_mtl = new Material();
		terr_mtl.kd[0] = 0.1f;
		terr_mtl.kd[1] = 0.5f;
		terr_mtl.kd[2] = 0.1f;
		terr_mtl.ks[0] = 0.1f;
		terr_mtl.ks[1] = 0.1f;
		terr_mtl.ks[2] = 0.1f;
		terr_mtl.ka[1] = 0.3f;
		MeshContext terr_mc = new MeshContext(terr_mesh, terr_mtl);

		// BOX / BALL / MONKEY !!!
		List<Mesh> entity_meshlist = new ArrayList<Mesh>();
		entity_meshlist.add(TerrainTest.getBox());

		FileInputStream fis = new FileInputStream("ball.txt");
		entity_meshlist = MeshLoader.loadComp261(fis);
		fis.close();

		Material entity_mtl = new Material();
		entity_mtl.kd[0] = 0.6f;
		entity_mtl.kd[1] = 0.1f;
		entity_mtl.kd[2] = 0.1f;
		entity_mtl.ks[0] = 0.3f;
		entity_mtl.ks[1] = 0.3f;
		entity_mtl.ks[2] = 0.3f;
		entity_mtl.ka[0] = 0.6f;
		List<MeshContext> mclist = new ArrayList<MeshContext>();
		for (Mesh m : entity_meshlist) {
			mclist.add(new MeshContext(m, entity_mtl));
		}

		final int WIDTH = 848;
		final int HEIGHT = 480;

		Engine eng = new Engine(WIDTH, HEIGHT, true);
		eng.start();

		eng.addMeshContext(terr_mc);
		for (MeshContext mc : mclist) {
			eng.addMeshContext(mc);
		}
		eng.getCamera().setPosition(Vec3.create(TerrainTest.SIZE / 2, 10, TerrainTest.SIZE / 2));

		double[][] temp = Matrix.create(4, 4);
		double[][] xform = Matrix.createIdentity(4);
		double[][] selfcentre = Matrix.createIdentity(4);
		double[][] rotateX = Matrix.createIdentity(4);
		double[][] rotateY = Matrix.createIdentity(4);
		double[][] translate = Matrix.create(4, 4);
		// TransformationMatrix4D.translate(selfcentre, -0.5, -0.5, -0.5);
		TransformationMatrix4D.rotateY(rotateY, Math.PI / 4);
		for (MeshContext mc : mclist) {
			mc.setTransform(translate);
		}
		
		double x = 0, z = 0;
		Perlin p = new Perlin(32);
		
		
		while (true) {
			double last = mapHeight(p, (int)x, (int)z);
			double next = mapHeight(p, (int)x+1, (int)z+1);
			
			TransformationMatrix4D.translate(translate, x * 2,
					((x-(int)x)*(next-last)+last) + 0.9, z * 2);
			
//			TransformationMatrix4D.translate(translate, x * 2,
//					8 * p.getNoise(x / (double) SIZE, z / (double) SIZE, 0, 8) + 0.5, z * 2);
			TransformationMatrix4D.rotateX(rotateX, x * 4);

			Matrix.multiplyChain(temp, xform, selfcentre, rotateX, rotateY, translate);
			for (MeshContext mc : mclist) {
				mc.setTransform(xform);
			}
			
			x += 0.01;
			z += 0.01;

			Thread.sleep(15);
		}
	}
}
