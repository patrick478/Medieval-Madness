package game.level;

public abstract class AbstractLevelPlanner {

	private final long seed;
	
	public AbstractLevelPlanner(long _seed){
		seed = _seed;
	}
	
	public long getSeed(){
		return this.seed;
	}
	
	public abstract void designLevel(Floor _floor);
}
