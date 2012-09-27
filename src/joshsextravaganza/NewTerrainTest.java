package joshsextravaganza;

import java.util.ArrayList;
import java.util.List;

import soundengine.SimpleAudioPlayer;

import common.map.SegmentGenerator;

import initial3d.engine.Vec3;
import initial3d.engine.old.Engine;
import initial3d.engine.old.MeshContext;

public class NewTerrainTest {

	public static void main(String[] args) throws Exception {
		
		SegmentGenerator sg = new SegmentGenerator(1234);
		
//		List<MeshContext> mc_list = new ArrayList<MeshContext>();
		
//		for (int z = 0; z <60 ; z+=10) {
//			mc_list.add(sg.getSegment(z, z).getMeshContext());
//		}
		
//		for (int z = 0; z <256 ; z++) {
//			for (int x = 0; x <256 ; x++){
//				mc_list.add(sg.getSegment(x, z).getMeshContext());
//			}
//		}
		
		final int WIDTH = 848;
		final int HEIGHT = 480;

		Engine eng = new Engine(WIDTH, HEIGHT, true);
		eng.start();
		SimpleAudioPlayer.play("Daybreak.wav");
		for(MeshContext mc : sg.getAllSegmentsAsSomeReallyBigFuckingMeshes()){
			eng.addMeshContext(mc);
		}
		eng.getCamera().setPosition(Vec3.create(TerrainTest.SIZE / 2, 10, TerrainTest.SIZE / 2));

	}
}
