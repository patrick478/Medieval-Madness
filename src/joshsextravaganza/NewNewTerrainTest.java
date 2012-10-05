package joshsextravaganza;

import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import soundengine.SimpleAudioPlayer;

import common.entity.Entity;
import common.entity.MovableEntity;
import common.entity.Player;
import common.map.Segment;
import common.map.SegmentGenerator;
import comp261.modelview.MeshLoader;

import initial3d.Initial3D;
import initial3d.Texture;
import initial3d.engine.*;

import initial3d.linearmath.Matrix;
import initial3d.linearmath.TransformationMatrix4D;

public class NewNewTerrainTest {

	public static int SIZE = 64;

//	private static double mapHeight(Perlin p, double x, double z) {
//		return p.getNoise(x / (double) SIZE, z / (double) SIZE, 0, 8) * 8;
//	}
//
//	public static Mesh getMesh() {
//
//		Perlin p = new Perlin(32);
//
//		MeshLOD mlod0 = new MeshLOD(SIZE * SIZE * 2, 3, SIZE * SIZE * 2, 20, SIZE * SIZE * 2, 1);
//
//		int[][] ind = new int[(SIZE + 1)][(SIZE + 1)];
//
//		for (int z = 0; z <= SIZE; z++) {
//			for (int x = 0; x <= SIZE; x++) {
//				double h = mapHeight(p, x, z);
//				ind[x][z] = mlod0.addVertex(x * 2, h, z * 2);
//
//				// try to construct normal from noise pseudo-derivative
//				Vec3 d0 = Vec3.create(-0.5, mapHeight(p, x - 0.25, z - 0.25) - h, -0.5).unit();
//				Vec3 d1 = Vec3.create(-0.5, mapHeight(p, x - 0.25, z + 0.25) - h, 0.5).unit();
//				Vec3 d2 = Vec3.create(0.5, mapHeight(p, x + 0.25, z + 0.25) - h, 0.5).unit();
//				Vec3 d3 = Vec3.create(0.5, mapHeight(p, x + 0.25, z - 0.25) - h, -0.5).unit();
//
//				Vec3 normal = (d0.cross(d1).add(d1.cross(d2)).add(d2.cross(d3)).add(d3.cross(d0))).unit();
//
//				mlod0.addNormal(normal.x, normal.y, normal.z);
//			}
//		}
//
//		Random r = new Random(32);
//
//		int[] tri_vt_1a = new int[] { mlod0.addTexCoord(0, 1), mlod0.addTexCoord(1, 1), mlod0.addTexCoord(0, 0) };
//		int[] tri_vt_1b = new int[] { mlod0.addTexCoord(1, 1), mlod0.addTexCoord(1, 0), mlod0.addTexCoord(0, 0) };
//		int[] tri_vt_2a = new int[] { mlod0.addTexCoord(0, 1), mlod0.addTexCoord(1, 1), mlod0.addTexCoord(1, 0) };
//		int[] tri_vt_2b = new int[] { mlod0.addTexCoord(0, 1), mlod0.addTexCoord(1, 0), mlod0.addTexCoord(0, 0) };
//
//		// add polygons by theorized indexed values ^^;
//		for (int z = 0; z < SIZE; z++) {
//			for (int x = 0; x < SIZE; x++) {
//				int[] tri0, tri1;
//
//				if (r.nextBoolean()) {
//					tri0 = new int[] { ind[x + 1][z], ind[x][z], ind[x + 1][z + 1] };
//					tri1 = new int[] { ind[x][z], ind[x][z + 1], ind[x + 1][z + 1] };
//					mlod0.addPolygon(tri0, tri_vt_1a, tri0, null);
//					mlod0.addPolygon(tri1, tri_vt_1b, tri1, null);
//				} else {
//					tri0 = new int[] { ind[x + 1][z], ind[x][z], ind[x][z + 1] };
//					tri1 = new int[] { ind[x + 1][z], ind[x][z + 1], ind[x + 1][z + 1] };
//					mlod0.addPolygon(tri0, tri_vt_2a, tri0, null);
//					mlod0.addPolygon(tri1, tri_vt_2b, tri1, null);
//				}
//
//			}
//		}
//
//		Mesh mesh = new Mesh();
//		mesh.add(mlod0);
//
//		return mesh;
//	}
//
//	public static Mesh getBox() {
//
//		MeshLOD mlod0 = new MeshLOD(10, 4, 10, 1, 2, 1);
//
//		mlod0.addVertex(0, 0, 0);
//		mlod0.addVertex(1, 0, 0);
//		mlod0.addVertex(0, 1, 0);
//		mlod0.addVertex(0, 0, 1);
//		mlod0.addVertex(1, 1, 0);
//		mlod0.addVertex(1, 0, 1);
//		mlod0.addVertex(0, 1, 1);
//		mlod0.addVertex(1, 1, 1);
//
//		mlod0.addPolygon(new int[] { 2, 1, 3, 5 }, null, null, null);
//		mlod0.addPolygon(new int[] { 6, 4, 1, 2 }, null, null, null);
//		mlod0.addPolygon(new int[] { 1, 4, 7, 3 }, null, null, null);
//		mlod0.addPolygon(new int[] { 6, 2, 5, 8 }, null, null, null);
//		mlod0.addPolygon(new int[] { 5, 3, 7, 8 }, null, null, null);
//		mlod0.addPolygon(new int[] { 4, 6, 8, 7 }, null, null, null);
//
//		Mesh mesh = new Mesh();
//		mesh.add(mlod0);
//
//		return mesh;
//	}

	public static void main(String[] args) throws Exception {
		
		MovableEntity ball = new Player(Vec3.zero, 123123123);
		ball.updateMotion(Vec3.create(10, 10, 10), Vec3.zero, Quat.one, Vec3.zero, System.currentTimeMillis());
		
		// TERRAIN
		SegmentGenerator sg = new SegmentGenerator(System.currentTimeMillis());
		Segment seg = sg.getSegment(0,  0);
		
//		Material terr_mtl = new Material(new Color(0.4f, 0.4f, 0.4f), new Color(0.1f, 0.1f, 0.1f), 1f);
//
//		final int terr_tex_size = 16;
//
//		Texture terr_tx = Initial3D.createTexture(terr_tex_size);
//
//		for (int u = 0; u < terr_tex_size; u++) {
//			for (int v = 0; v < terr_tex_size; v++) {
//				terr_tx.setTexel(u, v, 1f, 0.3f, (float) (Math.random() * 0.4 + 0.3), 0.3f);
//			}
//		}
//		terr_tx.composeMipMaps();
//		terr_tx.useMipMaps(true);
//
//		terr_mtl = new Material(terr_mtl, terr_tx, null, null);
//
//		MeshContext terr_mc = new MeshContext(terr_mesh, terr_mtl, ReferenceFrame.SCENE_ROOT);
		MeshContext terr_mc = seg.getMeshContext();
		
		FileInputStream fis = new FileInputStream("ball.txt");
		ball.setMeshContexts(MeshLoader.loadComp261(fis));
		fis.close();



		final int WIDTH = 848;
		final int HEIGHT = 480;

		RenderWindow rwin = RenderWindow.create(WIDTH, HEIGHT);

		SceneManager sman = new SceneManager(WIDTH, HEIGHT);
		sman.setDisplayTarget(rwin);
		rwin.setLocationRelativeTo(null);
		rwin.setVisible(true);

		Scene scene = new Scene();

		scene.addDrawable(terr_mc);
		scene.addDrawables(ball.getMeshContexts());

		sman.attachToScene(scene);

		MovableReferenceFrame camera_rf = new MovableReferenceFrame(ball);
		scene.getCamera().trackReferenceFrame(camera_rf);
		camera_rf.setPosition(Vec3.create(-10, 10, -10));
		camera_rf.setOrientation(Quat.create(Math.PI / 8, Vec3.i));
		
		camera_rf.setOrientation(camera_rf.getOrientation().mul(Quat.create(Math.PI / 4, Vec3.j)));
		//scene.getCamera().setFOV(Math.PI / 12);
		
		Vec3 vel = Vec3.zero;
		Vec3 location = Vec3.zero;
		double speed = 0.001;
		while(true)
		{
			if(rwin.getKey(KeyEvent.VK_UP))
			{
				vel = vel.setX(speed);
				vel = vel.setZ(speed);
			}
			else if(rwin.getKey(KeyEvent.VK_DOWN))
			{
				vel = vel.setX(-speed);
				vel = vel.setZ(-speed);
			}
			else
				vel = Vec3.zero;
			
			ball.updateMotion(location.setY(seg.getHeight(location.x, location.z)), vel, Quat.one, Vec3.zero, System.currentTimeMillis());
			location = location.add(vel);
		}
	}
}
