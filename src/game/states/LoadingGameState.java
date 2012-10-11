package game.states;

import initial3d.engine.Color;
import initial3d.engine.xhaust.*;
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
	
	private LevelGenerator levelGen = new LevelGenerator(32l);
	
	private List<String> waitingOn = new ArrayList<String>();
	private String[] toLoad = new String[] {
			"cube.obj",
			"sphere.obj",
			"human.obj",
			"plane.obj",
			"trumpet.obj",
			"resources/texturetiles.png"
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
				
		System.out.println("You're a porqupine!");
		Game.getInstance().setLevel(levelGen.getLevel(Game.getInstance().getCurrentLevelNumber()));
		System.out.println("You're a porqupine, too!");
		
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
		double loadProgress = Math.ceil((this.waitingOn.size() / toLoad.length) * 100f);
		// for now - no loading
		if(this.waitingOn.size() == 0 && hasLoadedModels)
		{
			Game.getInstance().changeState(new PlayState());
			System.out.println("Finished waiting for content");
		}
//		else
//			System.out.printf("Loading %.2f complete..\n", loadProgress);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadComplete(String filename) {
		this.waitingOn.remove(filename);
		this.hasLoadedModels = true;
	}	
}
