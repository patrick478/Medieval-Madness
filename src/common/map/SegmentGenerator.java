package common.map;

import java.util.List;

import common.map.worldgenerator.MapGenerator;
import common.map.worldgenerator.Point;
import common.map.worldgenerator.Triangle;

public class SegmentGenerator {
	private final long seed;
	private final Perlin perlin;
	private final double[][] regionHeight;
	private final int regionSize = 256;// segments per region???

	public static final double frequency = 3;

	public static final double heightScale = 100;

	public SegmentGenerator(long seed) {
		this.seed = seed;
		this.perlin = new Perlin(seed);
		regionHeight = new double[regionSize][regionSize];

		createRegion();
	}

	private void createRegion() {
		MapGenerator map = new MapGenerator(999999937, regionSize);
		map.run();
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

	public Segment getSegment(long posx, long posz) {
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

		Segment s = new Segment(posx, posz, hm);

		return s;
	}
}