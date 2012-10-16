package game;

import game.entity.Damageable;
import game.entity.Entity;
import game.entity.moveable.MoveableEntity;
import game.entity.moveable.PlayerEntity;
import game.entity.moveable.ProjectileEntity;
import game.entity.moveable.SpikeBallEntity;
import game.item.Item;
import game.level.Level;
import game.net.NetworkingClient;
import game.net.NetworkingHost;
import game.net.packets.ChangeAttributePacket;
import game.net.packets.EnterPrePostPacket;
import game.net.packets.EntityDestroyPacket;
import game.net.packets.GiveItemPacket;
import game.net.packets.ItemLifePacket;
import game.net.packets.MoveMobPacket;
import game.net.packets.MovementPacket;
import game.net.packets.ProjectileLifePacket;
import game.net.packets.SetReadyPacket;
import game.states.GameOverState;
import game.states.LobbyState;
import game.states.PregameState;
import game.states.PreloadGameState;
import initial3d.Profiler;
import initial3d.engine.Quat;
import initial3d.engine.RenderWindow;
import initial3d.engine.SceneManager;
import initial3d.engine.Vec3;
import initial3d.engine.xhaust.DialogPane;
import initial3d.engine.xhaust.Pane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/***
 * Main game class
 * 
 * @author Ben
 * 
 */
public class Game implements Runnable {
	private static Game gameInstance = null;

	public static Game getInstance() {
		if (gameInstance == null) gameInstance = new Game();

		return gameInstance;
	}

	public static long time() {
		return getInstance().getGameTime();
	}
	
	public static long time(long systime) {
		return getInstance().getGameTime(systime);
	}

	private final int gameHz = 30;
	private final long optimalTime = 1000000000 / gameHz;

	private DialogPane invenPopUp;
	private Pane inventoryHolder;
	private GameState currentGameState;
	private GameState previousState = null; 
	private Thread gameThread = null;
	private boolean gameRunning = false;
	private RenderWindow gameWindow = null;
	private SceneManager sceneManager = null;
	private int updatesPerSecond = 0;
	private NetworkingClient network = null;
	private NetworkingHost nhost = null;
	private int playerIndex = -1;

	private Level currentLevel = null;
	private int currentLevelNumber = 1;
	private boolean isPregameReady = false;

	private long predictedLatency = 0;
	private long timeOffset = 0;
	private int maxPlayers = 1;
	
	private long startGameTime;
	private long endGameTime = 0;
	private long timeLeft = 0;

	private Map<Integer, PlayerEntity> players = new HashMap<Integer, PlayerEntity>();
	private List<Item> itemInWorld = new ArrayList<Item>();

	private Game() {
	}

	/***
	 * Starts the main game including the game loop
	 */
	public void start() {
		this.changeState(new PreloadGameState());
		startGameTime = Game.time();
		this.gameRunning = true;
		this.gameThread = new Thread(this);
		this.gameThread.start();
	}

	public void stop() {
		this.gameRunning = false;
		try {
			this.gameThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/***
	 * The main game loop thread - contains a much better implementation of ben
	 * a's update loop
	 */
	public void run() {
		long lastUpdateTime = System.nanoTime();
		long lastUpsTime = 0;
		int ups = 0;

		while (gameRunning) {
			long now = System.nanoTime();
			long updateLength = now - lastUpdateTime;
			lastUpdateTime = now;
			double delta = updateLength / 1000000000d;

			lastUpsTime += updateLength;
			ups++;

			if (lastUpsTime >= 1000000000) {
				this.updatesPerSecond = ups;
				lastUpsTime = 0;
				ups = 0;
			}
			this.getState().update(delta);

			try {
				long sleepTime = (lastUpdateTime - System.nanoTime() + optimalTime) / 1000000;
				if (sleepTime > 0) Thread.sleep(sleepTime);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/***
	 * Changes the main game state to the state supplied
	 * 
	 * @param gs
	 *            The state to be used as the main game state from now on
	 */
	public void changeState(GameState gs) {
		Profiler p = new Profiler();
		p.setResetOutput(System.out);

		p.startSection("ChangeState-DestroyOldGameState()");
		if (this.previousState != null) this.previousState.destroy();
		p.endSection("ChangeState-DestroyOldGameState()");
		this.previousState = this.currentGameState;
		this.currentGameState = gs;

		p.startSection("ChangeState-InitaliseNewState()");
		this.currentGameState.initalise();
		p.endSection("ChangeState-InitaliseNewState()");

		p.startSection("ChangeState-AttachToScene()");
		if (this.sceneManager != null) this.sceneManager.attachToScene(this.currentGameState.scene);
		p.endSection("ChangeState-AttachToScene()");

		
	}
	
	public void revertState() {
		if (this.currentGameState != null) this.currentGameState.destroy();

		this.currentGameState = this.previousState;

		if (this.sceneManager != null) this.sceneManager.attachToScene(this.currentGameState.scene);


	
	}

	/***
	 * Returns the game state currently being run by the game
	 * 
	 * @return The current GameState
	 */
	public GameState getState() {
		return this.currentGameState;
	}

	public int getUps() {
		return this.updatesPerSecond;
	}

	/***
	 * Ensures the game has a valid game window
	 */
	public void createWindow() {
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

	public RenderWindow getWindow() {
		return this.gameWindow;
	}

	public NetworkingClient getNetwork() {
		return this.network;
	}

	public void setNetwork(NetworkingClient nm) {
		if (this.network != null) this.network.destroy();

		this.network = nm;
	}

	public void setHost(NetworkingHost networkingHost) {
		if (this.nhost != null) this.nhost.destroy();

		this.nhost = networkingHost;
	}

	public void setPlayerIndex(int pIndex) {
		this.playerIndex = pIndex;

		PlayerEntity pe = new PlayerEntity(-pIndex - 10, Vec3.create(pIndex + 2, 0.220, pIndex + 2), 0.125, pIndex);

		addPlayer(pIndex, pe);
	}

	public void setMaxPlayers(short _maxPlayers) {
		// These can add the rest of the players
		for (int i = 0; i < _maxPlayers; i++) {
			if (i == this.playerIndex) continue;
			
			PlayerEntity p = new PlayerEntity(-i - 10, Vec3.create(i + 2, 0.125, i + 2), 0.125, i);
			addPlayer(i, p);
		}

		this.maxPlayers = _maxPlayers;
	}

	public int getPlayerIndex() {
		return this.playerIndex;
	}
	
	public PlayerEntity getPlayer() {
		return this.players.get(this.playerIndex);
	}

	public void addPlayer(int index, PlayerEntity e) {
		this.players.put(index, e);
	}

	public void movePlayer(int playerIndex, Vec3 position, Vec3 velocity, Quat orientation) {
		PlayerEntity me = this.players.get(playerIndex);
		if (me == null) return;

		me.updateMotion(position, velocity, orientation, Vec3.zero, this.getGameTime());
	}

	public void transmitPlayerPosition() {
		MovementPacket mp = new MovementPacket(this.getPlayerIndex(), this.getPlayer().getPosition(), this.getPlayer().getLinVelocity(), this.getPlayer().getOrientation());
		this.getNetwork().send(mp.toData());
	}

	public void addEntity(Entity _entity)
	{
		_entity.addToLevel(currentLevel);
		_entity.addToScene(currentGameState.scene);
	}

	public void removeEntity(long _eid) {
		if(this.isHost())
		{
			EntityDestroyPacket edp = new EntityDestroyPacket();
			edp.eid = _eid;
			this.getHost().notifyAllClients(edp);			
		}
		
		Entity e = currentLevel.removeEntity(_eid);
		if(e!=null){
			currentGameState.scene.removeDrawables(e.getMeshContexts());
		}
	}
	
	public void spawnItem(Item _item)
	{
		ItemLifePacket ilp = new ItemLifePacket();
		ilp.itemType = _item.type;
		ilp.itemID = _item.id;
		ilp.position = _item.getPosition();
		ilp.setCreate();
		if(this.isHost())
		{
			this.getHost().notifyAllClients(ilp);
			selfSpawnItem(_item);
			this.itemInWorld.add(_item);
		}
	}
	
	public void selfSpawnItem(Item _item) {
		addEntity(_item.getItemEntity());
		this.itemInWorld.add(_item);
	}
	
	public void addItemToPlayer(long _eid, long itemid) {
		// find hte item
		Item cItem = null;
		for(Item i : this.itemInWorld)
		{
			if(i.id == itemid)
			{
				cItem = i;
				break;
			}
		}
		
		if(cItem == null) {
			return;
		}
	
		
		for(PlayerEntity p : getPlayers()){
			if(p.id == _eid){
				if(p.getInventory().containsItem(cItem)){
					break;
				}
				p.getInventory().addItem(cItem);
				invenPopUp.getRoot().repaint();
				
				if(this.isHost())
				{
					GiveItemPacket gip = new GiveItemPacket();
					gip.eid = p.id;
					gip.itemID = cItem.id;
					this.getHost().notifyAllClients(gip);
				}
				
				break;
			}
		}
	}
	
	public void removeItemFromPlayer(long _eid, Item _item){
	}

	public boolean isHost() {
		return this.nhost != null;
	}

	public long getGameTime() {
		return System.currentTimeMillis() + this.timeOffset;
	}
	
	public long getGameTime(long systime) {
		return systime + this.timeOffset;
	}

	public void setTimeOffset(long offset) {
		this.timeOffset = offset;
	}

	public void setPredictedLatency(long pl) {
		this.predictedLatency = pl;
	}

	public Level getLevel() {
		return currentLevel;
	}

	public void setLevel(Level level) {
		currentLevel = level;
	}

	public int getCurrentLevelNumber() {
		return this.currentLevelNumber;
	}

	public NetworkingHost getHost() {
		return this.nhost;
	}

	public int getMaxPlayers() {
		return this.maxPlayers;
	}

	public PlayerEntity[] getPlayers() {
		PlayerEntity[] ps = new PlayerEntity[this.players.size()];
		for (int i = 0; i < this.players.size(); i++)
			ps[i] = this.players.get(i);

		return ps;
	}

	public void createProjectile() {
		long id = System.nanoTime();
		Vec3 norm = this.currentGameState.scene.getCamera().getNormal().flattenY().unit();
		Vec3 vel = this.getPlayer().getLinVelocity().add(norm.scale(8));
		Vec3 pos = this.getPlayer().getPosition().add(norm.scale(0.4));
		Quat orientation = this.getPlayer().getOrientation();
		short creator = (short) this.getPlayerIndex();
		long createTime = System.currentTimeMillis();
		
		ProjectileLifePacket pl = new ProjectileLifePacket();
		pl.eid = id;
		pl.pos = pos;
		pl.vel = vel;
		pl.ori = orientation;
		pl.creator = creator;
		pl.createTime = createTime;
		pl.setCreateMode();
		
		this.getNetwork().send(pl.toData());
	}

	public void selfCreateProjectile(long id, Vec3 position, Vec3 velocity, Quat orientation, short creator, long createTime) {
//		public ProjectileEntity(long _id, long _parid, int _delta, Vec3 _pos, Vec3 _vel)
		ProjectileEntity pe = new ProjectileEntity(id, getPlayers()[creator].id, -5, position, velocity, orientation);		
		pe.addToLevel(Game.getInstance().getLevel());
		pe.addToScene(Game.getInstance().currentGameState.scene);
		getPlayers()[creator].muzzleFlash(true);
	}

	public void setSelfPregameReady(boolean b) {
		Game.getInstance().getPlayer().setPregameReadyState(b);
		SetReadyPacket srp = new SetReadyPacket();
		srp.newReadyStatus = Game.getInstance().getPlayer().getPregameReadyState();
		srp.pIndex = this.getPlayerIndex();
		byte[] data = srp.toData().getData();
		this.getNetwork().send(srp.toData());
	}
	
	public void setPregameReady(int pIndex, boolean b)
	{
		Game.getInstance().getPlayers()[pIndex].setPregameReadyState(b);
	}

	public boolean isPregameReady() {
		return this.isPregameReady;
	}
	
	public void setInvenPopUp(DialogPane invenPopUp) {
		this.invenPopUp = invenPopUp;
	}
	
	public DialogPane getInvenPopUp() {
		return invenPopUp;
	}

	public Pane getInventoryHolder() {
		return inventoryHolder;
	}

	public void setInventoryHolder(Pane inventoryHolder) {
		this.inventoryHolder = inventoryHolder;
	}

	public void updatePregameScreen() {
		if(this.currentGameState instanceof PregameState)
		{
			((PregameState)this.currentGameState).updatePregameScreen();
		}
	}

	public void setGameStarting() {
		if(this.currentGameState instanceof PregameState)
		{
			((PregameState)this.currentGameState).setGameStarting();
		}
	}

	public void requestStart() {
		if(this.isHost())
			this.getHost().requestStart();
	}

	public void setLobbyCurrentPlayers(int nPlayers) {
		if(this.currentGameState instanceof LobbyState)
		{
			((LobbyState)this.currentGameState).setNumPlayers(nPlayers);
		}
	}

	public void setEntityHealth(long id, int delta)
	{
		ChangeAttributePacket cap = new ChangeAttributePacket();
		cap.setHealth();
		cap.eid = id;
		cap.newVal = delta;
		this.nhost.notifyAllClients(cap);
	}
	
	
	public void selfSetEntityHealth(long id, int i)
	{
		Damageable d = (Damageable) this.currentLevel.getEntity(id);
		if(d == null) return;
		d.setCurrentHealth(i);
	}
	
	public void finishLevel()
	{
		this.currentLevelNumber++;
		timeLeft = endGameTime - Game.time();
		EnterPrePostPacket erp = new EnterPrePostPacket(this.startGameTime);
		erp.setPre();
		if(this.isHost())
		{
			this.getHost().notifyAllClients(erp);
		}
	}

	public void startTimer() {
		this.startGameTime = Game.time();
		this.endGameTime = this.startGameTime + 300000 + timeLeft;
	}

	public long getStartTime() {
		return this.startGameTime;
	}

	public long getRemainingMs() {
		return this.endGameTime - Game.time();
	}
	
	public void moveMob(long eid, Vec3 pos, Vec3 vel, Quat ori)
	{
		MoveableEntity me = (MoveableEntity)Game.getInstance().getLevel().getEntity(eid);
		if(me == null) {
			me = new SpikeBallEntity(eid, 100, -1, pos, 0.5);
			me.addToLevel(Game.getInstance().getLevel());
			me.addToScene(Game.getInstance().currentGameState.scene);
		}
		if(this.isHost())
		{
			MoveMobPacket mmb = new MoveMobPacket();
			mmb.eid = eid;
			mmb.pos = pos;
			mmb.vel = vel;
			mmb.ori = ori;
			this.getHost().notifyAllClients(mmb);
		}
		me.updateMotion(pos, vel, ori, Vec3.zero, Game.time());
	}
	public void gameOver() {
		Game.getInstance().changeState(new GameOverState());
		Game.getInstance().getHost().shutdown();
		Game.getInstance().getNetwork().shutdown();
	}

	public int alivePlayers() {
		int alive = 0;
		for(PlayerEntity pe : this.players.values())
			if(!pe.isDead()) alive++;
		
		return alive;
	}
}
