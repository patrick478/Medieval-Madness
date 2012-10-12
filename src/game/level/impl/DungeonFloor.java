package game.level.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.level.*;

public class DungeonFloor extends AbstractFloorPlanner{
	
	private static final int EMPTY = 0;
	private static final int ROOM = 1;
	private static final int DOOR = 2;
	private static final int PASSAGE = 3;
	private static final int ERROR = 4;
	
	protected DungeonFloor(long _seed) {
		super(_seed);
	}

	@Override
	public Space[][] generateMaze(int size) {
		Random rand = new Random(size*seed);
		Space[][] floor = new Space[size][size];
		return floor;
	}
	
	private int[][] createDungeon(int _start_x, int _start_z, int _size, Random _rand){
		int[][] tempFloor = new int[_size][_size];
		List<Spot> stack = new ArrayList<Spot>();
		
		Spot start = new Spot(_start_x, _start_z);
		
		
		
		
		
		
		
		
		
		
		return tempFloor;
	}
	private class Spot{
		int x, z;
		public Spot(int _x, int _z){
			x = _x; z = _z;
		}
	}
}





















