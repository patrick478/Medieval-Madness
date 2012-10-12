package game.floor;

import game.bound.Bound;
import game.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Level {

	private final Floor floor;
	private final List<Entity> entities;
	private final Map<Long, Entity> entityID;
	
	public Level(Floor _floor, List<Entity> _entities){
		floor = _floor;
		entities = new ArrayList<Entity>(_entities);
		entityID = new HashMap<Long, Entity>();
		for(Entity e : _entities){
			entityID.put(e.id, e);
		}
		
	}

	public Floor getFloor()
	{
		return this.floor;
	}
	
	public void addEntity(Entity _entity){
		entities.add(_entity);
		entityID.put(_entity.id, _entity);
	}
	
	public Entity getEntity(long _eid){
		return entityID.get(_eid);
	}
	
	public void removeEntity(long _eid){
		entities.remove(entityID.remove(_eid));
	}
	
	public boolean collides(Bound _b, boolean _solid){
		for(Entity e : entities){
			if(e.isSolid()==_solid && e.getBound().intersects(_b)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns one entity whose bound has intersected the given bounding
	 * box. Returns null if no collision was detected. 
	 * 
	 * @param _b a Bounding volume to be checked against
	 * @return A entity that collides with the given bound or null
	 */
	public Entity firstCollision(Bound _b){
		for(Entity e : entities){
			if(e.getBound().intersects(_b)){
				return e;
			}
		}
		return null;
	}	
}
