package game.level;

import game.level.impl.DefualtLevel;
import game.level.impl.EmptyLevel;

import java.util.HashMap;

public class LevelGenerator {

	private final long seed;
	private final FloorGenerator floorGen;
	private final AbstractLevelPlanner levelPlan;
	
	public LevelGenerator(long _seed){
		seed = _seed;
		floorGen = new FloorGenerator(_seed);
		levelPlan = new EmptyLevel(_seed);
//		levelPlan = new DefualtLevel(_seed);
	}
	
	public void loadLevel(int _level){
		Floor floor = floorGen.getFloor(_level);
		levelPlan.designLevel(floor);
	}
	
	public long getSeed()
	{
		return this.seed;
	}
}
