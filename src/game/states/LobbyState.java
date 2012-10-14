package game.states;

import initial3d.engine.xhaust.Pane;
import initial3d.engine.xhaust.vision.ActionIDList;
import initial3d.engine.xhaust.vision.Button;
import initial3d.engine.xhaust.vision.Label;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
		Pane p = new Pane(600, 400);
		statusLabel = new Label(String.format("Waiting for %d more players..", Game.getInstance().getMaxPlayers() - this.nPlayers));
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
		statusLabel.setPosition(0, 0);
		startButton.setPosition(0, 40);
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
