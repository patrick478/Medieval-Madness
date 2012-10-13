package initial3d.engine.xhaust;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import initial3d.Initial3D;
import initial3d.Texture;
import initial3d.engine.Drawable;
import initial3d.engine.MeshContext;
import initial3d.engine.SceneManager;
import initial3d.linearmath.Matrix;
import initial3d.linearmath.TransformationMatrix4D;
import initial3d.linearmath.Vector4D;

import static initial3d.Initial3D.*;

/**
 * A Drawable (for Initial3D's SceneManager) that displays Xhaust GUI components.
 * 
 * @author Ben Allen
 */
public class Pane extends Drawable {

	private static final double NEAR_CLIP = (SceneManager.NEAR_PLANE + MeshContext.DEFAULT_NEAR_CLIP) / 2;
	private static final double FAR_CULL = MeshContext.DEFAULT_NEAR_CLIP;

	private volatile double rotation = 0;

	private final BufferedImage bi;
	private final Texture tex;
	private final double tc_umax, tc_vmax;

	private final int width, height;
	private int x, y;

	private final Container root;

	private final double[][] vec0 = new double[4][1];
	private final double[][] vec1 = new double[4][1];
	private final double[][] xtemp = Matrix.createIdentity(4);

	private Component focused;

	private final int zlevel;

	protected Pane(int width_, int height_, int zlevel_) {
		width = width_;
		height = height_;

		bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		tex = Initial3D.createTexture(Texture.requiredSize(Math.max(width, height)));

		tc_umax = width / (double) tex.size();
		tc_vmax = height / (double) tex.size();

		requestInputEnabled(true);
		requestVisible(false);

		root = new Container(width, height) {
			@Override
			public Pane getPane() {
				return Pane.this;
			}

			@Override
			public void remove() {
				throw new IllegalStateException("Root Container of a Pane cannot be removed.");
			}
		};

		focused = root;

		zlevel = zlevel_;
	}

	public Pane(int width_, int height_) {
		this(width_, height_, 1);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	/* package-private */
	final int getZLevel() {
		return zlevel;
	}

	public void setPosition(int x_, int y_) {
		x = x_;
		y = y_;
	}

	public double getRotation() {
		return rotation;
	}

	public void setRotation(double r) {
		rotation = r;
	}

	public Container getRoot() {
		return root;
	}

	@Override
	protected void draw(Initial3D i3d, int framewidth, int frameheight) {

		// TODO handle this properly
		i3d.nearClip(NEAR_CLIP);
		i3d.farCull(FAR_CULL);
		double zview = (NEAR_CLIP * zlevel + FAR_CULL) / (zlevel + 1);

		vec0[0][0] = 0;
		vec0[1][0] = 0;
		vec0[2][0] = zview;
		vec0[3][0] = 1;

		i3d.matrixMode(PROJ);
		i3d.transformOne(vec1, vec0);
		Vector4D.homogenise(vec1, vec1);

		vec1[0][0] = 1;
		vec1[1][0] = 1;

		i3d.matrixMode(PROJ_INV);
		i3d.transformOne(vec0, vec1);
		Vector4D.homogenise(vec0, vec0);

		// FIXME: What is the purpose of this? I commented it out temporarily.
		// double xleft = vec0[0][0];
		double ytop = vec0[1][0];

		i3d.disable(LIGHTING);

		i3d.matrixMode(VIEW);
		i3d.pushMatrix();
		i3d.loadIdentity();

		i3d.matrixMode(MODEL);
		i3d.pushMatrix();
		i3d.loadIdentity();

		double scale = ytop * 2d / (double) frameheight;
		TransformationMatrix4D.scale(xtemp, scale, scale, 1, 1);
		i3d.multMatrix(xtemp);

		i3d.translateX(x);
		i3d.translateY(y);

		i3d.rotateZ(rotation);

		i3d.translateX(width / 2);
		i3d.translateY(height / 2);

		i3d.disable(WRITE_COLOR | WRITE_Z);
		Graphics g = bi.createGraphics();

		if (!root.isOpaque() && root.repaintRequired()) {
			// try to transparent-clear
			Graphics2D g2d = (Graphics2D) g;
			Composite ac_clear = AlphaComposite.getInstance(AlphaComposite.CLEAR);
			Composite ac_old = g2d.getComposite();
			g2d.setComposite(ac_clear);
			g2d.setColor(Color.BLACK);
			g2d.fillRect(0, 0, width, height);
			g2d.setComposite(ac_old);
		}

		root.doRepaint(g, i3d, getDrawIDStart(), zview);
		g.dispose();

		if (root.repainted()) {
			// tex.drawImage(bi);
			tex.drawImage(0, 0, 0, 0, width, height, bi);
		}

		i3d.enable(WRITE_COLOR | WRITE_Z | TEXTURE_2D);
		i3d.disable(WRITE_ID);
		i3d.texImage2D(FRONT, tex, null, null);

		i3d.begin(POLYGON);
		i3d.texCoord2d(0, tc_vmax);
		i3d.vertex3d(0, -height, zview);
		i3d.texCoord2d(tc_umax, tc_vmax);
		i3d.vertex3d(-width, -height, zview);
		i3d.texCoord2d(tc_umax, 0);
		i3d.vertex3d(-width, 0, zview);
		i3d.texCoord2d(0, 0);
		i3d.vertex3d(0, 0, zview);
		i3d.end();

		i3d.disable(TEXTURE_2D);

		i3d.popMatrix();
		i3d.matrixMode(VIEW);
		i3d.popMatrix();

	}

	@Override
	protected int getRequestedIDCount() {
		return root.count();
	}

	@Override
	protected void processKeyEvent(KeyEvent e) {
		if (focused == null) {
			return;
		}
		focused.dispatchKeyEvent(e);
	}

	@Override
	protected void processMouseEvent(MouseEvent e, int drawid, int framex, int framey) {
		Component target = root.findByID(drawid);
		if (target == null) {
			// this shouldn't happen
			return;
		}

		if (e.getID() == MouseEvent.MOUSE_PRESSED) {
			// allow mouse down to switch local focus
			doFocusChange(target);
		}

		target.dispatchMouseEvent(e);
	}

	public void requestLocalFocusFor(Component c) {
		if (root.contains(c)) {
			doFocusChange(c);
		}
	}

	public Component getLocalFocused() {
		return focused;
	}
	
	private void doFocusChange(Component c) {
		focused.notifyLocalFocusLost();
		focused = c;
		focused.notifyLocalFocusGained();
	}

}
