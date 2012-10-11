package game;

import java.util.*;

import game.entity.Entity;
import game.entity.moveable.PlayerEntity;
import game.level.Level;
import game.net.NetworkingClient;
import initial3d.*;
import initial3d.engine.*;
import game.net.*;
import game.net.packets.MovementPacket;
import game.states.*;

/***
 * Main game class
 * @author Ben
 *
 */
public class Game implements Runnable {
	private static Game gameInstance = null;
	public static Game getInstance()
	{
		if(gameInstance == null)
			gameInstance = new Game();
		
		return gameInstance;
	}
	
	private final int gameHz = 30;
	private final long optimalTime = 1000000000 / gameHz;
	
	private GameState currentGameState;
	private Thread gameThread = null;
	private boolean gameRunning = false;
	private RenderWindow gameWindow = null;
	private SceneManager sceneManager = null;
	private int updatesPerSecond = 0;
	private NetworkingClient network = null;
	private NetworkingHost nhost = null;
	private int playerIndex = -1;
	
	private Level currentLevel = null;
	
	private long predictedLatency = 0;
	private int maxPlayers = 1;
	
	public PlayerEntity player = null;
	private Map<Integer, PlayerEntity> players = new HashMap<Integer, PlayerEntity>();
	
	private Game()
	{
	}
	
	/***
	 * Starts the main game including the game loop
	 */
	public void start()
	{
		this.changeState(new PreloadGameState());
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
			e.printStackTrace();
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
			double delta = updateLength / 1000000000d;
			
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
		RenderWindow rwin = this.gameWindow;
		this.sceneManager.setDisplayTarget(rwin);
		rwin.addKeyListener(sceneManager);
		rwin.addCanvasMouseListener(sceneManager);
		rwin.addCanvasMouseMotionListener(sceneManager);
		rwin.addMouseWheelListener(sceneManager);
		
		this.sceneManager.getProfiler().setResetOutput(null);
	}
	
	public RenderWindow getWindow()
	{
		return this.gameWindow;
	}	
	
	public NetworkingClient getNetwork()
	{
		return this.network;
	}
	
	public void setNetwork(NetworkingClient nm)
	{
		if(this.network != null)
			this.network.destroy();
		
		this.network = nm;
	}

	public void setHost(NetworkingHost networkingHost) {
		if(this.nhost != null)
			this.nhost.destroy();
		
		this.nhost = networkingHost;
	}

	public void setPlayerIndex(int pIndex) 
	{
		this.playerIndex = pIndex;

		this.player = new PlayerEntity(0, Vec3.create(pIndex+2, 0.125, pIndex+2), 0.125);
		
		// This needs to add the main player
		addPlayer(pIndex, player);
		
	}

	public void setMaxPlayers(short _maxPlayers) {
		// These can add the rest of the players
		for(int i = 0; i < _maxPlayers; i++)
		{
			if(i == this.playerIndex) continue;
			
			PlayerEntity p = new PlayerEntity(0, Vec3.create(i+2, 0.125, i+2), 0.125);
			addPlayer(i, p);
		}
		
		this.maxPlayers = _maxPlayers;
	}

	public int getPlayerIndex() {
		return this.playerIndex;
	}
	
	public void addPlayer(int index, PlayerEntity e)
	{
		this.players.put(index,  e);
	}

	public void movePlayer(int playerIndex, Vec3 position, Vec3 velocity)
	{
		PlayerEntity me = this.players.get(playerIndex);
		if(me == null)
			return;
		
		//TODO need to transmit orientation and angular velocity
		me.updateMotion(position, velocity, Quat.one, Vec3.zero, this.getGameTime());
	}

	public void transmitPlayerPosition() {
		MovementPacket mp = new MovementPacket(this.getPlayerIndex(), this.player.getPosition(), this.player.getLinVelocity());
		this.getNetwork().send(mp.toData());
	}
	
	
	//METHODS CALLED BY THE EVENT CLASSES
	//TODO need to notify the clients about this
	
	public void moveEntity(long _eid, Vec3 _pos){
		Entity e = currentLevel.getEntity(_eid);
		e.setPosition(_pos);
	}
	
	public void turnEntity(long _eid, Quat _orient){
		Entity e = currentLevel.getEntity(_eid);
		e.setOrientation(_orient);
	}
	
	public void addEntity(Entity _entity){
		currentLevel.addEntity(_entity);
	}
	
	public void removeEntity(long _eid){
		currentLevel.removeEntity(_eid);
	}
	
	public boolean isHost()
	{
		return this.nhost != null;
	}
	
	public long getGameTime()
	{
		return System.currentTimeMillis() + this.predictedLatency;
	}

	public void setPredictedLatency(long pl)
	{
		this.predictedLatency = pl;
	}
	
	public Level getLevel(){
		return currentLevel;
	}
	
	public void setLevel(Level level){
		currentLevel = level;
	}

	public int getCurrentLevelNumber() {
		return 0;
	}

	public NetworkingHost getHost() {
		return this.nhost;
	}
	
	public int getMaxPlayers()
	{
		return this.maxPlayers;
	}
	
	public PlayerEntity[] getPlayers() {
		PlayerEntity[] ps = new PlayerEntity[this.players.size()];
		for(int i = 0; i < this.players.size(); i++)
			ps[i] = this.players.get(i);
		
		return ps;
	}
}

