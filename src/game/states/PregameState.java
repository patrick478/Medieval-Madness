package game.states;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import initial3d.engine.xhaust.Pane;
import initial3d.engine.xhaust.vision.ActionIDList;
import initial3d.engine.xhaust.vision.Button;
import initial3d.engine.xhaust.vision.Label;
import game.Game;
import game.GameState;
import game.entity.moveable.PlayerEntity;

/*** 
 * The state before and after each of the levels
 *
 * @author Ben
 *
 */
public class PregameState extends GameState {
	Label statusLabel = null;
	Button readyButton = null;
	Label[] readyLabels = new Label[4];
	
	public PregameState() {
	}

	@Override
	public void initalise() {
		final Pane p = new Pane(600, 400);
		statusLabel = new Label("Waiting for ready!");
		readyButton = new Button("Ready");		
		readyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getID() != ActionIDList.CLICKED)
					return;
				
				if(!Game.getInstance().isPregameReady())
				{
					Game.getInstance().setSelfPregameReady(true);
					statusLabel.setText("Waiting for other players..");
					readyButton.setPosition(-1000, -1000); // don't hate me
					p.getRoot().repaint();
				}
			}
		});
		statusLabel.setPosition(0, 0);
		readyButton.setPosition(0, 40);
		p.getRoot().add(readyButton);		
		p.getRoot().add(statusLabel);
		
		int index = 1;
		for(PlayerEntity pe : Game.getInstance().getPlayers())
		{
			Label nLabel = new Label(String.format("Player %d: %s\n", index, pe.getPregameReadyState() ? "Ready!" : "Not ready. :("));
			nLabel.setPosition(200, (index * 30) - 30);
			p.getRoot().add(nLabel);
			readyLabels[index-1] = nLabel;
			index++;
		}
		
		scene.addDrawable(p);

		p.requestVisible(true);
		p.requestFocus();
	}

	@Override
	public void update(double delta) {
	}

	@Override
	public void destroy() {
	}

	public void updatePregameScreen() {
		for(int i = 0; i < Game.getInstance().getMaxPlayers(); i++)
		{
			readyLabels[i].setText(String.format("Player %d: %s\n", i+1, Game.getInstance().getPlayers()[i].getPregameReadyState() ? "Ready!" : "Not ready. :("));
		}
	}

	public void setGameStarting() {
		statusLabel.setText("Game starting..\n");
	}

}
