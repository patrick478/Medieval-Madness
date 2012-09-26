package server;

import common.entity.*;
import java.util.*;

public class PlayerManager {
	private static PlayerManager instance = null;
	public static PlayerManager getInstance()
	{
		if(instance == null)
			instance = new PlayerManager();
		
		return instance;
	}	
	
	private Map<String, Player> players = new HashMap<String, Player>();
	public PlayerManager()
	{
	}
	
	public void addPlayer(String charName, Player player)
	{
		if(players.containsKey(charName))
		{
			for(int i = 0; i < 500; i++)
				System.err.println("Ben! GET HERE NOW!!");
		}
		players.put(charName, player);
	}
	
	public Player getPlayer(String charname)
	{
		if(players.containsKey(charname))
			return players.get(charname);
		return null;
	}
}
