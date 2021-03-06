package initial3d.test;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import initial3d.engine.*;
import initial3d.engine.xhaust.ButtonV0;
import initial3d.engine.xhaust.Component;
import initial3d.engine.xhaust.DialogPane;
import initial3d.engine.xhaust.Pane;
import initial3d.engine.xhaust.TextFieldV0;
import initial3d.engine.xhaust.vision.Button;
import initial3d.engine.xhaust.vision.Input;
import initial3d.engine.xhaust.vision.Label;

public class TestUI {

	public static void main(String[] args) throws Throwable {

		RenderWindow rwin = RenderWindow.create(848, 480);
		rwin.setLocationRelativeTo(null);
		rwin.setVisible(true);

		SceneManager sman = new SceneManager(848, 480);
		sman.getProfiler().setResetOutput(null);
		sman.setDisplayTarget(rwin);
		rwin.addKeyListener(sman);
		rwin.addCanvasMouseListener(sman);
		rwin.addCanvasMouseMotionListener(sman);
		rwin.addMouseWheelListener(sman);

		Scene scene = new Scene();
		sman.attachToScene(scene);

		// stuff

		final Pane p = new Pane(250, 250);
		p.setPosition(128, 30);
		p.getRoot().setBackgroundColor(Color.GRAY);
		
		final Component newButton = new Button("Hello");		
		final Component newLabel = new Label("This is a label");
		final Component newInput = new Input();

		final Component c = new ButtonV0(40, 20, "Hello");
		final Component ct = new TextFieldV0(120, 20);
		final Component c1 = new Component(40, 20) {
			String s = "";

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.BLACK);
				g.drawString(s, 5, 15);
				g.setColor(Color.CYAN);
				g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			}

			@Override
			protected void processKeyEvent(KeyEvent e) {
				if (e.getID() == KeyEvent.KEY_PRESSED) {
					s += e.getKeyChar();
					repaint();
				}
			}

		};
		final Component c2 = new Component(50, 20) {
			String s = "";

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(Color.BLACK);
				g.drawString(s, 5, 15);
				g.setColor(Color.CYAN);
				g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
			}

			@Override
			protected void processKeyEvent(KeyEvent e) {
				if (e.getID() == KeyEvent.KEY_PRESSED) {
					s += e.getKeyChar();
					repaint();
				}
			}
			
		};
		
		c.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == c) {
					c1.setBackgroundColor(Color.RED);
					c1.repaint();
					c2.setBackgroundColor(Color.GREEN);
					c2.repaint();
				}
			}
			
		});
		
		c.setPosition(10, 10);
		c1.setPosition(20, 40);
		c2.setPosition(30, 70);
		ct.setPosition(20, 80);
		
		newButton.setPosition(20, 40);
		newLabel.setPosition(20, 80);
		newInput.setPosition(20, 120);

//		p.getRoot().add(c);
//		p.getRoot().add(c1);
//		p.getRoot().add(c2);
//		p.getRoot().add(ct);
		
		p.getRoot().add(newButton);
		p.getRoot().add(newLabel);
		p.getRoot().add(newInput);
		
		p.setRotation(Math.PI / 4);

		p.requestVisible(true);

		scene.addDrawable(p);
		
		Pane last = p;
		
		for (int i = 0; i < 5; i++) {
			DialogPane dp = new TestDialogPane(200, 200, last, true);
			dp.setPosition(-20 * i, -20 * i);
			dp.requestVisible(true);
			scene.addDrawable(dp);
			last = dp;
		}
		
		

		MovableReferenceFrame camera_rf = new MovableReferenceFrame(ReferenceFrame.SCENE_ROOT);
		scene.getCamera().trackReferenceFrame(camera_rf);
		camera_rf.setPosition(Vec3.create(-3, 3, -3));
		camera_rf.setOrientation(Quat.create(Math.PI / 16, Vec3.i));

		camera_rf.setOrientation(camera_rf.getOrientation().mul(Quat.create(Math.PI / 4, Vec3.j)));
		
//		while (true) {
//			p.setRotation(p.getRotation() + Math.PI / 60);
//			Thread.sleep(33);
//		}

	}
	
	private static class TestDialogPane extends DialogPane {

		public TestDialogPane(int width_, int height_, Pane parent_, boolean modal_) {
			super(width_, height_, parent_, modal_);
			
			final Component b = new ButtonV0(40, 20, "DIE");
			getRoot().add(b);
			
			b.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					TestDialogPane.this.requestRemoval();
				}
				
			});
		}
		
	}

}
