package game.level.impl;

import initial3d.engine.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import game.entity.Entity;
import game.entity.moveable.DoorEntity;
import game.level.AbstractLevelPlanner;
import game.level.Floor;
import game.level.Level;
import game.level.Space;

public class DefualtLevel extends AbstractLevelPlanner{

	public DefualtLevel(long _seed) {
		super(_seed);
	}

	@Override
	public void designLevel(Floor _floor) {
		Random rand = new Random(_floor.getSize()*getSeed());
		List<Spot> deadEnds = new ArrayList<Spot>();
		List<Spot> corridors = new ArrayList<Spot>();
		
		for(int x=1; x < _floor.getSize()-1; x++){
			for(int z=1; z < _floor.getSize()-1; z++){
				Spot s = new Spot (x, z);
				if(isDeadEnd(s, _floor)){
					deadEnds.add(s);
				}else if(isCorridor(s, _floor)){
					corridors.add(s);
				}
			}
		}
		
		Spot keySpot = null;
		Spot doorSpot = null;
		do{
			keySpot = deadEnds.get((int) (deadEnds.size()*rand.nextDouble()));
			doorSpot = corridors.get((int) (corridors.size()*rand.nextDouble()));
		}while(keyReachable(_floor, keySpot, doorSpot, rand));
		
		List<Entity> entities = new ArrayList<Entity>();
		
//		entities.add(new DoorEntity(Vec3.create(doorSpot.x, 0, doorSpot.z)));
//		entities.add(new DoorEntity(Vec3.create(doorSpot.x, 0, doorSpot.z)));
		
//		Level level = new Level();
	}
	
	private boolean keyReachable(Floor _floor, Spot keySpot, Spot doorSpot, Random rand){
		boolean[][] visited = new boolean[_floor.getSize()][_floor.getSize()];
		List<Spot> map = new ArrayList<Spot>();
		map.add(new Spot(1, 1));
		
		
		while(!map.isEmpty()){
			Spot s = map.remove(map.size());
			if(_floor.getData()[s.x][s.z].type!=Space.EMPTY
				||(doorSpot.x==s.x && doorSpot.z==s.z)){
				continue;
			}
			
			if(keySpot.x==s.x && keySpot.z==s.z){
				return true;
			}
			
			for(int u=-1; u<=1; u++){
				for(int v=-1; v<=1; v++){
					if(visited[s.x+u][s.z+v]){
						continue;
					}else{
						map.add(new Spot(s.x+u, s.z+v));
						visited[s.x+u][s.z+v] = true;
					}
				}
			}
		}
		return false;
	}
	
	private boolean isDeadEnd(Spot s, Floor _floor) {
		//if the centre square is NOT empty
		if(_floor.getData()[s.x][s.z].type!=Space.EMPTY){
			return false;
		}
		
		int openSpace = 0;
		for(int u=-1; u<=1; u++){
			for(int v=-1; v<=1; v++){
				if((u+v)%2!=0 && _floor.getData()[s.x+u][s.z+v].type==Space.EMPTY){
					openSpace++;
				}
			}
		}
		//requires the total amount of empty spaces in 3x3 to be 2, including the center square
		return openSpace == 2;
	}
	
	private boolean isCorridor(Spot s, Floor _floor) {
		//if the center tile is not a corridor
		if(!(_floor.getData()[s.x][s.z].type==Space.EMPTY)){
			return false;
		}
		boolean horz = true, vert = true;
		
		//checking the horizontal plane of empty corridors
		for(int u=-1; u<=1; u++){
			for(int v=-1; v<=1; v++){
				//if the middle horz row is NOT empty
				if(u==0 && _floor.getData()[s.x+u][s.z+v].type!=Space.EMPTY){
					horz = false;
				//or the anything but the horz row IS empty
				}else if(_floor.getData()[s.x+u][s.z+v].type==Space.EMPTY){
					horz = false;
				}
				//if the middle vert column is NOT empty
				if(v==0 && _floor.getData()[s.x+u][s.z+v].type!=Space.EMPTY){
					vert = false;
				//or the anything but the vert column IS empty
				}else if(_floor.getData()[s.x+u][s.z+v].type==Space.EMPTY){
					vert = false;
				}
			}
		}
		return horz || vert;
	}
	
	private class Spot{
		int x, z;
		public Spot(int _x, int _z){
			x = _x; z = _z;
		}
	}
}
