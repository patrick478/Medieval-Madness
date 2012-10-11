package game.level;

import game.bound.Bound;
import game.entity.Entity;
import game.entity.trigger.TriggerEntity;

import initial3d.engine.Scene;
import initial3d.engine.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Level {

	private final Floor floor;
	private final List<Entity> entities;
	private final Map<Long, Entity> entityID;
	
	private final List<TriggerEntity> triggers;
	
	public Level(Floor _floor, List<Entity> _entities){
		floor = _floor;
		entities = new ArrayList<Entity>(_entities);
		entityID = new HashMap<Long, Entity>();
		triggers = new ArrayList<TriggerEntity>();
		
		for(Entity e : floor.getWalls()){
			entities.add(e);
		}
		for(Entity e : entities){
			entityID.put(e.id, e);
			if(e instanceof TriggerEntity){
				triggers.add((TriggerEntity) e);
			}
		}
		
	}
	
	/**
	 * Iterates through the list of all entities on this level
	 * and call Entity.poke() on them.
	 */
	public void pokeAll(){
		for(Entity e : entities){
			e.poke();
		}
	}
	
	public void addToScene(Scene _scene){
		floor.addToScene(_scene);
		for(Entity e : entities){
			e.addToScene(_scene);
		}
	}
	
	public void addEntity(Entity _entity){
		entities.add(_entity);
		entityID.put(_entity.id, _entity);
		if(_entity instanceof TriggerEntity){
			triggers.add((TriggerEntity) _entity);
		}
	}
	
	public Entity getEntity(long _eid){
		return entityID.get(_eid);
	}
	
	public void removeEntity(long _eid){
		entities.remove(entityID.remove(_eid));
	}
	
	/**
	 * Retrieves a copy of the list of triggers currently held
	 * by the level. The list may be modified but contain references
	 * to the original triggers and their events. 
	 * 
	 * @return A copy of the list of triggers
	 */
	public List<TriggerEntity> getTriggers(){
		return new ArrayList<TriggerEntity>(triggers);
	}
	
	/**
	 * Returns whether the given bound intersects with any entity
	 * that has the same value for Entity.isSolid() as the given
	 * parameter. Returns false if null is given.
	 * 
	 * @param _b The bound to intersect with
	 * @param _solid Checking against solid or non-solid entities
	 * @return Whether the given bound intersects
	 */
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
