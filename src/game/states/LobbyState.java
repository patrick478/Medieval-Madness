package game.states;

import initial3d.engine.xhaust.Pane;
import initial3d.engine.xhaust.Picture;
import initial3d.engine.xhaust.vision.ActionIDList;
import initial3d.engine.xhaust.vision.Button;
import initial3d.engine.xhaust.vision.Label;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import game.Game;
import game.GameState;

/*** 
 * The state used while waiting for the correct number of players.
 *
 * @author Ben
 *
 */
public class LobbyState extends GameState {
	public LobbyState() {

	}
	Label statusLabel = null;
	Button startButton = null;
	Pane pane = null;

	int nPlayers = 0;

	@Override
	public void initalise() {
		BufferedImage bg;


		Pane p = new Pane(800, 600);
		try {
			bg = ImageIO.read(new File("resources/lobbypage.png"));	
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		statusLabel = new Label(String.format("Waiting for %d more players..", Game.getInstance().getMaxPlayers() - this.nPlayers), 200);
		startButton = new Button("Start");		
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getID() != ActionIDList.CLICKED)
					return;

				Game.getInstance().requestStart();
				// handle
			}
		});
		Picture pic = new Picture(bg, 0, 0, 800, 600);

		p.getRoot().add(pic);

		statusLabel.setPosition(100, 80);
		startButton.setPosition(100, 120);
		p.getRoot().add(statusLabel);
		scene.addDrawable(p);
		p.requestFocus();
		p.requestVisible(true);
		this.pane = p;
	}

	@Override
	public void update(double delta) {
	}

	@Override
	public void destroy() {
	}

	public void setNumPlayers(int nPlayers) {
		this.nPlayers = nPlayers;
		statusLabel.setText(String.format("Waiting for %d more players..", Game.getInstance().getMaxPlayers() - this.nPlayers));
		if(this.nPlayers == Game.getInstance().getMaxPlayers())
		{
			pane.getRoot().add(this.startButton);
			pane.getRoot().repaint();
			System.out.println("here");
		}
	}

}
