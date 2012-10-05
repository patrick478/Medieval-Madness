package server.game;

import common.entity.*;
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
		player.setPosition(
							Vec3.create(
									this.parentServer.serverSettings.getIntValue("default_x", 0),
									this.parentServer.serverSettings.getIntValue("default_y", 0),
									this.parentServer.serverSettings.getIntValue("default_z", 0)));
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
}
