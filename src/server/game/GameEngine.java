package server.game;

import java.io.PrintStream;

import server.net.ServerLayer;
import server.session.Session;

import common.DataPacket;
import common.Timer;
import common.Log;
import common.map.Segment;
import common.map.SegmentGenerator;

public class GameEngine {
	public static final int cacheSize = 10;
	
	private SegmentGenerator sg;
	private SegmentQueue segQueue;
	private ServerLayer serverLayer;
	
	public Log log;
	
	public GameEngine(long seed, PrintStream p0, ServerLayer pServerLayer)
	{
		log = new Log("game.log", true, p0);
		log.setPrefix("(GameEngine) ");
		
		this.serverLayer = pServerLayer;
		
		this.log.printf("Game engine started. Worldseed=%d\n", seed);
		
		this.log.printf("Starting segment generator..\n");
		
		Timer t = new Timer(true);
		sg = new SegmentGenerator(seed);
		t.stop();
		
		this.log.printf("Segment generator started. Took %.2fs\n", t.elapsed_sDouble());
		
		segQueue = new SegmentQueue(sg, this);
		segQueue.startThreadPool();
	}
	
	public void warm()
	{
		this.log.printf("Beginning segment cache warmup\n");
		Timer t = new Timer(true);
		for(int i = -(cacheSize / 2); i < cacheSize/2; i++)
			for(int j = -(cacheSize /2); j < cacheSize/2; j++)
				this.segQueue.enqueueSegmentRequest(null, i, j);
		
		this.segQueue.waitTillIdle();
		
		t.stop();
		this.log.printf("Finished segment cache warmup in %.2fs\n", t.elapsed_sDouble());
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
