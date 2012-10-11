package game.level;

import game.entity.Entity;

import java.util.ArrayList;

public class LevelGenerator {

	private final long seed;
	private final FloorGenerator floorGen;
	
	
	public LevelGenerator(long _seed){
		seed = _seed;
		floorGen = new FloorGenerator(_seed);
	}
	
	public Level getLevel(int _level){
		Floor floor = floorGen.getFloor(_level);
		
		//TODO create the list of entities here and add them to a list
		
		Level level = new Level(floor, new ArrayList<Entity>());
		return level;
	}
}
