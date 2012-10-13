package game.modelloader;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

public class Content implements Runnable {
	private static Content instance = null;
	public static Content get()
	{
		if(instance == null)
			instance = new Content();
		
		return instance;
	}
	
	private Map<String, AbstractContentLoader> loaders = new HashMap<String, AbstractContentLoader>();
	private Map<String, Object> content = new HashMap<String, Object>();
	private Thread loadThread = null;
	private BlockingQueue<ContentRequestItem> toLoad = new LinkedBlockingQueue<ContentRequestItem>();
	private boolean isRunning = true;
	
	public Content()
	{
		loaders.put("obj", new WavefrontLoader());
		loaders.put("png", new ImageLoader());
		
		this.loadThread = new Thread(this);
		this.loadThread.start();
	}
	
	@SuppressWarnings("unchecked")
	private <E> E load(String filename)
	{
		File f = new File(filename);
		if(!f.exists())
			return null;
		
		String extension = filename.substring(filename.lastIndexOf('.')+1, filename.length());
		if(!loaders.containsKey(extension.toLowerCase()))
		{
			System.err.printf("Unknown model format: %s\n",  extension);
			return null;
		}
		
		return (E)loaders.get(extension.toLowerCase()).Load(filename); 
	}
	
	public static void preloadContent(String filename, ContentRequest mcr)
	{
		get().toLoad.add(new ContentRequestItem(filename, mcr));
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T loadContent(String filename)
	{
		if(!get().content.containsKey(filename))
			get().content.put(filename, get().load(filename));
		
		return (T)get().content.get(filename);
	}

	@Override
	public void run() {
		this.isRunning = true;
		while(this.isRunning)
		{
			ContentRequestItem mcr = null;
			try {
				 mcr = this.toLoad.poll(1000,  TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
			}
			
			if(mcr == null)
				continue;
			
			this.load(mcr.getFilename());
			mcr.getCaller().loadComplete(mcr.getFilename());
		}
	}
}
