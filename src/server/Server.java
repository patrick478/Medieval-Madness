package server;

import java.io.*;
import java.util.*;

import server.commands.PrintCommand;
import server.commands.SessionCommand;

import common.*;

public class Server implements Runnable {
	public static final int TICKS_PER_SECOND = 100;
	
	public Log log;
	private Thread runThread;
	
	private Queue<Integer> lastTickRates = new LinkedList<Integer>();
	private Map<String, Command> serverCommands = new HashMap<String, Command>();
	public Map<String, Object> serverData = new HashMap<String, Object>();

	private ServerLayer networking;
	
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
		this.log = new Log("server.log", true);
		this.log.println("Medieval Madness Server v0.1 - \"Proofie Penguin\"");
		
		// check if the server is already running
		if(this.serverData.get("status") != ServerStatus.Stopped)
		{
			this.log.printf("Server :: Start() :: The server cannot be started because it is already running\n");
			return ErrorEnd();
		}
		
		// initalise serverData
		serverData.put("atps",  0);
		serverData.put("start_time_millis", System.currentTimeMillis());
		serverData.put("listen_port", 14121);
		
		serverData.put("status", ServerStatus.Starting);
		
		// prepare stuff here
		this.networking = new ServerLayer((Integer) this.serverData.get("listen_port"), this);
		if(!this.networking.Start())
		{
			this.log.printf("Server :: Start() :: Unable to start the networking system");
			return ErrorEnd();
		}
		
		this.runThread = new Thread(this);
		this.runThread.start();
		
		this.serverCommands.put("print",  new PrintCommand(this));
		this.serverCommands.put("session",  new SessionCommand(this));
		
		// warm the session manager
		SessionMngr.getInstance();
		
		// read console commands
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		String input = "";
		do
		{
			try {
				input = console.readLine();
			} catch (IOException e) {
				this.log.printf("Server :: Console Error() :: %s\n", e.toString());
			}
		} while(this.HandleCommandLine(input));
		
		this.log.printf("Server is shutting down\n");
		
		// start exiting
		this.setStatus(ServerStatus.Stopping);
		
		try {
			this.runThread.join();
			this.networking.stopAndWait();
		} catch (InterruptedException e) {
			System.out.printf("Server :: Run() :: %s\n", e.toString());
		}
		
		SessionMngr.getInstance().shutdown();
				
		// finished - exit
		this.setStatus(ServerStatus.Stopped);
		this.log.close();
		
		return true;
		
	}
	
	public void Tick(long elapsedTimeMillis)
	{
	}

	@Override
	public void run() {
		// variables needed to keep the ticks in sync
		long maxTimePerTick = (1000 / TICKS_PER_SECOND);
		long lastStartTime = System.currentTimeMillis();
		long lastResetTime = System.currentTimeMillis();
		long elapsedTimeSinceLastTick = System.currentTimeMillis();
		int numTicks = 0;
		
		this.setStatus(ServerStatus.Running);
		this.log.printf("Server started in %.2fs\n", (System.currentTimeMillis() - ((Long)this.serverData.get("start_time_millis"))) / 1000.0f);
		
		while(true)
		{
			if(this.serverData.get("status") != ServerStatus.Running)
				break;
			
			elapsedTimeSinceLastTick = System.currentTimeMillis() - lastStartTime;
			lastStartTime = System.currentTimeMillis();
			
			this.Tick(elapsedTimeSinceLastTick);
			numTicks++;
			
			if(System.currentTimeMillis() - lastResetTime >= 1000)
			{
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
				
				lastResetTime = System.currentTimeMillis();
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
		else if(args[0].equals("exit") || args[0].equals("quit"))
		{
			return false;
		}
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
		else
			System.out.printf("Unrecognised command. Enter 'help' for a list of available commands");
		
		return true;
	}
}
