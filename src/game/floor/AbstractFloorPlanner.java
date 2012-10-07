package game.floor;

public abstract class AbstractFloorPlanner {
	protected final long seed;
	
	protected AbstractFloorPlanner(long _seed){
		seed = _seed;
	}
	public abstract Space[][] generateMaze(int size);
}
