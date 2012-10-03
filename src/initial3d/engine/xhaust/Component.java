package initial3d.engine.xhaust;

import java.awt.Graphics;

public class Component {
	
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

}
