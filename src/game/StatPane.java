package game;

import java.awt.Color;
import java.awt.Graphics;

import initial3d.engine.xhaust.Component;
import initial3d.engine.xhaust.Pane;

public class StatPane extends Pane {

	private Component hp = new Component(200, 30) {

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.RED);

			double h = Game.getInstance().getPlayer().getCurrentHealth()
					/ (double) Game.getInstance().getPlayer().getTotalHealth();

			g.fillRect((int) ((1 - h) * 200), 0, 200, 30);

			g.setColor(Color.WHITE);
			g.drawString("" + Game.getInstance().getPlayer().getCurrentHealth(), 160, 20);
		}

	};

	private Component eg = new Component(200, 30) {

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.GREEN);

			double h = Game.getInstance().getPlayer().getCurrentEnergy()
					/ (double) Game.getInstance().getPlayer().getTotalEnergy();

			g.fillRect((int) ((1 - h) * 200), 0, 200, 30);

			g.setColor(Color.WHITE);
			g.drawString("" + Game.getInstance().getPlayer().getCurrentEnergy(), 160, 20);
		}

	};

	private Component t = new Component(100, 30) {

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			long ms = Game.getInstance().getRemainingMs();
			long hs = (ms % 1000l) / 10;
			long s = ms / 1000;

			if (ms < 10000) {
				g.setColor(Color.RED);
			} else {
				g.setColor(Color.WHITE);
			}
			g.drawString(String.format("%d:%02d", s, hs), 5, 20);

		}

	};

	private long last_repaint = 0;

	public StatPane() {
		super(500, 30);
		getRoot().setOpaque(false);
		hp.setPosition(300, 0);
		hp.setOpaque(false);
		getRoot().add(hp);
		eg.setPosition(100, 0);
		eg.setOpaque(false);
		getRoot().add(eg);
		t.setPosition(0, 0);
		t.setOpaque(false);
		getRoot().add(t);
	}

	@Override
	protected void prepareDraw() {
		if (System.currentTimeMillis() - last_repaint > 100) {
			getRoot().repaint();
		}
	}

}
