package game.level.impl;

import game.level.AbstractFloorPlanner;
import game.level.Space;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomFloor extends AbstractFloorPlanner{

	public RandomFloor(long _seed) {
		super(_seed);
	}

	@Override
	public Space[][] generateMaze(int size) {
		Space[][] floor = new Space[size][size];
		List<Space> valid = new ArrayList<Space>();
		Random rand = new Random(size*seed);
		
		for(int x=0; x<size; x++){
			for(int z=0; z<size; z++){
				floor[x][z] = new Space(x, z, Space.WALL);
//				if(x!=0 && z!=0 && x!=size-1 && z!=size-1){
//					valid.add(floor[x][z]);
//				}
			}
		}
		
		valid.add(floor[1][1]);
		
		while(!valid.isEmpty()){
			Space s = valid.remove((int)(rand.nextDouble() * valid.size()));
			if(!validSpace(s, floor)){
				continue;
			}
			
			s.type = Space.EMPTY;
			
			valid.add(floor[s.x+1][s.z]);
			valid.add(floor[s.x][s.z+1]);
			valid.add(floor[s.x-1][s.z]);
			valid.add(floor[s.x][s.z-1]);
			
			
			
//			if(validSpace(s, floor)){
//				s.type = Space.EMPTY;
//			}
		}
		
		return floor;
	}
	
	private boolean validSpace(Space s, Space[][] floor){
		if(s.x==0 || s.z==0 || s.x==floor.length-1 || s.z==floor.length-1){
			return false;
		}
		
		int emptyAdjacent = 0;	
		int emptyDiagonal = 0;
		
		for(int x=-1; x<=1; x++){
			for(int z=-1; z<=1; z++){
				if (x==0 && z==0){
					continue;
				}else if (x==0 || z==0){
					emptyAdjacent += (floor[s.x+x][s.z+z].type == Space.EMPTY) ? 1:0;
				}else{
					emptyDiagonal += (floor[s.x+x][s.z+z].type == Space.EMPTY) ? 1:0;
				}
			}
		}
		
		// just to supress dat warning :P now 'emptyDiagonal' feels important
		emptyDiagonal = emptyDiagonal * 1;
		
		return emptyAdjacent < 2;// && emptyDiagonal==0;
	}

	public static void main(String[] args){
		RandomFloor rf = new RandomFloor(32l);
		final int size = 100;
		Space[][] maze = rf.generateMaze(size);
		for(int x=0; x<size; x++){
			for(int z=0; z<size; z++){
				System.out.printf("%s", (maze[x][z].type == Space.EMPTY) ? "   " : "|_|");
			}
			System.out.printf("\n");
		}
	}
}
