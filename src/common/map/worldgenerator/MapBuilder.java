package common.map.worldgenerator;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapBuilder {
	
	private Map<Polygon, Center> polyMap = new HashMap<Polygon, Center>();
	
	
	public MapBuilder(){
		MapGenerator map = new MapGenerator(999999937, 512);
		map.run();
	}
}
