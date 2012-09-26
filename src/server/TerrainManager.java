package server;

import java.util.*;

import common.map.Segment;
import common.map.SegmentGenerator;

public class TerrainManager {
	private SegmentGenerator sg = new SegmentGenerator(System.currentTimeMillis());
	private Map<String, Segment> segmentCache = new HashMap<String, Segment>();
	
	public Segment loadSegment(int x, int y)
	{
		String hash = Long.toString(x) + " " + Long.toString(y);
		if(segmentCache.containsKey(hash))
		{
			
		}
		
		Segment ts = sg.getSegment(x,  y);
		return ts;
	}
}
