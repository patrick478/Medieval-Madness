package game.level;

import game.entity.Entity;

import java.util.List;

public abstract class AbstractLevelPlanner {

	private final long seed;
	
	public AbstractLevelPlanner(long _seed){
		seed = _seed;
	}
	public abstract List<Entity> populateFloor(int size);
}
