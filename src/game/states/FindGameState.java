package game.states;

import initial3d.engine.xhaust.Pane;
import initial3d.engine.xhaust.Picture;
import initial3d.engine.xhaust.TextFieldV0;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import game.Game;
import game.GameState;
import game.net.NetworkingClient;

/***
 * This state will display a list of games to join and allow a user to join a game
 * @author Ben
 *
 */
public class FindGameState extends GameState {
	public FindGameState() {
	}
	
	private TextFieldV0 target;
	private Picture pic;

	@Override
	public void initalise() {
		BufferedImage bg;

		
		Pane p = new Pane(800, 600);
		try {
			bg = ImageIO.read(new File("resources/tower.jpg"));	
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		target = new TextFieldV0(200, 32);
		target.setPosition(100, 100);
		target.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Game.getInstance().setNetwork(new NetworkingClient());
				Game.getInstance().getNetwork().setRemoteTarget(target.getText());
				Game.getInstance().getNetwork().start();
			}
		});
		
		pic = new Picture(bg ,0,0, 800,600);
		
		p.getRoot().add(pic);
		p.getRoot().add(target);
		target.requestLocalFocus();
		
		
		
		scene.addDrawable(p);

		p.requestVisible(true);
		p.requestFocus();
}

		public void repaint(){
			pic.repaint();
		}

	@Override
	public void update(double delta) {
	}

	@Override
	public void destroy() {		
	}	
}
