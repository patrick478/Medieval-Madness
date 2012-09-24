package joshsextravaganza;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import soundengine.SimpleAudioPlayer;

import common.map.SegmentGenerator;
import comp261.modelview.MeshLoader;

import initial3d.Initial3D;
import initial3d.Texture;
import initial3d.engine.Color;
import initial3d.engine.Engine;
import initial3d.engine.Material;
import initial3d.engine.Mesh;
import initial3d.engine.MeshContext;
import initial3d.engine.MeshLOD;
import initial3d.engine.Vec3;
import initial3d.linearmath.Matrix;
import initial3d.linearmath.TransformationMatrix4D;

public class NewTerrainTest {

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
		
		SegmentGenerator sg = new SegmentGenerator(32);
		
		List<MeshContext> mc_list = new ArrayList<MeshContext>();
		for (int z = -1; z < 2; z++) {
			for (int x = -1; x < 2; x++){
				mc_list.add(sg.getSegment(x, z).getMeshContext());
			}
		}
		
		final int WIDTH = 848;
		final int HEIGHT = 480;

		Engine eng = new Engine(WIDTH, HEIGHT, true);
		eng.start();
		SimpleAudioPlayer.play("Daybreak.wav");
		for(MeshContext mc : mc_list){
			eng.addMeshContext(mc);
		}

		eng.getCamera().setPosition(Vec3.create(TerrainTest.SIZE / 2, 10, TerrainTest.SIZE / 2));

	}
}
