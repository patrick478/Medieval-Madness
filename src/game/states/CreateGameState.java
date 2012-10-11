package game.states;

import initial3d.engine.xhaust.LabelV0;
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
import game.net.NetworkingHost;

/***
 * The create-a-game state, should show all the options then when "next" is pressed, goto the LobbyState.
 * @author Ben
 *
 */
public class CreateGameState extends GameState {
	public CreateGameState() {
	}
	
	Picture pic;
	TextFieldV0 numPlayers;

	@Override
	public void initalise() {
		
		BufferedImage bg;

	
		Pane p = new Pane(800, 600);
		try {
			bg = ImageIO.read(new File("resources/tower.jpg"));	
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		numPlayers = new TextFieldV0(80, 32);
		numPlayers.setNumericOnly(true);
		numPlayers.setPosition(100, 100);
		numPlayers.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Game.getInstance().setHost(new NetworkingHost());
				Game.getInstance().getHost().setNumPlayers(Integer.parseInt(numPlayers.getText()));
				Game.getInstance().getHost().start();
				System.out.printf("Creating game..\n");
				Game.getInstance().setNetwork(new NetworkingClient());
				Game.getInstance().getNetwork().start();
				Game.getInstance().changeState(new LobbyState());
			}
		});
		
		pic = new Picture(bg ,0,0, 800,600);
		
		p.getRoot().add(pic);
		p.getRoot().add(numPlayers);
		numPlayers.requestLocalFocus();
		
		
		
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
