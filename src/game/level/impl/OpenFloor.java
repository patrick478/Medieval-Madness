package game.level.impl;

import game.level.AbstractFloorPlanner;
import game.level.Space;

public class OpenFloor extends AbstractFloorPlanner {
	
	public OpenFloor(long _seed){
		super(_seed);
	}
	
	@Override
	public Space[][] generateMaze(int size) {
		Space[][] floor = new Space[size][size];
		
		for(int x=0; x<size; x++){
			for(int z=0; z<size; z++){
				if(x==0||z==0||x==size-1||z==size-1){
					floor[x][z] = new Space(x, z, Space.WALL);
				}else{
					floor[x][z] = new Space(x, z, Space.EMPTY);
				}
			}
		}
		
		return floor;
	}

	public static void main(String[] args){
		OpenFloor of = new OpenFloor(32l);
		final int size = 10;
		Space[][] maze = of.generateMaze(size);
		for(int x=0; x<size; x++){
			for(int z=0; z<size; z++){
				System.out.printf("%s", (maze[x][z].type == Space.EMPTY) ? "-":"8");
			}
			System.out.printf("\n");
		}
	}
}
