package common.map.worldgenerator;

import java.awt.Polygon;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapBuilder {
	
	private Map<Polygon, Center> polyMap = new HashMap<Polygon, Center>();
	private static final int SEG_NUM = 128;
	
	
	public MapBuilder(long seed, int regionSize){
		MapGenerator map = new MapGenerator(999999937, 512);
		map.run();
	
		double[][] heightMap = new double[SEG_NUM][SEG_NUM];
		
		List<Triangle> tList = map.getTriangles();
		
		for(int z = 0; z<SEG_NUM; z++){
			for(int x = 0; x<SEG_NUM; x++){
				Point p = new Point(x, z);
				for(Triangle t : tList){
					if(t.contains(p)){
						heightMap[x][z] = t.height(p);
						break;
					}
				}
			}
		}

	}
	
	
}
