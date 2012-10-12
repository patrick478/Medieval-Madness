package game.level;

import game.level.impl.OpenFloor;
import game.level.impl.RandomFloor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FloorGenerator {

	private final int base = 10;//base size of mazes
	private final int incr = 2;//base increment for maze levels
	private final long seed;
	
	private final HashMap<Integer, Floor> floorCache = new HashMap<Integer, Floor>();
	private final AbstractFloorPlanner floorPlan;
	
	public FloorGenerator(long _seed){
		seed = _seed;
		floorPlan = new OpenFloor(seed);
	//	floorPlan = new RandomFloor(seed);
		
	}
	
	public Floor getFloor(int level){
		if(level<0){
			throw new IllegalArgumentException();
		}
		
		if(floorCache.containsKey(level)){
			return floorCache.get(level);
		}
		//Vital to implementation, size must be different between levels
		int size = level * incr + base;
		Space[][] maze = floorPlan.generateMaze(size);
		
		//store and return the new floor
		Floor floor = new Floor(maze);
		floorCache.put(level, floor);
		return floor;
	}
}
