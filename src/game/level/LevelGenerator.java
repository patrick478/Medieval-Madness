package game.level;

import game.level.impl.EmptyLevel;

import java.util.HashMap;

public class LevelGenerator {

	private final long seed;
	private final FloorGenerator floorGen;
	
	private final HashMap<Integer, Level> levelCache = new HashMap<Integer, Level>();
	private final AbstractLevelPlanner levelPlan;
	
	public LevelGenerator(long _seed){
		seed = _seed;
		floorGen = new FloorGenerator(_seed);
		levelPlan = new EmptyLevel(_seed);
	}
	
	public Level getLevel(int _level){
		if(levelCache.containsKey(_level)){
			return levelCache.get(_level);
		}
		Floor floor = floorGen.getFloor(_level);
		Level level = levelPlan.designLevel(floor);
		
		levelCache.put(_level, level);
		return level;
	}
	
	public long getSeed()
	{
		return this.seed;
	}
}
