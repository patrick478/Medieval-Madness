package server.game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import server.session.Session;

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
	private int targetThreadsInPool = 1;
	private int defaultThreadsInPool = 1;
	private int extraJobTolerance = 4;
	
	private List<Thread> threadPool = new ArrayList<Thread>();
	private SegmentGenerator gen = null;
	private GameEngine parentEngine;
	
	public SegmentQueue(SegmentGenerator sg, GameEngine pEngine)
	{
		this.gen = sg;
		this.parentEngine = pEngine;
	}
	
	public void enqueueSegmentRequest(Session target, int tx, int tz)
	{
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
	
	public void setThreadPoolDefault(int nDef)
	{
		this.targetThreadsInPool = nDef;
		this.defaultThreadsInPool = nDef;
	}
	
	public void setJobTolerance(int nTol)
	{
		this.extraJobTolerance = nTol;
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
//		this.parentEngine.log.printf("SegmentQueue maxThreadsInPool=%d\n",  this.maxThreadsInPool);
		synchronized(threadPool)
		{
			for(int i = 0; i < threadPool.size(); i++)
			{
				if(threadPool.get(i) != null && threadPool.get(i).isAlive()) threadPool.remove(i);
			}
			
			while(threadPool.size() < (this.targetThreadsInPool))
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
				sgi = this.fetchQueue.poll(500, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// Again, how to handle this properly. Killing the thread will do for now - but its bad.
				// TODO: Discuss with Ben Allen. ~B.Anderson
				break;
			}
			if(sgi != null)
			{
				Segment newSeg = this.gen.getSegment(sgi.getX(), sgi.getZ());
				SegmentPacket sp = new SegmentPacket();
				sp.segment = newSeg;
				if(sgi.getSession() != null)
					this.parentEngine.enqueueSend(sgi.getSession(), sp.toData());
				
				synchronized(this.fetchQueue)
				{
					this.fetchQueue.notify();
				}
				
				if(sgi.isStats())
				{
					this.parentEngine.recordSegmentRequest(Segment.getID(sgi.getX(),  sgi.getZ()));
				}
			}

			if(this.fetchQueue.size() > this.extraJobTolerance && this.targetThreadsInPool < this.maxThreadsInPool)
			{
				this.targetThreadsInPool++;
//				this.parentEngine.log.printf("SegmentFetch thread pool spawning extra thread\n");
				this.ensureThreadCount();
				free = 0;
			}
			else if(this.fetchQueue.size() <= this.extraJobTolerance && this.targetThreadsInPool > this.defaultThreadsInPool)
			{
//				this.parentEngine.log.printf("SegmentFetch thread pool destroying extra thread\n");
				this.targetThreadsInPool--;
				break;
			}
			else if(this.fetchQueue.size() <= this.extraJobTolerance)
			{
				//this.targetThreadsInPool--;
				free++;
			}
			else
				free = 0;
		}
//		System.out.println("Thread dead");
		this.ensureThreadCount();
	}

	public void waitTillIdle() {
		while(!this.fetchQueue.isEmpty())
			synchronized(this.fetchQueue)
			{
				try {
					this.fetchQueue.wait(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
}
