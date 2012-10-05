package server.game;

import common.entity.*;
import common.map.Segment;
import common.packets.ChangeEntityModePacket;
import common.packets.EnterWorldPacket;
import common.packets.EntityUpdatePacket;

import initial3d.engine.Vec3;

import java.util.*;

import server.Server;
import server.session.Session;

public class PlayerManager {
	private static PlayerManager instance = null;
	public static PlayerManager getInstance()
	{	
		return instance;
	}

	public static void warm(Server server)
	{
		instance = new PlayerManager(server);
	}
	
	private Server parentServer;
	private Map<String, ServerPlayer> players = new HashMap<String, ServerPlayer>();
	public PlayerManager(Server ps)
	{
		parentServer = ps;
	}
	
	public void addPlayer(String username, ServerPlayer player)
	{
		if(players.containsKey(username))
		{
			System.err.printf("Duplicate user logged in.\n");
		}
		
		Random r = new Random();
		double x = this.parentServer.serverSettings.getDoubleValue("default_x", 0) + (r.nextDouble() * 20) - 10; 
		double z = this.parentServer.serverSettings.getDoubleValue("default_z", 0) + (r.nextDouble() * 20) - 10; 
		System.out.printf("Created player %s at %f,%f\n", username, x, z);
		player.setPosition(Vec3.create(x, this.parentServer.game.segQueue.getSegmentFromWorld(x, z).getHeight(x,  z), z));
		players.put(username, player);
				
		this.notifyPlayerJoined(player);
		this.playerEnterWorld(player);
	}
	
	public ServerPlayer getPlayer(String username)
	{
		if(players.containsKey(username))
			return players.get(username);
		return null;
	}
	
	private void playerEnterWorld(ServerPlayer player)
	{
		EntityUpdatePacket pk = new EntityUpdatePacket();
		pk.entityID = player.id;
		pk.angularVel = player.getAngularVelocity();
		pk.orientation = player.getOrientation();
		pk.position = player.getPosition();
		pk.velocity = player.getLinearVelocity();
		player.session.send(pk);
		
		EnterWorldPacket ewp = new EnterWorldPacket();
		ewp.newWorld = 0;
		ewp.playerEntity = player.id;
		
		player.session.send(ewp);
		ensureSegmentRange(player);
	}
	
	private void notifyPlayerJoined(ServerPlayer sp)
	{
		ChangeEntityModePacket pk = new ChangeEntityModePacket();
		pk.entityID = sp.id;
		pk.mode = EntityMode.Born;
		pk.type = EntityType.Moveable;
		
		for(ServerPlayer op : this.players.values())
		{
			if(op.equals(sp))
				continue;
			
			System.out.printf("Notifying Mr. %s of player in type %s\n", sp.id, pk.type.toString());
			op.session.send(pk);
		}
	}
	
	public void ensureSegmentRange(ServerPlayer sp)
	{
		int segRange = this.parentServer.serverSettings.getIntValue("min_segment_send_range", 12);
		int seg2 = segRange / 2;
		
		Segment curSeg = this.parentServer.game.segQueue.getSegmentFromWorld(sp.getPosition().x, sp.getPosition().z);
		int curX = curSeg.xPos;
		int curZ = curSeg.zPos;
		
		for(int i = 0; i < segRange; i++)
			for(int j = 0; j < segRange; j++)
			{
				System.out.printf("Sending segment @ %d, %d\n", i-seg2+curX, j-seg2+curZ);
				this.parentServer.game.addSegmentRequest(sp.session, i-seg2+curX, j-seg2+curZ);
			}
	}
}
