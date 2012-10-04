package initial3d.engine.xhaust;

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
	final void setDrawID(int id) {
		drawid = id;
	}
	
	/* package-private */
	final int getDrawID() {
		return drawid;
	}
	
	/* package-private */
	void doRepaint(Graphics g) {
		
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
