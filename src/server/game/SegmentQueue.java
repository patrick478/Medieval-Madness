package server.game;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SegmentQueue implements Runnable {
	BlockingQueue<SegmentQueueItem> fetchQueue = new LinkedBlockingQueue<SegmentQueueItem>();
	@Override
	public void run() {
		
	}
	
}
