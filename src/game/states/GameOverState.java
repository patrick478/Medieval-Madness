package game.states;

import game.Game;
import game.GameState;
import initial3d.engine.xhaust.Pane;
import initial3d.engine.xhaust.Picture;
import initial3d.engine.xhaust.vision.ActionIDList;
import initial3d.engine.xhaust.vision.Button;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GameOverState extends GameState {

	private Picture pic;

	@Override
	public void initalise() {
		
			Pane p = new Pane(800, 600);
			BufferedImage bg;
			try {
				bg = ImageIO.read(new File("resources/gameover.png"));	
			} catch (IOException e) {
				throw new AssertionError(e);
			}
			pic = new Picture(bg ,0,0, 800,600);
			p.getRoot().add(pic);
			
			Button mainMenu = new Button("Return to main menu", 125);
			mainMenu.setPosition(p.getRoot().getWidth()/2-mainMenu.getWidth()/2, p.getRoot().getHeight()/2-mainMenu.getHeight()/2);
			mainMenu.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(e.getID() != ActionIDList.CLICKED) return;
					Game.getInstance().changeState(new MainMenuState());
				}
			});
			
			p.getRoot().add(mainMenu);
			
	}
	
	
	

	@Override
	public void update(double delta) {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
