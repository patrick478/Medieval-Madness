package server;

import java.io.FileNotFoundException;
import java.util.*;

import server.commands.GameEngineCommands;
import server.commands.PrintCommand;
import server.commands.SessionCommand;
import server.datalayer.DataProvider;
import server.datalayer.SQLite;
import server.face.ConsoleFace;
import server.face.ServerFace;
import server.game.GameEngine;
import server.net.ServerLayer;
import server.session.SessionMngr;
import common.settings.Settings;

import common.*;
import common.Timer;

public class Server implements Runnable {	
	public Log log;
	private Thread runThread;
	
	private Queue<Integer> lastTickRates = new LinkedList<Integer>();
	private Map<String, Command> serverCommands = new HashMap<String, Command>();
	public Map<String, Object> serverData = new HashMap<String, Object>();

	public ServerLayer networking;
	public DataProvider db;
	public GameEngine game;
	public ServerFace face;
	public Settings serverSettings;
	
	public Server()
	{
		this.serverData.put("status",  ServerStatus.Stopped);
	}
	
	public boolean ErrorEnd()
	{
		this.setStatus(ServerStatus.Stopped);
		return false;
	}

	public boolean RunServer()
	{			    
		Timer startTimer = new Timer(true);
		
		try {
			serverSettings = new Settings("server.mms");
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		face = new ConsoleFace();
		face.setup(this);
		Thread faceThread = new Thread(face);
		faceThread.setDaemon(true);
		
		this.log = new Log("server.log", true, this.face.getOut());
		this.log.println("Medieval Madness Server v0.1 - \"Proofie Penguin\"");
		
		// check if the server is already running
		if(this.serverData.get("status") != ServerStatus.Stopped)
		{
			this.log.printf("Server :: Start() :: The server cannot be started because it is already running\n");
			return ErrorEnd();
		}
		
		// initalise serverData
		serverData.put("game_seed", serverSettings.getLongValue("game_seed", System.currentTimeMillis()));
		serverData.put("atps",  0);
		serverData.put("start_time_millis", System.currentTimeMillis());
		serverData.put("listen_port", serverSettings.getIntValue("listen_port", 14121));
		
		serverData.put("running", true);
		serverData.put("status", ServerStatus.Starting);
		
		// prepare stuff here
		this.networking = new ServerLayer((Integer) this.serverData.get("listen_port"), this);
		if(!this.networking.Start())
		{
			this.log.printf("Server :: Start() :: Unable to start the networking system");
			return ErrorEnd();
		}
		
		// setup the db
		this.db = new SQLite("save/unknown.db");
		this.db.startup(this.face.getOut());
		
		this.runThread = new Thread(this);
		this.runThread.start();
		
		this.serverCommands.put("print",  new PrintCommand(this));
		this.serverCommands.put("session",  new SessionCommand(this));
		this.serverCommands.put("ge", new GameEngineCommands(this));	
		
		// warm the session manager
		SessionMngr.warm(this.face.getOut());
		
		// start the face
		faceThread.start();
		
		game = new GameEngine((Long)this.serverData.get("game_seed"), this.face.getOut(), this);
		game.warm();
		
		startTimer.stop();		
		this.setStatus(ServerStatus.Running);
		this.log.printf("Server started in %.2fs\n", startTimer.elapsed_sDouble());
		
		while((Boolean)this.serverData.get("running"))
			try {
				Thread.sleep(10);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		this.log.printf("Server is shutting down\n");
		
		// start exiting
		this.setStatus(ServerStatus.Stopping);
		
		try {
			this.runThread.join();
			this.networking.stopAndWait();
			this.db.stopAndWait();
		} catch (InterruptedException e) {
			System.out.printf("Server :: Run() :: %s\n", e.toString());
		}
		
		SessionMngr.getInstance().shutdown();
				
		// finished - exit
		this.setStatus(ServerStatus.Stopped);
		this.log.close();
		
		this.face.close();
		
		this.serverSettings.saveChanges();
		
		return true;
		
	}

	public void Tick(long elapsedTimeMillis)
	{
	}

	@Override
	public void run() {
		// variables needed to keep the ticks in sync
		long maxTimePerTick = (1000 / this.serverSettings.getIntValue("ticks_per_second",  30));
		long lastStartTime = System.currentTimeMillis();
		long lastResetTime = System.currentTimeMillis();
		long elapsedTimeSinceLastTick = System.currentTimeMillis();
		int numTicks = 0;
		
		while(true)
		{
			if(this.serverData.get("status") != ServerStatus.Running)
				break;
			
			elapsedTimeSinceLastTick = System.currentTimeMillis() - lastStartTime;
			lastStartTime = System.currentTimeMillis();
			
			this.Tick(elapsedTimeSinceLastTick);
			numTicks++;
			
			if(System.currentTimeMillis() - lastResetTime >= (1000 - this.serverSettings.getIntValue("ticks_per_second",  30) + (this.serverSettings.getIntValue("ticks_per_second",  30) / 10)))
			{
				lastResetTime = System.currentTimeMillis();
				
				this.lastTickRates.add(numTicks);
				if(this.lastTickRates.size() > 10)
					this.lastTickRates.poll();
				
				this.serverData.put("atps", 0);
				Iterator<Integer> itr = this.lastTickRates.iterator();
				while(itr.hasNext())
				{
					this.serverData.put("atps", (Integer)this.serverData.get("atps") + itr.next());
				}
				this.serverData.put("atps", (Integer)this.serverData.get("atps") / (float)(this.lastTickRates.size()));
				//this.log.printf("aTPS=%f\n",  this.AverageTicksPerSecond);
				
				numTicks = 0;
			}
			
			long currentTimeForTick = System.currentTimeMillis() - lastStartTime;
			if(currentTimeForTick < maxTimePerTick)
			{
				try
				{
					Thread.sleep((long) (maxTimePerTick - currentTimeForTick));
				}
				catch(Exception e)
				{
					System.out.printf("{ERROR} %s", e.toString());
				}
			}
			else
				this.log.printf("{WARNING} Server can't keep up - ticks are taking too long");
		}
	}
	
	public void setStatus(ServerStatus st)
	{
		this.serverData.put("status",  st);
		this.log.printf("{STATUS} Server status changed to %s\n", st.toString());
	}
	
	public ServerStatus getStatus()
	{
		return (ServerStatus) this.serverData.get("status");
	}
	
	public boolean HandleCommandLine(String msg)
	{
		if(msg == null || msg.length() == 0) return false;
		
		msg = msg.trim();
		String args[] = msg.split(" ");
		if(this.serverCommands.containsKey(args[0]))
			this.serverCommands.get(args[0]).execute(args);
		else if(args[0].equals("help"))
		{
			String options = "";
			for(String str : this.serverCommands.keySet())
			{
				if(options.length() > 0)
					options += " | ";
				options += str;
			}
			System.out.printf("Availble commands:\n\t%s\n", options);
		}
		else if(args[0].equals("quit") || args[0].equals("exit"))
		{
			serverData.put("status", ServerStatus.Stopping);
			serverData.put("running", false);
		}
		else
			this.face.getOut().printf("Unrecognised command. Enter 'help' for a list of available commands\n");
		
		return true;
	}
}
