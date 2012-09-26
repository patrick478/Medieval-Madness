package common.map;

import initial3d.engine.Mesh;
import initial3d.engine.MeshLOD;
import initial3d.engine.Vec3;
import initial3d.engine.old.MeshContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import common.map.worldgenerator.MapGenerator;
import common.map.worldgenerator.Point;
import common.map.worldgenerator.Triangle;

public class SegmentGenerator {
	private final long seed;
	private final Perlin perlin;
	private final double[][] regionHeight;
	private static final int regionSize = 256; // segments per region
	private static final double frequency = 3; //noise frequency for segments
	private static final double heightScale = 100; //

	private Map<Long, Segment> segmentCache = new HashMap<Long, Segment>();
	
	public SegmentGenerator(long seed) {
		this.seed = seed;
		this.perlin = new Perlin(seed);
		regionHeight = new double[regionSize][regionSize];

		createRegion();
	}

	private void createRegion() {
		MapGenerator map = new MapGenerator(seed, regionSize);
		map.look();
		List<Triangle> tList = map.getTriangles();

		System.out.println("running tri list");

		for (int z = 0; z < regionSize; z++) {
			for (int x = 0; x < regionSize; x++) {
				Point p = new Point(x, z);
				for (Triangle t : tList) {
					if (t.contains(p)) {
						// System.out.println(p.toString());
						// System.out.println(t.toString());
						// System.out.println(t.height(p));
						regionHeight[x][z] = t.height(p);
						break;
					}
				}
			}
		}

		System.out.println("tri list finished");

	}

	public Segment getSegment(int posx, int posz) {
		
		//location id by long
		Long id = new Long((long)(posx<<32) + (long)(posz));
		//return if in the cache
		if(segmentCache.containsKey(id)){
			return segmentCache.get(id);
		}
		
		float[][] hm = new float[Segment.size + 3][Segment.size + 3];

		//if the region is made up from the values of the mapgenerator, use those values
		if (!(posx < 0) && !(posx >= regionSize - 1) && !(posz < 0)
				&& !(posz >= regionSize - 1)) {
			double topLeft = regionHeight[(int) posx][(int) posz];
			double topRight = regionHeight[(int) posx + 1][(int) posz];

			double botLeft = regionHeight[(int) posx][(int) posz + 1];
			double botRight = regionHeight[(int) posx + 1][(int) posz + 1];

			double leftStep = (botLeft - topLeft) / (hm.length - 3);
			double rightStep = (botRight - topRight) / (hm.length - 3);

			//sing the scanline algorithm to work out heightmap for the segment given
			for (int z = 0; z < hm.length; z++) {
				double start = topLeft + (leftStep * (z - 1));
				double end = topRight + (rightStep * (z - 1));

				double step = (end - start) / (hm.length - 3);

				for (int x = 0; x < hm.length; x++) {
					hm[x][z] = (float) (start + ((x - 1) * step))
							* (float) heightScale;
				}
			}

			//next applying perlin noise
//			for (int z = -1; z <= Segment.size + 1; z++) {
//				for (int x = -1; x <= Segment.size + 1; x++) {
//					hm[x + 1][z + 1] += (float) perlin.getNoise((x
//							/ (double) Segment.size + posx)
//							/ frequency, (z / (double) Segment.size + posz)
//							/ frequency, 0, 8);
//				}
//			}

		}

		//finally create the segment and place in the cache and return
		Segment s = new Segment(posx, posz, hm);
		segmentCache.put(id, s);
		return s;
	}
	
	public List<MeshContext> getAllSegmentsAsSomeReallyBigFuckingMeshes(){
		
		List<MeshContext> meshes = new ArrayList<MeshContext>();
		
		meshes.add(new MeshContext(getMeshPart(0, 0, 129, 129), Segment.terr_mtl));
		meshes.add(new MeshContext(getMeshPart(0, 127, 129, 129), Segment.terr_mtl));
		meshes.add(new MeshContext(getMeshPart(127, 0, 129, 129), Segment.terr_mtl));
		meshes.add(new MeshContext(getMeshPart(127, 127, 129, 129), Segment.terr_mtl));

		return meshes;
		
		
	}
	
	private Mesh getMeshPart(int _x, int _z, int xSize, int zSize){
		
		Mesh m = new Mesh();
		
		MeshLOD mLOD = new MeshLOD(xSize * zSize * 2, 3, xSize * zSize * 2,
				20, 1, 1);

		int[][] ind = new int[xSize][zSize];

		// first create vectors and normals
		for (int z = 0; z < zSize; z++) {
			for (int x = 0; x < xSize; x++) {
				double h = (regionHeight[_x+x][_z+z] * Segment.vertScale);
				
				System.out.println(h);

				ind[x][z] = mLOD.addVertex(
						(_x+x) * Segment.horzScale * Segment.size, 
						h*100, 
						(_z+z) * Segment.horzScale * Segment.size);
			}
		}

		Random r = new Random(32);

		int[] tri_vt_1a = new int[] { mLOD.addTexCoord(0, 1),
				mLOD.addTexCoord(1, 1), mLOD.addTexCoord(0, 0) };
		int[] tri_vt_1b = new int[] { mLOD.addTexCoord(1, 1),
				mLOD.addTexCoord(1, 0), mLOD.addTexCoord(0, 0) };
		int[] tri_vt_2a = new int[] { mLOD.addTexCoord(0, 1),
				mLOD.addTexCoord(1, 1), mLOD.addTexCoord(1, 0) };
		int[] tri_vt_2b = new int[] { mLOD.addTexCoord(0, 1),
				mLOD.addTexCoord(1, 0), mLOD.addTexCoord(0, 0) };

		// add polygons by theorized indexed values ^^;
		for (int z = 0; z < xSize-1; z++) {
			for (int x = 0; x < zSize-1; x++) {
				int[] tri0, tri1;

				if (r.nextBoolean()) {
					tri0 = new int[] { ind[x + 1][z], ind[x][z], ind[x + 1][z + 1] };
					tri1 = new int[] { ind[x][z], ind[x][z + 1], ind[x + 1][z + 1] };
					
					mLOD.addPolygon(tri0, tri_vt_1a, null, null);
					mLOD.addPolygon(tri1, tri_vt_1b, null, null);
					
					
				} else {
					tri0 = new int[] { ind[x + 1][z], ind[x][z], ind[x][z + 1] };
					tri1 = new int[] { ind[x + 1][z], ind[x][z + 1], ind[x + 1][z + 1] };
					
					mLOD.addPolygon(tri0, tri_vt_2a, null, null);
					mLOD.addPolygon(tri1, tri_vt_2b, null, null);
				}

			}
		}

		m.add(mLOD);
		
		return m;
	}
}