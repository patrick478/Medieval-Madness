package initial3d.engine.xhaust;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

public class ButtonV0 extends Component {

	private String text = "";
	
	private boolean mousedown = false, mouseover = false;
	
	public ButtonV0(int width_, int height_, String text_) {
		super(width_, height_);
		text = text_;
		setBackgroundColor(Color.LIGHT_GRAY);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		g.drawString(text, 5, 15);
		if (mouseover) g.setColor(Color.CYAN);
		if (mousedown) g.setColor(Color.ORANGE);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
	}
	
	@Override
	protected void processMouseEvent(MouseEvent e) {
		switch(e.getID()) {
		case MouseEvent.MOUSE_PRESSED:
			mousedown = true;
			break;
		case MouseEvent.MOUSE_RELEASED:
			if (mousedown) {
				// fire action event
				// TODO how do i constructed action event?
				ActionEvent ae = new ActionEvent(this, 0, text);
				dispatchActionEvent(ae);
			}
			mousedown = false;
			break;
		case MouseEvent.MOUSE_ENTERED:
			mousedown = false;
			mouseover = true;
			break;
		case MouseEvent.MOUSE_EXITED:
			mousedown = false;
			mouseover = false;
		}
		repaint();
	}
	

}
