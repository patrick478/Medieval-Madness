package game.floor;

import game.entity.WallEntity;

import initial3d.engine.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class FloorGenerator {

	private final int base = 10;//base size of mazes
	private final int incr = 10;//base increment for maze levels
	private final long seed;
	
	private HashMap<Integer, Floor> floorCache = new HashMap<Integer, Floor>();
	private List<AbstractFloorPlanner> floorPlans = new ArrayList<AbstractFloorPlanner>();
	
	public FloorGenerator(long _seed){
		seed = _seed;
		floorPlans.add(new RandomFloor(seed));
		floorPlans.add(new OpenFloor(seed));
//		floorPlans.add(new RandomFloor(seed));
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
		List<WallEntity> walls = new ArrayList<WallEntity>();
		Space[][] maze = floorPlans.get(level%(floorPlans.size())).generateMaze(size);
		
		//store and return the new floor
		Floor floor = new Floor(maze);
		floorCache.put(level, floor);
		return floor;
	}
}
