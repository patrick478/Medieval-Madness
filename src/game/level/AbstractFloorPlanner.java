package game.level;


public abstract class AbstractFloorPlanner {
	protected final long seed;
	
	public AbstractFloorPlanner(long _seed){
		seed = _seed;
	}
	public abstract Space[][] generateMaze(int size);
}
