package game.level;

import game.Game;
import game.bound.Bound;
import game.bound.BoundingSphere;
import game.entity.Entity;
import game.entity.moveable.MoveableEntity;
import game.entity.moveable.PlayerEntity;
import game.entity.trigger.StaticTriggerEntity;
import game.entity.trigger.TriggerEntity;
import game.event.LevelFinishEvent;
import game.item.Item;
import game.modelloader.Content;

import initial3d.engine.Color;
import initial3d.engine.Material;
import initial3d.engine.Mesh;
import initial3d.engine.MeshContext;
import initial3d.engine.MovableReferenceFrame;
import initial3d.engine.Scene;
import initial3d.engine.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Level {

	private final Floor floor;
	private final List<Entity> entitesToLoad = new ArrayList<Entity>();
	private final List<Item> itemsToLoad = new ArrayList<Item>();
	private final List<Entity> entities = new ArrayList<Entity>();
	private final Map<Long, Entity> entityID = new HashMap<Long, Entity>();
	
	private final List<TriggerEntity> triggers = new ArrayList<TriggerEntity>();
	
	public Level(Floor _floor, List<Entity> _entities, List<Item> items){
		Game.getInstance().setLevel(this);
		floor = _floor;
		for(Entity e : floor.getWalls()){
			entitesToLoad.add(e);
		}
		for(Entity e : _entities){
			entitesToLoad.add(e);
		}
		for(PlayerEntity p : Game.getInstance().getPlayers()){
			entitesToLoad.add(p);
		}
		for(Item i : items){
			itemsToLoad.add(i);
		}
		
		//set up the end level scenario
		this.addEntity(
				new StaticTriggerEntity(Entity.freeID(), 
				new LevelFinishEvent(), 
				new BoundingSphere(Vec3.create(_floor.getSize()-2, 0, _floor.getSize()-2), 2))
		);
	}
	
	/**
	 * Adds all the entities into the game via the Game.getInstance().addEntity(...) Method
	 */
	public void init(Scene _scene){
		floor.addToScene(_scene);
		if(Game.getInstance().isHost()){
			synchronized(entities){
				for(Entity e : entitesToLoad){
					Game.getInstance().addEntity(e);
				}
				for(Item i : itemsToLoad){
					Game.getInstance().spawnItem(i);
				}
			}
		}
		
		Material mat1 = new Material(Color.GRAY, new Color(0.1f, 0.08f, 0.036f), new Color(0.91f, 0.82f, 0.54f),
				new Color(0.2f, 0.2f, 0f), 2f, 1f);
		Mesh m1 = Content.loadContent("resources/models/grail/grail.obj");
		MovableReferenceFrame mrf = new MovableReferenceFrame(null);
		mrf.setPosition(Vec3.create(3, 0, 3));
		MeshContext mc1 = new MeshContext(m1, mat1, mrf);
		// mc1.setScale(0.1);
		mc1.setHint(MeshContext.HINT_SMOOTH_SHADING);
		_scene.addDrawable(mc1);
	}
	
	public Floor getFloor() {
		return floor;
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
	
	
	public void addEntity(Entity _entity){
		synchronized(entities){
			if(entityID.containsKey(_entity.id)){
				return;
			}
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
	 * TODO
	 * 
	 * @param _e The Entity whose bound to check collides
	 * @param _solid Checking against solid or non-solid entities
	 * @return Whether the given bound intersects
	 */
	public Vec3 preCollisionNorm(MoveableEntity _e, boolean _solid){
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
