package game.level.impl;

import initial3d.engine.Vec3;

import java.util.ArrayList;
import java.util.List;

import game.entity.Entity;
import game.entity.moveable.DoorEntity;
import game.level.AbstractLevelPlanner;
import game.level.Floor;
import game.level.Level;
import game.level.Space;

public class ClosedEndLevel extends AbstractLevelPlanner{

	public ClosedEndLevel(long _seed) {
		super(_seed);
	}

	@Override
	public void designLevel(Floor _floor) {
		List<DoorEntity> doors = new ArrayList<DoorEntity>();
		System.out.println("creating doors");
		Space[][] floor = _floor.getData();
		int size = _floor.getSize();
		for(int i=0; i<4; i++){
			if(floor[size-5][size-5+i].type==Space.EMPTY){
				doors.add(new DoorEntity(Entity.freeID(), Vec3.create(size-5, 0, size-5+i)));
			}
			if(floor[size-5+i][size-5].type==Space.EMPTY){
				doors.add(new DoorEntity(Entity.freeID(), Vec3.create(size-5, 0, size-5+i)));
			}
		}
		
		
		List<Entity> entities = new ArrayList<Entity>(doors);
		System.out.println("creating levels");
		new Level(_floor, entities);
	}

}
