package client;

import initial3d.engine.*;

import java.util.*;
import common.entity.*;
import common.map.Segment;

public class Game {
	private double trackDistance = 100;
	private double existDistance = 400;
	
	private Map<Long, MovableEntity> movableEntities = new HashMap<Long, MovableEntity>();	
	private Map<Long, Entity> staticEntities = new HashMap<Long, Entity>();	
	
	private Map<Long, Segment> terrain = new HashMap<Long, Segment>();	
	
	private Scene world;
	
	public Game(){}
	
	
	public void loadScene(Scene _world){
		world = _world;
		for(MovableEntity me : movableEntities.values()){
			//add mesh
		}
		for(Entity m : staticEntities.values()){
			//add mesh
		}
		for(Segment s : terrain.values()){
			//add mesh
		}
	}
	
	public Scene getScene(){
		return world;
	}
	
	public void entityMoved(Long eid, Vec3 pos, Vec3 linVel, Quat ori, Vec3 angVel, Long time){
		MovableEntity e = movableEntities.get(eid);
		if(e!=null ){//TODO fix the time signiture 
			e.updateMotion(pos, linVel, ori, angVel, time);
		}
	}
	
	//TODO wtf is this method going to do?
	public void entityStartTracking(long eid){ 
		System.out.println("(OTHER)BEN GET THE FUCK HERE");
	}
	
	public void entityStopTracking(long eid){
		//remove mesh
		movableEntities.remove(eid);
		staticEntities.remove(eid);
	}
	
	public void addMoveableEntity(MovableEntity jim){
		//add mesh
		movableEntities.put(jim.id, jim);
	}
	
	public void addStaticEntity(Entity jim){
		//add mesh
		staticEntities.put(jim.id, jim);
	}
	
	public void addTerrain(Segment tim){
		//add mesh
		terrain.put(tim.id, tim);
	}
}
