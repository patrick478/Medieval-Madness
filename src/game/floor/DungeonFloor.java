package game.floor;

public class DungeonFloor extends AbstractFloorPlanner{

	
	
	protected DungeonFloor(long _seed) {
		super(_seed);
	}

	@Override
	public Space[][] generateMaze(int _size) {
		throw new RuntimeException("DungeonFloor not yet implemented");
	}
}
