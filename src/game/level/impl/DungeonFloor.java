package game.level.impl;

import game.level.*;

public class DungeonFloor extends AbstractFloorPlanner{
	protected DungeonFloor(long _seed) {
		super(_seed);
	}

	@Override
	public Space[][] generateMaze(int size) {
		throw new RuntimeException("DungeonFloor not yet implemented");
	}
}
