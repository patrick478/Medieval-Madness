package initial3d.engine.xhaust;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

public class MouseArea extends Component{

	private boolean mousedown;
	private boolean mouseover;

	public MouseArea(int x, int y, int width_, int height_) {
		super(width_, height_);
		this.setPosition(x, y);
		this.setOpaque(true);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		
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
				ActionEvent ae = new ActionEvent(this, 0, "released");
				dispatchActionEvent(ae);
			}
			mousedown = false;
			break;
		case MouseEvent.MOUSE_ENTERED:
			mousedown = false;
			mouseover = true;
			ActionEvent ae = new ActionEvent(this, 0, "mouseover");
			dispatchActionEvent(ae);
			break;
		case MouseEvent.MOUSE_EXITED:
			mousedown = false;
			mouseover = false;
		}
		repaint();
	}

}
