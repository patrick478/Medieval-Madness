package initial3d.engine.xhaust;

import java.awt.Graphics;

public class Component {
	
	private final int width, height;
	private int x, y;
	
	public Component(int width_, int height_) {
		width = width_;
		height = height_;
		
	}
	
	public void paint(Graphics g) {
		paintComponent(g);
		paintChildren(g);
		paintBorder(g);
	}
	
	protected void paintComponent(Graphics g) {
		
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

}
