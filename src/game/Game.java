package game;

import initial3d.Profiler;
import initial3d.engine.RenderWindow;
import initial3d.engine.SceneManager;
import game.GameStates.PreloadGameState;

/***
 * Main game class
 * @author Ben
 *
 */
public class Game implements Runnable {
	private final int gameHz = 30;
	private final long optimalTime = 1000000000 / gameHz;
	
	private GameState currentGameState;
	private Thread gameThread = null;
	private boolean gameRunning = false;
	private RenderWindow gameWindow = null;
	private SceneManager sceneManager = null;
	private int updatesPerSecond = 0;
	
	public Game()
	{
		this.changeState(new PreloadGameState(this));
	}
	
	/***
	 * Starts the main game including the game loop
	 */
	public void start()
	{
		this.gameRunning = true;
		this.gameThread = new Thread(this);
		this.gameThread.start();
	}
	
	public void stop()
	{
		this.gameRunning = false;
		try {
			this.gameThread.join();
		} catch (InterruptedException e) {
			System.out.println("Error: Unable to stop the main game thread");
		}
	}
	
	/***
	 * The main game loop thread - contains a much better implementation of ben a's update loop
	 */
	public void run()
	{
		long lastUpdateTime = System.nanoTime();
		long lastUpsTime = 0;
		int ups = 0;
		
		while(gameRunning)
		{
			long now = System.nanoTime();
			long updateLength = now - lastUpdateTime;
			lastUpdateTime = now;
			double delta = updateLength / ((double)optimalTime);
			
			lastUpsTime += updateLength;
			ups++;
			
			if(lastUpsTime >= 1000000000)
			{
				this.updatesPerSecond = ups;
				lastUpsTime = 0;
				ups = 0;
			}
			this.getState().update(delta);
			
			try
			{
				long sleepTime = (lastUpdateTime - System.nanoTime() + optimalTime) / 1000000;
				if(sleepTime > 0)
					Thread.sleep(sleepTime);
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/***
	 * Changes the main game state to the state supplied
	 * @param gs The state to be used as the main game state from now on
	 */
	public void changeState(GameState gs)
	{		
		Profiler p = new Profiler();
		p.setResetOutput(System.out);
		
		p.startSection("ChangeState-DestroyOldGameState()");
		if(this.currentGameState != null)
			this.currentGameState.destroy();
		p.endSection("ChangeState-DestroyOldGameState()");
		
		this.currentGameState = gs;
		
		p.startSection("ChangeState-InitaliseNewState()");
		this.currentGameState.initalise();
		p.endSection("ChangeState-InitaliseNewState()");
		
		p.startSection("ChangeState-AttachToScene()");
		if(this.sceneManager != null)
			this.sceneManager.attachToScene(this.currentGameState.scene);
		p.endSection("ChangeState-AttachToScene()");
		
		// This is used to figure out how long stuff is going.. disabled most of the time!
		// p.reset();
	}
	
	/***
	 * Returns the game state currently being run by the game
	 * @return The current GameState
	 */
	public GameState getState()
	{
		return this.currentGameState;
	}
	
	public int getUps()
	{
		return this.updatesPerSecond;
	}

	/***
	 *  Ensures the game has a valid game window
	 */
	public void createWindow()
	{
		this.gameWindow = RenderWindow.create(800, 600);
		this.gameWindow.setVisible(true);
		this.sceneManager = new SceneManager(800, 600);
		this.sceneManager.setDisplayTarget(this.gameWindow);
		this.sceneManager.getProfiler().setResetOutput(null);
	}
	
}
