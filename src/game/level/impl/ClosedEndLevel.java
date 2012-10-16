package game.level.impl;

import initial3d.engine.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.entity.Entity;
import game.entity.moveable.DoorEntity;
import game.entity.moveable.SpikeBallEntity;
import game.item.Item;
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
		Space[][] floor = _floor.getData();
		int size = _floor.getSize();
		Random rand = new Random(size *getSeed());
		//add in the doors around the end
		for(int i=0; i<3; i++){
			if(floor[size-4][size-4+i].type==Space.EMPTY){
				doors.add(new DoorEntity(Entity.freeID(), Vec3.create(size-4, 0, size-4+i)));
			}
			if(floor[size-4+i][size-4].type==Space.EMPTY){
				doors.add(new DoorEntity(Entity.freeID(), Vec3.create(size-4+i, 0, size-4)));
			}
		}
		
		List<Entity> entities = new ArrayList<Entity>(doors);
		List<Item> items = new ArrayList<Item>();
		
		//generate and distrubute the keys fr those doors
		for(DoorEntity d : doors){
			int xPos = 1;
			int zPos = 1;
			do{
				xPos = (int) ((size-5)*rand.nextDouble()+2);
				zPos = (int) ((size-5)*rand.nextDouble()+2);
			}while(floor[xPos][zPos].type!=Space.EMPTY);
			Vec3 position = Vec3.create(xPos, 0.15, zPos);
			items.add(d.generatekey(position));
		}
		
		//create spike ball entities
		for(int i = 0; i<((size-5)/3); i++){
			Vec3 pos = Vec3.create(((size-5)*rand.nextDouble()+5), 0.25, (size-5)*rand.nextDouble()+5);
			entities.add(new SpikeBallEntity(Entity.freeID(), size*5, -size+8, pos, size/(double)50));
		}
		
		new Level(_floor, entities, items);
	}

}
