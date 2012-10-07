package game;

import game.entity.WallEntity;

import initial3d.engine.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class FloorGenerator {

	private final int base = 10;//base size of mazes
	private final int incr = 10;//base increment for maze levels
	private final long seed;
	
	private HashMap<Integer, Floor> floorCache = new HashMap<Integer, Floor>();
	
	public FloorGenerator(long _seed){
		seed = _seed;
	}
	
	public Floor getFloor(int level){
		if(level<0){
			throw new IllegalArgumentException();
		}
		
		if(floorCache.containsKey(level)){
			return floorCache.get(level);
		}
		
		int size = level * incr + base;
		List<WallEntity> walls = new ArrayList<WallEntity>();
		Space[][] maze = generateMaze(size);
		
		//create a wall entity for each wall space in the maze
		for(int x=0; x<size; x++){
			for(int z=0; z<size; z++){
				if(maze[x][z].type==Space.WALL){
					walls.add(new WallEntity(Vec3.create(x, 0, z)));
				}
			}
		}
		
		//store and return the new floor
		Floor floor = new Floor(size, walls);
		floorCache.put(level, floor);
		return floor;
	}
	
	private Space[][] generateMaze(int size){
		Random rand = new Random(size * seed);
		
		if (size<4 || rand==null){
			throw new IllegalArgumentException();
		}
		
		Space[][] maze = new Space[size][size];
		for(int x=0; x<size; x++){
			for(int z=0; z<size; z++){
				maze[x][z] = new Space(x, z, Space.WALL);
			}
		}
		
//		addToMaze(maze[1][1], maze, rand);
		
		List<Space> valid = new ArrayList<Space>();
		valid.add(maze[1][1]);//add top left as the starting point
		
		while(!valid.isEmpty()){
			Space s = valid.remove((int)(rand.nextDouble() * valid.size()));
			s.type = Space.EMPTY;
			
			//check surrounding spaces and add them if they can be expanded to
			Space[] beside = new Space[]{
					maze[s.x-1][s.z], maze[s.x][s.z-1], 
					maze[s.x+1][s.z], maze[s.x][s.z+1]};
			
			for(Space r : beside){
				if(spaceValid(r, maze)){valid.add(r);}
			}
		}
		
		
		
		

		return maze;
	}
	
	private void addToMaze(Space _s, Space[][] _maze, Random _rand){
		List<Space> valid = new ArrayList<Space>();
		
		//check surrounding spaces and add them if they can be expanded to
		Space[] beside = new Space[]{
				_maze[_s.x-1][_s.z], _maze[_s.x][_s.z-1], 
				_maze[_s.x+1][_s.z], _maze[_s.x][_s.z+1]
		};
		
		for(Space r : beside){
			if(spaceValid(r, _maze)){valid.add(r);}
		}
		
		_s.type = Space.EMPTY;
		
		while(!valid.isEmpty()){
			Space next = valid.remove((int)(_rand.nextDouble() * valid.size()));
			addToMaze(next, _maze, _rand);
		}
	}
	
//	/**
//	 * Returns true if given Space is not on the edges of the maze
//	 * and is not surrounded by Spaces that are empty. Assumes the
//	 * values are not null.
//	 * 
//	 * @param maze
//	 * @param s
//	 * @return
//	 */
//	private boolean spaceValid(Space s, Space[][] maze){
//		if(maze==null||s==null){
//			throw new IllegalArgumentException();
//		}
//		
//		//if at the edge of the map, cannot be available
//		if (s.x<=0 || s.x>=maze.length-1 ||
//			s.z<=0 || s.z>=maze.length-1 ||
//			s.type==Space.EMPTY){
//			return false;
//		}
//		
//		//check all spaces around this space are not empty
//		if (maze[s.z-1][s.z].type == Space.EMPTY ||
//			maze[s.z][s.z-1].type == Space.EMPTY ||
//			maze[s.z+1][s.z].type == Space.EMPTY ||
//			maze[s.z][s.z+1].type == Space.EMPTY ){
//			return false;
//		}
//		
//		return true;
//	}
	
	
	private boolean spaceValid(Space s, Space[][] maze){
		if(maze==null||s==null){
			throw new IllegalArgumentException();
		}
		
		//if at the edge of the map, cannot be available
		if (s.x<=0 || s.x>=maze.length-1 ||
			s.z<=0 || s.z>=maze.length-1 ||
			s.type==Space.EMPTY){
			return false;
		}
		
		//check that if this space will join two other spaces
		int emptySpaces = 0;
		//check surrounding spaces and add them if they can be expanded to
		Space[] beside = new Space[]{
				maze[s.x-1][s.z], maze[s.x][s.z-1], 
				maze[s.x+1][s.z], maze[s.x][s.z+1]
		};
		
		for(Space r : beside){
			if(r.type == Space.EMPTY){emptySpaces++;}
		}
		
		return emptySpaces<2;
	}

	private class Space{
		static final int WALL = 1;
		static final int EMPTY = 0;
		
		final int x, z;
		int type;
		Space(int _x, int _z, int _type){
			x = _x; z = _z; type = _type;
		}
	}
	
	public static void main(String[] args){
		FloorGenerator fg = new FloorGenerator(32l);
		final int size = 10;
		Space[][] maze = fg.generateMaze(size);
		for(int x=0; x<size; x++){
			for(int z=0; z<size; z++){
				System.out.printf("%s", (maze[x][z].type == Space.EMPTY) ? "-":"8");
			}
			System.out.printf("\n");
		}
	}
}
