package client;

import initial3d.engine.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import common.entity.*;
import common.map.Segment;
import common.map.SegmentGenerator;
import comp261.modelview.MeshLoader;

public class Game {
	private static Game instance = null;
	public static Game getInstance()
	{
//		if(instance == null)
//			instance = new Game();
//		
		return instance;
	}
	
	private Map<Long, MovableEntity> movableEntities = new HashMap<Long, MovableEntity>();	
	private Map<Long, Entity> staticEntities = new HashMap<Long, Entity>();	
	
	private Map<Long, Segment> terrain = new HashMap<Long, Segment>();	
	
	private Client client = null;
	private MovableEntity player = null;
	private Scene world = null;
	
	public Game(Client c){
		instance = this;
		client = c;
	}
	
	
	public void loadScene(Scene _world){
		world = _world;
		
		// these loops are bad - what if the scene already has the terrain?!
		for(MovableEntity me : movableEntities.values()){
			//add mesh
		}
		
		for(Entity m : staticEntities.values()){
			//add mesh
		}
		
		for(Segment s : terrain.values()){
			
		}
		

	}
	
	public void enterWorld(int worldID, long entityID)
	{
		System.out.printf("Entering world %d and i am entity #%d\n", worldID, entityID);
		this.setPlayer(entityID);
		this.player.updateMotion(movableEntities.get(entityID).getPosition(), Vec3.zero, Quat.one, Vec3.zero, System.currentTimeMillis());
		
		ensureTerrainRelevent();
	}

	public boolean setPlayer(long eid){
		player = movableEntities.get(eid);
//		System.out.println(world);
//		world.getCamera().trackReferenceFrame(player);
		trackPlayer(player);
		return player!=null;
	}
	
	private Player getBall()
	{
		Player ball = new Player(Vec3.zero, 1231231);
		FileInputStream fis;
		try {
			fis = new FileInputStream("ball.txt");
			ball.setMeshContexts(MeshLoader.loadComp261(fis));
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (Drawable d : ball.getMeshContexts()) {
			world.addDrawable(d);
		}
		
		return ball;
	}
	
	private void trackPlayer(MovableEntity me)
	{
		if(me == null) return;
		
		MovableReferenceFrame camera_rf = new MovableReferenceFrame(me);
		world.getCamera().trackReferenceFrame(camera_rf);
		camera_rf.setPosition(Vec3.create(-10, 10, -10));
		camera_rf.setOrientation(Quat.create(Math.PI / 8, Vec3.i));
		
		camera_rf.setOrientation(camera_rf.getOrientation().mul(Quat.create(Math.PI / 4, Vec3.j)));
	}
	
	public void entityMoved(Long eid, Vec3 pos, Vec3 linVel, Quat ori, Vec3 angVel, Long time){
		System.out.println("Moving");
		MovableEntity e;
		do
		{
			e = movableEntities.get(eid);
			if(e == null)
				movableEntities.put(eid, getBall());
		} while(e == null);
			
		if(e!=null ){//TODO fix the time signiture 
//			System.out.printf("Updated entity %d to %f,%f,%f\n", eid, pos.x, pos.y, pos.z);
			e.updateMotion(e.getPosition(), linVel, ori, angVel, time);
		}
	}
	
	//TODO wtf is this method going to do?
	public void entityStartTracking(long eid){ 
		//System.out.println("(OTHER)BEN GET THE FUCK HERE");
	}
	
	public void entityStopTracking(long eid){
		//System.out.println("(OTHER)BEN GET THE FUCK HERE");
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
		System.out.printf("Adding segment at %d %d\n", tim.xPos, tim.zPos);
		if(terrain.containsKey(tim.id))
			return;
		
		terrain.put(tim.id, tim);
		MeshContext mc = tim.getMeshContext();
		

	}
	
	public void ensureTerrainRelevent()
	{
		int releventRange = 2;
		int segX = SegmentGenerator.segCoordFromWorldCoord(player.getPosition().x);
		int segZ = SegmentGenerator.segCoordFromWorldCoord(player.getPosition().z);
		
		for(Segment seg : terrain.values())
		{
			if(Math.abs(seg.xPos - segX) < releventRange && Math.abs(seg.zPos - segZ) < releventRange)
			{
				if(!this.client.getState().getScene().containsDrawable(seg.getMeshContext()))
					this.client.getState().getScene().addDrawable(seg.getMeshContext());
			}
			else
			{
				this.client.getState().getScene().removeDrawable(seg.getMeshContext());
			}
		}
	}
}
