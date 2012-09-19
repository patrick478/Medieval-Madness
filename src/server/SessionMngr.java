package server;

import java.util.*;

import common.Log;

public class SessionMngr {
	private static SessionMngr instance = null;
	public static SessionMngr getInstance() {
		if(instance == null)
			instance = new SessionMngr();
		
		return instance;
	}
	
	private Map<String, Session> sessionList = new HashMap<String, Session>();
	private Log log = new Log("sessions.log", true);
	private long totalSessions = 0;
	
	public SessionMngr()
	{
		this.log.setPrefix("(SessionMngr) ");
		this.log.printf("Session manager started\n");
	}
	
	public void shutdown()
	{
		this.log.printf("Shutting down\n");
		this.log.close();
	}
	
	public String createSession()
	{
		String id = getUnusedIdentifier();
		Session ses = new Session();
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
	
	private String getUnusedIdentifier() {
		String id = null;
		while(id == null || sessionList.containsKey(id))
			id = String.valueOf(System.currentTimeMillis());
		
		return id;
	}
}
