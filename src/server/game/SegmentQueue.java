package server.game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import server.session.Session;

import common.Timer;
import common.map.Segment;
import common.map.SegmentGenerator;
import common.packets.SegmentPacket;

/***
 * 
 * @author "Ben Anderson (BageDevimo)"
 * An class which is capable of using an arbitary number of threads to respond to requests for segments
 */
public class SegmentQueue implements Runnable {
	private BlockingQueue<SegmentQueueItem> fetchQueue = new LinkedBlockingQueue<SegmentQueueItem>();

	private int maxThreadsInPool = 2;
	
	private List<Thread> threadPool = new ArrayList<Thread>();
	private SegmentGenerator gen = null;
	private GameEngine parentEngine;
	
	public SegmentQueue(SegmentGenerator sg, GameEngine pEngine)
	{
		this.gen = sg;
		this.parentEngine = pEngine;
	}
	
	private int added = 0;
	
	public void enqueueSegmentRequest(Session target, int tx, int tz)
	{
//		System.out.printf("Added #%d\n", ++added);
		boolean useInStats = false;
		
		if(target != null)
			useInStats = true;
		
		try {
			this.fetchQueue.put(new SegmentQueueItem(target, tx, tz, useInStats));
		} catch (InterruptedException e) {
			// TODO: Handle this - how? Need to discuss with Ben Allen. ~B.Anderson.
		}
	}
	
	public void setThreadPoolMax(int nMax)
	{
		this.maxThreadsInPool = nMax;
	}
	
	public int getCurrentThreadCount()
	{
		return this.threadPool.size();
	}	
	
	public void startThreadPool()
	{
		ensureThreadCount();
	}
	
	private void ensureThreadCount()
	{
//		System.out.println("Ensuring..");
//		this.parentEngine.log.printf("SegmentQueue maxThreadsInPool=%d\n",  this.maxThreadsInPool);
		synchronized(threadPool)
		{
			for(int i = 0; i < threadPool.size(); i++)
			{
				if(threadPool.get(i) != null && !threadPool.get(i).isAlive()) 
				{
					threadPool.remove(i);
//					System.out.println("Removing a dead thread");
				}
			}
			
			while(threadPool.size() < (this.maxThreadsInPool))
			{
				Thread newThread = new Thread(this);
				newThread.setDaemon(true);
				newThread.start();
				threadPool.add(newThread);
//				this.parentEngine.log.printf("SegmentFetch thread pool thread spawned. currentThreads=%d\n", this.threadPool.size());
			}
		}
		
		// TODO: Check if the thread pool has somehow gotten over the thread_max, if it has, ask threads to terminiate
	}
	
	@Override
	public void run() {
		SegmentQueueItem sgi = null;
		int free = 0;
		
		while(true)
		{
			sgi = null;
			try {
				sgi = this.fetchQueue.poll(10,  TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// Again, how to handle this properly. Killing the thread will do for now - but its bad.
				// TODO: Discuss with Ben Allen. ~B.Anderson
				break;
			}
			if(sgi != null)
			{
				Timer t = new Timer(true);
				Segment newSeg = this.gen.getSegment(sgi.getX(), sgi.getZ());
				t.stop();
//				System.out.printf("Seggen took %.2f\n", t.elapsed_sDouble());
				SegmentPacket sp = new SegmentPacket();
				sp.segment = newSeg;
				
				if(sgi.getSession() != null && !sgi.getSession().hasSegment(newSeg.id))
				{
					this.parentEngine.enqueueSend(sgi.getSession(), sp.toData());
					sgi.getSession().sentSegment(newSeg.id);
				}
				
				synchronized(this.fetchQueue)
				{
					this.fetchQueue.notify();
				}
				
				if(sgi.isStats())
				{
					this.parentEngine.recordSegmentRequest(Segment.getID(sgi.getX(),  sgi.getZ()));
				}
			}
			this.ensureThreadCount();
		}
//		System.out.println("Thread dead");
		this.ensureThreadCount();
	}

	public void waitTillIdle() {
		double freq = 0.5;
		double max = -1;
		double logFreq = 5000;
		double lastLog = System.currentTimeMillis();
		while(!this.fetchQueue.isEmpty())
		{
			if(max < this.fetchQueue.size())
				max = (double)this.fetchQueue.size();
			
			if(System.currentTimeMillis() - lastLog > logFreq)
			{
				this.logProgress(max);
				lastLog = System.currentTimeMillis();
			}
			
			try {
				Thread.sleep((long) (freq * 1000));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		this.logProgress(max);
		lastLog = System.currentTimeMillis();
	}
	
	public void logProgress(double max)
	{
		double progress = (1f - (this.fetchQueue.size() / max)) * 100f;		
		this.parentEngine.log.printf("SegmentFetch waiting on %d requests. %d percent complete.\n", this.fetchQueue.size(), (int)progress);
	}

	public Segment getSegmentFromWorld(double x, double z) {
		return this.gen.segmentAt(x,  z);
	}
}
