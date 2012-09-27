package server.game;

import java.util.*;

import common.entity.*;

public class EntityManager {
	private static EntityManager instance = null;
	public static EntityManager getInstance()
	{
		if(instance == null)
			instance = new EntityManager();
		
		return instance;
	}
	
	EntityQuadTree staticEntityTree = new EntityQuadTree(100);
	Map<Long, Entity> staticEntities = new HashMap<Long, Entity>();
	Map<Long, MovableEntity> moveables = new HashMap<Long, MovableEntity>();
	
	public void addStaticEntity(Entity e)
	{
		this.staticEntityTree.add(e);
		this.staticEntities.put(System.nanoTime(), e);
	}
	
	public void addMoveableEntity(MovableEntity e)
	{
		this.moveables.put(System.nanoTime(), e);
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
			
		}
	}
}
