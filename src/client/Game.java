package client;

import initial3d.engine.*;

import java.util.*;
import common.entity.*;

public class Game {
	private double trackDistance = 100;
	private double existDistance = 400;
	
	private Entity playerEntity = null;
	private Map<Long, Entity> entities = new HashMap<Long, Entity>();	
	
	public void entityMoved(Long eid, Vec3 pos, Vec3 vel, Quat ori, Vec3 dori)
	{
		if(entities.containsKey(eid))
		{
			// bhah
		}
	}
	
	public void entityStartTracking(long eid)
	{
		
	}
	
	public void entityStopTracking(long eid)
	{
		
	}
}
