package game.states;

import initial3d.engine.xhaust.Pane;
import initial3d.engine.xhaust.Picture;
import initial3d.engine.xhaust.TextFieldV0;
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
 * This state will display a list of games to join and allow a user to join a game
 * @author Ben + Patrick
 *
 */


public class FindGameState extends GameState {
	
	Label serverOne = new Label(300);
	Label serverTwo = new Label(300);
	Label serverThree = new Label(300);
	Label serverFour = new Label(300);

	Label playersOne = new Label(50);
	Label playersTwo = new Label(50);
	Label playersThree = new Label(50);
	Label playersFour = new Label(50);
	
	public FindGameState() {
	}
	
	private Input target;
	private Picture pic;

	@Override
	public void initalise() {
		BufferedImage bg;

		
		Pane p = new Pane(800, 600);
		try {
			bg = ImageIO.read(new File("resources/serverpage.png"));	
		} catch (IOException e) {
			throw new AssertionError(e);
		}
		target = new Input(200);
		target.setNumericOnly(false);
		target.setPosition(90, 460);
		target.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Game.getInstance().setNetwork(new NetworkingClient());
				Game.getInstance().getNetwork().setRemoteTarget(target.getText());
				Game.getInstance().getNetwork().start();
			}
		});
		
		Button joinGameButton = new Button("Join Game");
		joinGameButton.setPosition(300, 460);
		joinGameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getID() != ActionIDList.CLICKED) return;
				Game.getInstance().setNetwork(new NetworkingClient());
				Game.getInstance().getNetwork().setRemoteTarget(target.getText());
				Game.getInstance().getNetwork().start();
			}
		});
		
		Button mainMenu = new Button("Return to main menu", 125);
		mainMenu.setPosition(p.getRoot().getWidth()-mainMenu.getWidth()-10, p.getRoot().getHeight()-mainMenu.getHeight()-10);
		mainMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getID() != ActionIDList.CLICKED) return;
				Game.getInstance().revertState();
			}
		});
		
		
		joinGameButton.setPosition(300, 460);
		joinGameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getID() != ActionIDList.CLICKED) return;
				Game.getInstance().setNetwork(new NetworkingClient());
				Game.getInstance().getNetwork().setRemoteTarget(target.getText());
				Game.getInstance().getNetwork().start();
			}
		});
		
		
		
		//Code for the sever select part of the screen
		
		serverOne.setPosition(90, 200);
		serverTwo.setPosition(90, 226);
		serverThree.setPosition(90, 252);
		serverFour.setPosition(90, 278);

		playersOne.setPosition(400, 200);
		playersTwo.setPosition(400, 226);
		playersThree.setPosition(400, 252);
		playersFour.setPosition(400, 278);

		
		
		
		pic = new Picture(bg ,0,0, 800,600);
		
		
		p.getRoot().add(pic);
		p.getRoot().add(target);
		p.getRoot().add(joinGameButton);
		p.getRoot().add(mainMenu);
		p.getRoot().add(serverOne);
		p.getRoot().add(serverTwo);
		p.getRoot().add(serverThree);
		p.getRoot().add(serverFour);
		
		p.getRoot().add(playersOne);
		p.getRoot().add(playersTwo);
		p.getRoot().add(playersThree);
		p.getRoot().add(playersFour);

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
	
	public void setServerInfo(int position, String serverName, int numPlayers){
		switch(position){
			case 1: serverOne.setText(serverName);
					playersOne.setText(String.valueOf(numPlayers));
			case 2: serverTwo.setText(serverName);
					playersTwo.setText(String.valueOf(numPlayers));
			case 3: serverThree.setText(serverName);
					playersThree.setText(String.valueOf(numPlayers));
			case 4: serverFour.setText(serverName);
					playersFour.setText(String.valueOf(numPlayers));
			default: throw new IndexOutOfBoundsException();
	}
	}
	
}
