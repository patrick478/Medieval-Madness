package server;

import java.io.*;
import java.util.*;

import common.*;

public class Server implements Runnable {
	public static final int TICKS_PER_SECOND = 100;
	
	private Log log;
	private Thread runThread;
	
	private Queue<Integer> lastTickRates = new LinkedList<Integer>();
	public Map<String, Object> serverData = new HashMap<String, Object>();

	private ServerLayer networking;
	
	public Server()
	{
		this.serverData.put("status",  ServerStatus.Stopped);
	}
	
	public boolean ErrorEnd()
	{
		this.serverData.put("status", ServerStatus.Stopped);
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
		serverData.put("start_time", System.currentTimeMillis());
		serverData.put("status", ServerStatus.Starting);
		serverData.put("listen_port", 14121);
		
		// prepare stuff here
		this.networking = new ServerLayer((Integer) this.serverData.get("listen_port"));
		if(!this.networking.Start())
		{
			this.log.printf("Server :: Start() :: Unable to start the networking system");
			return ErrorEnd();
		}
		
		this.runThread = new Thread(this);
		this.runThread.start();
		
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
		
		// start exiting
		this.serverData.put("status", ServerStatus.Stopping);
		
		try {
			this.runThread.join();
		} catch (InterruptedException e) {
			System.out.printf("Server :: Run() :: %s\n", e.toString());
		}
		
		// finished - exit
		this.serverData.put("status", ServerStatus.Stopped);
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
		
		this.serverData.put("status", ServerStatus.Running);
		this.log.printf("Server :: run() :: Started in %fs\n", (System.currentTimeMillis() - ((Long)this.serverData.get("start_time"))) / 1000.0f);
		
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
					System.out.printf("Server :: Run() :: %s", e.toString());
				}
			}
			else
				System.out.printf("Server :: Run() :: Server can't keep up - ticks are taking too long");
		}
	}
	
	public boolean HandleCommandLine(String msg)
	{
		msg = msg.trim();
		String args[] = msg.split(" ");
		if(args[0].equals("print"))
		{
			if(args.length == 2)
				this.PrintVariable(args[1]);
			else
				System.out.printf("You must specify one variable to print\n");
		}
		else if(args[0].equals("exit") || args[0].equals("quit"))
		{
			return false;
		}
		
		return true;
	}
	
	public void PrintVariable(String variable)
	{
		if(this.serverData.containsKey(variable))
			System.out.printf("%s=%s\n", variable, this.serverData.get(variable));
	}
}
