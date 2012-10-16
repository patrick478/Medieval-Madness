package game;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import game.level.Space;
import java.awt.Color;
import initial3d.engine.Vec3;
import initial3d.engine.xhaust.Component;
import initial3d.engine.xhaust.Pane;

/**
 * Drawable Pane for displaying the minimap.
 * 
 * @author Ben Allen
 */
public class MapPane extends Pane {

	private final BufferedImage bi;

	private int scale = 32;

	private final Color c;
	
	private long last_repaint = 0;

	private final Component map = new Component(150, 150) {

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Vec3 pos = Game.getInstance().getPlayer().getDrawPosition();
			
			// pos offset of 0.5 is to align to mesh
			// pixel offset of 0.5 is for rounding
			int img_x = (int) (scale * (pos.x + 0.5) + 0.5);
			int img_y = (int) (scale * (pos.z + 0.5) + 0.5);

			g.drawImage(bi, getWidth() / 2 - img_x, getHeight() / 2 - img_y, bi.getWidth() * scale, bi.getHeight()
					* scale, null);

			g.setColor(c);
			g.fillOval(getWidth() / 2 - 5, getHeight() / 2 - 5, 10, 10);

			last_repaint = System.currentTimeMillis();
		}

	};

	public MapPane() {
		super(150, 150);

		Space[][] floor = Game.getInstance().getLevel().getFloor().getData();

		bi = new BufferedImage(floor.length, floor[0].length, BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < floor.length; x++) {
			for (int z = 0; z < floor[0].length; z++) {

				if (floor[x][z].type == Space.WALL) {
					bi.setRGB(x, z, 0xFF8080AF);
				} else {
					bi.setRGB(x, z, 0x00000000);
				}

			}
		}

		initial3d.engine.Color i3dc = Game.getInstance().getPlayer().getColor();

		c = new Color(i3dc.r, i3dc.g, i3dc.b);

		getRoot().setOpaque(false);
		getRoot().add(map);
		map.setOpaque(false);
	}

	@Override
	protected void prepareDraw() {
		Vec3 head = Game.getInstance().getState().scene.getCamera().getNormal().flattenY().unit();
		double r = head.inc(Vec3.k) * Math.signum(head.dot(Vec3.i)) + Math.PI;
		setRotation(r);
		if (System.currentTimeMillis() - last_repaint > 100) {
			map.repaint();
		}
	}
	
	public int getScale() {
		return scale;
	}
	
	public void setScale(int scale_) {
		scale = scale_;
	}
	
	public void incScale() {
		scale *= 2;
	}
	
	public void decScale() {
		scale = scale <= 2 ? 1 : scale / 2;
	}

}
