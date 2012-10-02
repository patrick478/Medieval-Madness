package server.game;

import common.entity.*;
import common.packets.ChangeEntityModePacket;
import common.packets.EntityMode;

import java.util.*;

import server.session.Session;

public class PlayerManager {
	private static PlayerManager instance = null;
	public static PlayerManager getInstance()
	{
		if(instance == null)
			instance = new PlayerManager();
		
		return instance;
	}	
	
	private Map<String, ServerPlayer> players = new HashMap<String, ServerPlayer>();
	public PlayerManager()
	{
	}
	
	public void addPlayer(String username, ServerPlayer player)
	{
		if(players.containsKey(username))
		{
			for(int i = 0; i < 500; i++)
				System.err.println("Ben! GET HERE NOW!!");
		}
		players.put(username, player);
		
		this.notifyPlayerJoined(player);
	}
	
	public ServerPlayer getPlayer(String username)
	{
		if(players.containsKey(username))
			return players.get(username);
		return null;
	}
	
	private void notifyPlayerJoined(ServerPlayer sp)
	{
		ChangeEntityModePacket pk = new ChangeEntityModePacket();
		pk.entityID = sp.id;
		pk.mode = EntityMode.Born;
		
		for(ServerPlayer op : this.players.values())
		{
			System.out.printf("Notifying Mr. %s\n", sp.id);
			op.session.send(pk);
		}
	}
}
