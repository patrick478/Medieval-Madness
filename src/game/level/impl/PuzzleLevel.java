package game.level.impl;

import game.level.AbstractLevelPlanner;
import game.level.Floor;
import game.level.Level;
import game.level.Space;

public class PuzzleLevel extends AbstractLevelPlanner{
	
	public static final int DOOR_FLAG = 1;
	public static final int DEAD_END = 2;
	public static final int INTERSECTION = 3;

	public PuzzleLevel(long _seed) {
		super(_seed);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Level designLevel(Floor _floor) {
		int[][] mockLayout = new int[_floor.getSize()][_floor.getSize()];
		for(Space[] sa : _floor.getData()){
			for(Space s : sa){
				if(isDoor(s, _floor)){
					mockLayout[s.x][s.z] = DOOR_FLAG;
				} else if(isDeadEnd(s, _floor)){
					mockLayout[s.x][s.z] = DEAD_END;
				} else if(isIntersection(s, _floor)){
					mockLayout[s.x][s.z] = INTERSECTION;
				}
			}
		}
		
//		List<Spot> region = 
//		
//		
		
		return null;
	}
	
	
//	private List<Spot> getRegion(Spot _start, int[][] _layout, ){
//		
//	}
	
	
	
	
	private boolean isDoor(Space _s, Floor _floor){
		if(_s.x!=0 && _s.z!=0 && _s.x!=_floor.getSize()-1 && _s.z!=_floor.getSize()-1){
			//TODO
			return true;
		}
		return false;
	}

	
	private boolean isDeadEnd(Space s, Floor _floor) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private boolean isIntersection(Space s, Floor _floor) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private class Spot{
		int x, z;
		public Spot(int _x, int _z){
			x = _x; z = _z;
		}
		private PuzzleLevel getOuterType() {
			return PuzzleLevel.this;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			return (prime * (z + x))* z;
		}
		@Override
		public boolean equals(Object obj) {
			if (obj == null || getClass() != obj.getClass())
				return false;
			Spot other = (Spot) obj;
			if (x != other.x || z != other.z)
				return false;
			return true;
		}
	}
	
}
