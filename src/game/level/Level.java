package game.level;

import game.Game;
import game.bound.Bound;
import game.entity.Entity;
import game.entity.moveable.MoveableEntity;
import game.entity.moveable.PlayerEntity;
import game.entity.trigger.TriggerEntity;

import initial3d.engine.Scene;
import initial3d.engine.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Level {

	private final Floor floor;
	private final List<Entity> entities = new ArrayList<Entity>();
	private final Map<Long, Entity> entityID = new HashMap<Long, Entity>();
	
	private final List<TriggerEntity> triggers = new ArrayList<TriggerEntity>();
	
	public Level(Floor _floor, List<Entity> _entities){
		floor = _floor;
		
		for(Entity e : floor.getWalls()){
			this.addEntity(e);
		}
		for(PlayerEntity p : Game.getInstance().getPlayers()){
			this.addEntity(p);
		}
	}
	
	/**
	 * Iterates through the list of all entities on this level
	 * and call Entity.poke() on them.
	 */
	public void pokeAll(){
		synchronized(entities){
			for(Entity e : entities){
				e.poke();
			}
		}
	}
	
	/**
	 * Returns the spawning location of the given player 
	 * given via player index. Returns Vec3.zero if the index
	 * of player doesn't exist.
	 * 
	 * @param _playerIdx The index of the player for the game
	 * @return The starting position of the player
	 */
	public Vec3 getSpawnLocation(int _playerIdx){
		switch(_playerIdx){
			case 0: return Vec3.create(0.75, 0.125, 0.75);
			case 1: return Vec3.create(1.25, 0.125, 0.75);
			case 2: return Vec3.create(0.75, 0.125, 1.25);
			case 3: return Vec3.create(1.25, 0.125, 1.25);
		}
		return Vec3.zero;
	}
	
	/**
	 * Adds all the entities contained on this level to the given scene.
	 * Does nothing if the Scene is null;
	 * 
	 * @param _scene The scene to add entities to
	 */
	public void addToScene(Scene _scene){
		if(_scene == null)return;
		floor.addToScene(_scene);
		synchronized(entities){
			for(Entity e : entities){
				e.addToScene(_scene);
			}
		}
	}
	
	
	public void addEntity(Entity _entity){
		synchronized(entities){
			entities.add(_entity);
			entityID.put(_entity.id, _entity);
			if(_entity instanceof TriggerEntity){
				triggers.add((TriggerEntity) _entity);
			}
		}
	}
	
	public Entity getEntity(long _eid){
		return entityID.get(_eid);
	}
	
	public Entity removeEntity(long _eid){
		synchronized(entities){
			Entity e = entityID.remove(_eid);
			entities.remove(e);
			return e;
		}
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
	 * Returns whether the given Entity's bound intersects with any 
	 * entity that has the same value for Entity.isSolid() as the 
	 * given parameter. Returns false if null is given.
	 * 
	 * @param _e The Entity whose bound to check collides
	 * @param _solid Checking against solid or non-solid entities
	 * @return Whether the given bound intersects
	 */
	public boolean collision(Entity _e, boolean _solid){
		synchronized(entities){
			for(Entity e : entities){
				if(_e!=e && e.isSolid()==_solid && e.getBound().intersects(_e.getBound())){
					return true;
				}
			}
			return false;
		}
	}
	
	/**
	 * Returns whether the given Entity's bound intersects with any 
	 * entity that has the same value for Entity.isSolid() as the 
	 * given parameter. Returns false if null is given.
	 * 
	 * @param _e The Entity whose bound to check collides
	 * @param _solid Checking against solid or non-solid entities
	 * @return Whether the given bound intersects
	 */
	public Vec3 preCollision(MoveableEntity _e, boolean _solid){
		synchronized(entities){
			boolean collisionDect = false;
			Vec3 collisionNorm = Vec3.zero;
			for(Entity e : entities){
				if(_e!=e && e.isSolid()==_solid){
					Vec3 col = e.getBound().intersectNorm(_e.getNextBound());
					if(col!=null){
						collisionNorm = collisionNorm.add(col);
						collisionDect = true;
					}
				}
			}
			if(collisionDect){
				return collisionNorm.unit();
			}
		}
		return null;
	}
	
	public List<Entity> collisions(Entity _e){
		List<Entity> collisions = new ArrayList<Entity>();
		synchronized(entities){
			for(Entity e : entities){
				if(_e!=e && e.getBound().intersects(_e.getBound())){
					collisions.add(e);
				}
			}
		}
		return collisions;
	}
	
	/**
	 * Returns one entity whose bound has intersected the given entity's
	 * bound. Returns null if no collision was detected. 
	 * 
	 * @param _b a Bounding volume to be checked against
	 * @return A entity that collides with the given bound or null
	 */
	public Entity firstCollision(Entity _e){
		synchronized(entities){
			for(Entity e : entities){
				if(_e!=e && e.getBound().intersects(_e.getBound())){
					return e;
				}
			}
		}
		return null;
	}
}
