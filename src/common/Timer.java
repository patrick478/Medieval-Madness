package common;

public class Timer {
	private long startTime;
	private long endTime;
	private long elapsedTime;
	
	public Timer() { }
	
	public Timer(boolean startNow)
	{
		if(startNow)
			this.start();
	}
	
	public void start()
	{
		this.startTime = System.nanoTime();
	}
	
	public void stop()
	{
		this.endTime = System.nanoTime();
		this.elapsedTime = this.endTime - this.startTime;
	}
	
	public long elapsed_msLong()
	{
		return (elapsedTime / 1000000);
	}
	
	public double elapsed_sDouble()
	{
		return (elapsedTime / 1000000000d);
	}
}
