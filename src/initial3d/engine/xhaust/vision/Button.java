package initial3d.engine.xhaust.vision;

import initial3d.engine.xhaust.Component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;


public class Button extends Component {

	private static final int defaultHeight = 24;
	private static final int defaultWidth = 90;
	
	private String text = "";
	
	private boolean hovering = false;
	private boolean pressing = false;
	
	private Color defaultColor = new Color(102, 102, 102);
	private Color hoverColor = new Color(112, 112, 112);
	
	private int textOffset = -1;
	
	public Button(String _text) {
		super(defaultWidth, defaultHeight);
		this.text = _text;
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		if(!this.hovering)
			g.setColor(this.defaultColor);
		else
			g.setColor(this.hoverColor);
		g.fill3DRect(0, 0, this.getWidth(), this.getHeight(), !this.pressing);
		g.setColor(new Color(220, 220, 220));
		
		if(textOffset < 0) calculateTextOffset(g);
		g.drawString(text, textOffset, 16);
	}
	
	private void calculateTextOffset(Graphics g)
	{
		this.textOffset = (this.getWidth() / 2) - (g.getFontMetrics().stringWidth(text) / 2);
	}
	
	protected void processMouseEvent(MouseEvent e) {
		switch(e.getID()) {
		case MouseEvent.MOUSE_PRESSED:
			this.pressing = true;
			break;
		case MouseEvent.MOUSE_RELEASED:
			this.pressing = false;
			dispatchActionEvent(new ActionEvent(this, ActionIDList.CLICKED, text));
			break;
		case MouseEvent.MOUSE_ENTERED:
			this.hovering = true;
			this.pressing = false;
			dispatchActionEvent(new ActionEvent(this, ActionIDList.HOVER_ENTER, text));
			break;
		case MouseEvent.MOUSE_EXITED:
			this.hovering = false;
			this.pressing = false;
			dispatchActionEvent(new ActionEvent(this, ActionIDList.HOVER_LEAVE, text));
			break;
		}
		repaint();
	}
}
