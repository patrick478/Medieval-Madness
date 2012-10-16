package game.states;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import initial3d.engine.xhaust.Pane;
import initial3d.engine.xhaust.Picture;
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
		Game.getInstance().getWindow().setMouseCapture(false);
		Game.getInstance().getWindow().setCursorVisible(true);
		Game.getInstance().getWindow().setCrosshairVisible(false);
		
		final Pane p = new Pane(800, 600);
		BufferedImage bg;
		try {
			bg = ImageIO.read(new File("resources/lobbypage.png"));	
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		Picture pic = new Picture(bg, 0, 0, 800, 600);

		p.getRoot().add(pic); 
		statusLabel = new Label("Waiting for ready!");
		readyButton = new Button("Ready");		
		Label nextLevelLabel = new Label(String.format("Level %d - %dms remaining\n", Game.getInstance().getCurrentLevelNumber(), Game.getInstance().getRemainingMs()));
		nextLevelLabel.setPosition(100, 180);
		p.getRoot().add(nextLevelLabel);
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
		statusLabel.setPosition(100, 80);
		readyButton.setPosition(100, 110);
		p.getRoot().add(readyButton);		
		p.getRoot().add(statusLabel);
		
		int index = 1;
		for(PlayerEntity pe : Game.getInstance().getPlayers())
		{
			Label nLabel = new Label(String.format("Player %d: %s\n", index, pe.getPregameReadyState() ? "Ready!" : "Not ready. :("));
			nLabel.setPosition(500, 55 + (index * 30));
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
