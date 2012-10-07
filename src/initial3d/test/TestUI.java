//package initial3d.test;
//
//import java.awt.Color;
//import java.awt.Graphics;
//import java.awt.event.KeyEvent;
//import java.awt.event.KeyListener;
//import java.io.FileInputStream;
//
//import common.entity.MovableEntity;
//import common.entity.Player;
//import comp261.modelview.MeshLoader;
//
//import initial3d.engine.*;
//import initial3d.engine.xhaust.Component;
//import initial3d.engine.xhaust.Pane;
//
//public class TestUI {
//
//	public static void main(String[] args) throws Throwable {
//
//		RenderWindow rwin = RenderWindow.create(848, 480);
//		rwin.setLocationRelativeTo(null);
//		rwin.setVisible(true);
//
//		SceneManager sman = new SceneManager(848, 480);
//		sman.setDisplayTarget(rwin);
//		rwin.addKeyListener(sman);
//		rwin.addCanvasMouseListener(sman);
//		rwin.addCanvasMouseMotionListener(sman);
//		rwin.addMouseWheelListener(sman);
//
//		Scene scene = new Scene();
//		sman.attachToScene(scene);
//
//		// stuff
//
//		MovableEntity ball = new Player(Vec3.zero, 123123123);
//
//		FileInputStream fis = new FileInputStream("ball.txt");
//		ball.setMeshContexts(MeshLoader.loadComp261(fis));
//		fis.close();
//
//		MeshContext mc = ball.getMeshContexts().get(0);
//		mc.requestInputEnabled(true);
//		mc.requestFocus();
//
//		mc.addKeyListener(new KeyListener() {
//
//			@Override
//			public void keyPressed(KeyEvent e) {
//
//			}
//
//			@Override
//			public void keyReleased(KeyEvent e) {
//
//			}
//
//			@Override
//			public void keyTyped(KeyEvent e) {
//				System.out.println(e.getKeyChar());
//			}
//
//		});
//
//		scene.addDrawable(mc);
//
//		Pane p = new Pane(128, 128);
//		p.setPosition(128, 30);
//		p.getRoot().setBackgroundColor(Color.GRAY);
//
//		Component c = new Component(20, 20) {
//			String s = "";
//
//			@Override
//			protected void paintComponent(Graphics g) {
//				super.paintComponent(g);
//				g.setColor(Color.BLACK);
//				g.drawString(s, 5, 15);
//				g.setColor(Color.CYAN);
//				g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
//			}
//
//			@Override
//			protected void processKeyEvent(KeyEvent e) {
//				if (e.getID() == KeyEvent.KEY_PRESSED) {
//					s += e.getKeyChar();
//					repaint();
//				}
//			}
//
//		};
//		Component c1 = new Component(40, 20) {
//			String s = "";
//
//			@Override
//			protected void paintComponent(Graphics g) {
//				super.paintComponent(g);
//				g.setColor(Color.BLACK);
//				g.drawString(s, 5, 15);
//				g.setColor(Color.CYAN);
//				g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
//			}
//
//			@Override
//			protected void processKeyEvent(KeyEvent e) {
//				if (e.getID() == KeyEvent.KEY_PRESSED) {
//					s += e.getKeyChar();
//					repaint();
//				}
//			}
//
//		};
//		Component c2 = new Component(50, 20) {
//			String s = "";
//
//			@Override
//			protected void paintComponent(Graphics g) {
//				super.paintComponent(g);
//				g.setColor(Color.BLACK);
//				g.drawString(s, 5, 15);
//				g.setColor(Color.CYAN);
//				g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
//			}
//
//			@Override
//			protected void processKeyEvent(KeyEvent e) {
//				if (e.getID() == KeyEvent.KEY_PRESSED) {
//					s += e.getKeyChar();
//					repaint();
//				}
//			}
//
//		};
//		c.setPosition(10, 10);
//		c1.setPosition(20, 40);
//		c2.setPosition(30, 70);
//
//		p.getRoot().add(c);
//		p.getRoot().add(c1);
//		p.getRoot().add(c2);
//
//		p.requestVisible(true);
//
//		scene.addDrawable(p);
//
//		MovableReferenceFrame camera_rf = new MovableReferenceFrame(null);
//		scene.getCamera().trackReferenceFrame(camera_rf);
//		camera_rf.setPosition(Vec3.create(-3, 3, -3));
//		camera_rf.setOrientation(Quat.create(Math.PI / 16, Vec3.i));
//
//		camera_rf.setOrientation(camera_rf.getOrientation().mul(Quat.create(Math.PI / 4, Vec3.j)));
//
//		ball.updateMotion(Vec3.create(0, 0.5, 0), Vec3.create(0.5, 0, 0.2), Quat.one, Vec3.zero,
//				System.currentTimeMillis());
//
//	}
//
//}
