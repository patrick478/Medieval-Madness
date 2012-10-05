package initial3d.engine.xhaust;

import static initial3d.Initial3D.POLYGON;
import initial3d.Initial3D;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Collections;

public abstract class Component {
	
	private final int width, height;
	private volatile int x, y;
	private volatile boolean opaque = true;
	private volatile Color col_bg = Color.WHITE;
	
	private volatile boolean repaint_required = true;
	private int drawid;
	
	public Component(int width_, int height_) {
		width = width_;
		height = height_;
		
	}
	
	/* package-private */
	final int getDrawID() {
		return drawid;
	}
	
	/* package-private */
	boolean doRepaint(Graphics g, Initial3D i3d, int id, double zview) {
		drawid = id;

		i3d.objectID(id);
		i3d.begin(POLYGON);
		
		i3d.vertex3d(0, -height, zview);
		i3d.vertex3d(-width, -height, zview);
		i3d.vertex3d(-width, 0, zview);
		i3d.vertex3d(0, 0, zview);
		
		i3d.end();
		
		if (repaint_required) {
			paint(g);
			return true;
		}
		return false;
	}
	
	/* package-private */
	Component findByID(int id) {
		if (drawid == id) {
			return this;
		}
		return null;
	}
	
	public Iterable<Component> getChildren() {
		return Collections.<Component>emptyList();
	}
	
	public int count() {
		return 1;
	}
	
	public void repaint() {
		repaint_required = true;
	}
	
	public boolean repaintRequired() {
		return repaint_required;
	}
	
	public void paint(Graphics g) {
		paintComponent(g);
		paintChildren(g);
		paintBorder(g);
	}
	
	protected void paintComponent(Graphics g) {
		if (opaque) {
			g.setColor(col_bg);
			g.fillRect(0, 0, width, height);
		}
	}
	
	protected void paintChildren(Graphics g) {
		
	}
	
	protected void paintBorder(Graphics g) {
		
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
	
	public void setPosition(int x_, int y_) {
		x = x_;
		y = y_;
	}
	
	public boolean getOpaque() {
		return opaque;
	}
	
	public void setOpaque(boolean b) {
		opaque = b;
	}
	
	public Color getBackgroundColor() {
		return col_bg;
	}
	
	public void setBackgroundColor(Color c) {
		col_bg = c;
	}

}
