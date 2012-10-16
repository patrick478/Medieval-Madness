package game.states;

import initial3d.engine.xhaust.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;

import game.Game;
import game.GameState;
import game.level.LevelGenerator;
import game.modelloader.Content;
import game.modelloader.ContentRequest;

/***
 * This is the state required to load any character models, etc before playing the actual game
 * @author Ben
 *
 */
public class LoadingGameState extends GameState implements ContentRequest {
	public LoadingGameState() {
	}
	
	BufferedImage bg;
	Picture pic;
	private boolean hasLoadedModels = false;
	private double loadProgress = 0;
	
	private LevelGenerator levelGen = new LevelGenerator(32l);
	
	private List<String> waitingOn = new ArrayList<String>();
	private String[] toLoad = new String[] {
			"leftarrow.png",
			"mainmenu.png",
			"mainmenujoin.png",
			"mainmenustart.png",
			"rightarrow.png",
			"serverpage.png",
			"testuretiles.png",
			"texturetiles.png",
			"tower.jpg",
			"toweroriginal.jpg",
			"./inventory/battery.png",
			"./inventory/blank.png",
			"./inventory/box.png",
			"./inventory/gun.png",
			"./inventory/inventoryselect.png",
			"./inventory/key.png",
			"./inventory/selected.png",
			"./models/battery/battery.obj",
			"./models/battery/battery_kd.png",
			"./models/battery/battery_ke.png",
			"./models/battery/battery_outline.png",
			"./models/box/box.obj",
			"./models/box/box_kd.png",
			"./models/bullet/bullet.obj",
			"./models/character/char2_arm_l.obj",
			"./models/character/char2_arm_r.obj",
			"./models/character/char2_body.obj",
			"./models/character/char2_gun.obj",
			"./models/character/char2_kd.png",
			"./models/character/char2_ke.png",
			"./models/character/char2_outline.png",
			"./models/doorbars/doorbars.obj",
			"./models/doorbars/doorbars2.obj",
			"./models/gunpart/gunpart.obj",
			"./models/key/key.obj",
			"./models/spikeball/spikeball.obj",
			"./ui/health.png"
	};

	@Override
	public void initalise() {		
		Pane p = new Pane(800, 600);
		try {
			bg = ImageIO.read(new File("resources/tower.jpg"));	
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		pic = new Picture(bg ,0,0, 800,600);
		p.getRoot().add(pic);
		//input from user
		LabelV0 progress = new LabelV0(300, 30, "Loading..");
		progress.setBackgroundColor(new java.awt.Color(160, 160, 160));
		progress.setForegroundColor(java.awt.Color.BLACK);
		progress.setPosition(470, 540);
		p.getRoot().add(progress);
		scene.addDrawable(p);

		p.requestVisible(true);
		
		//load level here and let itself add to the game
		levelGen.loadLevel(Game.getInstance().getCurrentLevelNumber());
		
		loadModels();
	}
	
	public void loadModels()
	{
		for(int i = 0; i < toLoad.length; i++)
		{
			this.waitingOn.add(toLoad[i]);
			Content.preloadContent(toLoad[i], this);
		}
	}
	
	public void repaint(){
		pic.repaint();
	}

	@Override
	public void update(double delta) {
		this.loadProgress = Math.ceil((this.waitingOn.size() / toLoad.length) * 100f);
		// for now - no loading
		if(this.waitingOn.size() == 0 && hasLoadedModels)
		{
			Game.getInstance().changeState(new PlayState());
		}

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	
	public double getProgress() {
		return this.loadProgress;
	}

	@Override
	public void loadComplete(String filename) {
		this.waitingOn.remove(filename);
		this.hasLoadedModels = true;
	}	
}
