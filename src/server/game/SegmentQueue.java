package server.game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
	private int maxThreadsInPool = 10;
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
		try {
			this.fetchQueue.put(new SegmentQueueItem(target, tx, tz));
		} catch (InterruptedException e) {
			// TODO: Handle this - how? Need to discuss with Ben Allen. ~B.Anderson.
		}
	}
	
	public void setThreadPoolMax(int nMax)
	{
		this.maxThreadsInPool = nMax;
	}
	
	public void startThreadPool()
	{
		ensureThreadCount();
	}
	
	private void ensureThreadCount()
	{
		this.parentEngine.log.printf("SegmentQueue maxThreadsInPool=%d\n",  this.maxThreadsInPool);
		
		for(int i = 0; i < threadPool.size(); i++)
		{
			if(!threadPool.get(i).isAlive()) threadPool.remove(i);
		}
		
		while(threadPool.size() < this.maxThreadsInPool)
		{
			Thread newThread = new Thread(this);
			newThread.start();
			threadPool.add(newThread);
		}
		
		// TODO: Check if the thread pool has somehow gotten over the thread_max, if it has, ask threads to terminiate
	}
	
	@Override
	public void run() {
		SegmentQueueItem sgi = null;
		while(true)
		{
			sgi = null;
			try {
				sgi = this.fetchQueue.take();
			} catch (InterruptedException e) {
				// Again, how to handle this properly. Killing the thread will do for now - but its bad.
				// TODO: Discuss with Ben Allen. ~B.Anderson
				break;
			}
			
			Segment newSeg = this.gen.getSegment(sgi.getX(), sgi.getZ());
			SegmentPacket sp = new SegmentPacket();
			sp.segment = newSeg;
			if(sgi.getSession() != null)
				this.parentEngine.enqueueSend(sgi.getSession(), sp.toData());
			
			synchronized(this.fetchQueue)
			{
				this.fetchQueue.notify();
			}
		}
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
