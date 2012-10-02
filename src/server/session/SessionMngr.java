package server.session;

import java.io.PrintStream;
import java.nio.channels.SocketChannel;
import java.util.*;

import server.net.ServerLayer;

import common.Log;

public class SessionMngr {
	private static SessionMngr instance = null;
	public static SessionMngr getInstance() {
		if(instance == null)
			instance = new SessionMngr();
		
		return instance;
	}
	
	private Map<String, Session> sessionList = new HashMap<String, Session>();
	private Log log;
	private long totalSessions = 0;
	
	private static PrintStream outStream = System.out;
	
	private SessionMngr()
	{
		this.log = new Log("sessions.log", true, outStream);
		this.log.setPrefix("(SessionMngr) ");
		this.log.printf("Session manager started\n");
	}
	
	public static void warm(PrintStream outx)
	{
		outStream = outx;
		SessionMngr.instance = new SessionMngr();
	}
	
	public void shutdown()
	{
		this.log.printf("Shutting down\n");
		this.log.close();
	}
	
	public String createSession(SocketChannel sc, ServerLayer pl)
	{
		String id = getUnusedIdentifier();
		Session ses = new Session(sc, pl);
		sessionList.put(id, ses);
		this.totalSessions++;
		
		this.log.printf("Created new session: %s\n", id);
		return id;
	}
	
	public void destroySession(String id)
	{
		if(sessionList.containsKey(id))
			sessionList.remove(id);
		
		this.log.printf("Deleted session: %s\n", id);
	}
	
	public int numSessions() {
		return this.sessionList.size();
	}
	
	public long totalSessions() {
		return this.totalSessions;
	}
	
	public Session getSession(String id)
	{
		if(sessionList.containsKey(id))
			return sessionList.get(id);
		return null;
	}
	
	public String getKey(Session ses)
	{
		if(!sessionList.containsValue(ses))
			return null;
		
		Set<String> keySet = sessionList.keySet();
		
		for(Iterator<String> itr = keySet.iterator(); itr.hasNext();)
		{
			String key = itr.next();
			if(sessionList.get(key).equals(ses))
				return key;
		}
		
		return null;
	}
	
	private String getUnusedIdentifier() {
		String id = null;
		while(id == null || sessionList.containsKey(id))
			id = String.valueOf(System.currentTimeMillis());
		
		return id;
	}
}
