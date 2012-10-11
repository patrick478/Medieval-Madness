package game.states;

import initial3d.engine.xhaust.Pane;
import initial3d.engine.xhaust.Picture;
import initial3d.engine.xhaust.vision.ActionIDList;
import initial3d.engine.xhaust.vision.Button;
import initial3d.engine.xhaust.vision.Input;
import initial3d.engine.xhaust.vision.Label;

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
	Input numPlayers;
	Label numPlayersLabel;
	Button createGameButton;

	@Override
	public void initalise() {
		
		BufferedImage bg;

	
		Pane p = new Pane(800, 600);
		try {
			bg = ImageIO.read(new File("resources/tower.jpg"));	
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		
		numPlayersLabel = new Label("Max players: ");
		numPlayersLabel.setPosition(20,  100);
		numPlayers = new Input(30);
		numPlayers.setNumericOnly(true);
		numPlayers.setPosition(110, 100);
		
		createGameButton = new Button("Create Game");
		createGameButton.setPosition(300, 200);
		createGameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getID() != ActionIDList.CLICKED) return;
				Game.getInstance().setHost(new NetworkingHost());
				Game.getInstance().getHost().setNumPlayers(Integer.parseInt(numPlayers.getText()));
				Game.getInstance().getHost().start();
				
				Game.getInstance().setNetwork(new NetworkingClient());
				Game.getInstance().getNetwork().start();
				
				Game.getInstance().changeState(new LobbyState());
			}
		});
		
		pic = new Picture(bg, 0, 0, 800, 600);
		
		p.getRoot().add(pic);
		p.getRoot().add(numPlayersLabel);
		p.getRoot().add(numPlayers);
		p.getRoot().add(createGameButton);
		p.setPosition(0, 0);
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
