package server.game;

import initial3d.engine.Quat;
import initial3d.engine.Vec3;

import java.util.*;

import server.Server;

import common.entity.*;
import common.map.Segment;

public class EntityManager {
	private static EntityManager instance = null;
	public static EntityManager getInstance()
	{
		if(instance == null)
			instance = new EntityManager();
		
		return instance;
	}
	
	private static Server parentServer;
	
	EntityQuadTree staticEntityTree = new EntityQuadTree(100);
	Map<Long, Entity> staticEntities = new HashMap<Long, Entity>();
	Map<Long, MovableEntity> moveables = new HashMap<Long, MovableEntity>();
	
	public void addStaticEntity(Entity e, long id)
	{
		this.staticEntityTree.add(e);
		this.staticEntities.put(id, e);
	}
	
	public void addMoveableEntity(MovableEntity e, long id)
	{
		this.moveables.put(id, e);
	}
	
	public void tick(long lastTick)
	{
		for(MovableEntity e : this.moveables.values())
		{
			List<Entity> collidables = staticEntityTree.collisions(e);
			for(Entity col : collidables)
			{
				System.out.printf("THIS MOTHERFUCKER (%s) COLLIDED WITH %s\n", e.toString(), col.toString());
			}
			
			//lastly update the height of the entity on the map
			double xPos = e.getPosition().x;
			double zPos = e.getPosition().z;
			Vec3 oldVel = e.getIntendedVelocity();
			
			Segment s = parentServer.game.segQueue.getSegmentFromWorld(xPos, zPos);
			Vec3 norm = s.getNormal(xPos, zPos);
			Vec3 vel = norm.cross(oldVel.cross(norm)).unit().scale(oldVel.mag());
			
			
			e.updateMotion(Vec3.create(xPos, s.getHeight(xPos, zPos) + e.getHeight(), zPos), 
					oldVel, Quat.one, Vec3.zero, System.currentTimeMillis());
		}
	}

	public static void warm(Server server) {
		parentServer = server;
	}
}
