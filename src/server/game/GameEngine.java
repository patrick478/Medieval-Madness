package server.game;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import server.Server;
import server.net.ServerLayer;
import server.session.Session;

import common.DataPacket;
import common.Timer;
import common.Log;
import common.map.Segment;
import common.map.SegmentGenerator;

public class GameEngine {	
	private SegmentGenerator sg;
	public SegmentQueue segQueue;
	private Server server;
	private ServerLayer serverLayer;
	
	private Map<Long, Long> segmentStats = new HashMap<Long, Long>();
	
	public Log log;
	
	public GameEngine(long seed, PrintStream p0, Server server)
	{
		log = new Log("game.log", true, p0);
		log.setPrefix("(GameEngine) ");
		
		this.server = server;
		this.serverLayer = this.server.networking;
		
		this.log.printf("Game engine started. Worldseed=%d\n", seed);
		
		this.log.printf("Starting segment generator..\n");
		
		Timer t = new Timer(true);
		sg = new SegmentGenerator(seed);
		t.stop();
		
		this.log.printf("Segment generator started. Took %.2fs\n", t.elapsed_sDouble());
		
		segQueue = new SegmentQueue(sg, this);
		segQueue.startThreadPool();
		segQueue.setThreadPoolMax(this.server.serverSettings.getIntValue("max_threads_seg_fetch_pool", 8));
		segQueue.setThreadPoolDefault(this.server.serverSettings.getIntValue("default_threads_seg_fetch_pool", 2));
		segQueue.setJobTolerance(this.server.serverSettings.getIntValue("job_limit_spawm_threshold", 8));
	}
	
	public void warm()
	{
		this.log.printf("Beginning segment cache warmup\n");
		Timer t = new Timer(true);
		for(int i = -(this.server.serverSettings.getIntValue("spawn_cache_size",  12) / 2); i < this.server.serverSettings.getIntValue("spawn_cache_size",  12)/2; i++)
			for(int j = -(this.server.serverSettings.getIntValue("spawn_cache_size",  12) /2); j < this.server.serverSettings.getIntValue("spawn_cache_size",  12)/2; j++)
				this.segQueue.enqueueSegmentRequest(null, i+this.server.serverSettings.getIntValue("spawn_segment_x", 0), j + this.server.serverSettings.getIntValue("spawn_segment_z",  0));
		
		this.segQueue.waitTillIdle();
		
		t.stop();
		this.log.printf("Finished segment cache warmup in %.2fs\n", t.elapsed_sDouble());
	}
	
	protected void recordSegmentRequest(long segID)
	{
		Long cStat = 0l;
		if(this.segmentStats.containsKey(segID))
			cStat = this.segmentStats.get(segID);
		
		cStat++;
		this.segmentStats.put(segID, cStat);
	}
	
	public void enqueueSend(Session targetSession, DataPacket packet)
	{
		this.serverLayer.send(targetSession.getSocket(), packet.getData());
	}
	
	public Segment getSegment(int x, int z)
	{
		return sg.getSegment(x, z);
	}
	
	public void addSegmentRequest(Session s, int posx, int posz)
	{
		this.segQueue.enqueueSegmentRequest(s,  posx, posz);
	}
}
