package game.states;

import initial3d.engine.xhaust.MouseArea;
import initial3d.engine.xhaust.Pane;
import initial3d.engine.xhaust.Picture;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import game.Game;
import game.GameState;

/***
 * This is the main menu state, on steriods. It will have a GUI
 * 
 * From this state, the user chooses to create a game, or join an existing game. It should switch to the corrosponding state.
 * @author Ben and Patrick
 *
 */
public class MainMenuGUIState extends GameState {

	static MouseArea joinArea = new MouseArea(267, 345, 260, 35);
	static MouseArea startArea = new MouseArea(235, 407, 571-135, 35);
	Picture pic;
	BufferedImage mainmenu = null;
	BufferedImage joinmenu = null;
	BufferedImage startmenu = null;


	private final ActionListener l = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("released")){
				if(e.getSource().equals(joinArea))
					game.changeState(new FindGameState(game));
				else if(e.getSource().equals(startArea))
					game.changeState(new CreateGameState(game));

			}
			else if(e.getActionCommand().equals("mouseover")){
				if(e.getSource().equals(joinArea)){
					pic.setPicture(joinmenu);
				}
					

				else if(e.getSource().equals(startArea)){
					pic.setPicture(startmenu);

				}
					

			}
		}
	};

	public MainMenuGUIState(Game parent) {
		super(parent);
	}

	@Override
	public void initalise() {

		Pane p = new Pane(800, 600);

		//stuff
		


		try {
			mainmenu = ImageIO.read(new File("resources/mainmenu.png"));
			joinmenu = ImageIO.read(new File("resources/mainmenujoin.png"));
			startmenu = ImageIO.read(new File("resources/mainmenustart.png"));


		} catch (IOException e) {
			throw new AssertionError(e);
		}
		pic = new Picture(mainmenu, 800,600);
		p.getRoot().add(pic);
		//input from user

		joinArea.addActionListener(l);
		p.getRoot().add(joinArea);
		startArea.addActionListener(l);
		p.getRoot().add(startArea);

		scene.addDrawable(p);

		p.requestVisible(true);


	}



	@Override
	public void update(double delta) {
	}

	@Override
	public void destroy() {
	}	
}
