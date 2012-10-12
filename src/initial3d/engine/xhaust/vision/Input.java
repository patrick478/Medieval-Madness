package initial3d.engine.xhaust.vision;

import initial3d.engine.xhaust.Component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;


public class Input extends Component {

	private static final int defaultHeight = 24;
	private static final int defaultWidth = 60;
	private static final int margin = 8;
	
	private String[] text = new String[2];
	
	private boolean hovering = false;
	private boolean isNumericInputbox = false;
	
	private Color defaultColor = new Color(102, 102, 102);
	private Color hoverColor = new Color(112, 112, 112);
	
	public Input() {
		super(defaultWidth, defaultHeight);
		text[0] = "";
		text[1] = "";
	}
	
	public Input(int width) {
		super(width, defaultHeight);
		text[0] = "";
		text[1] = "";
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		if(!this.hovering)
			g.setColor(this.defaultColor);
		else
			g.setColor(this.hoverColor);
		g.fill3DRect(0, 0, this.getWidth(), this.getHeight(), false);
		g.setColor(new Color(220, 220, 220));
		
		int curX = g.getFontMetrics().stringWidth(text[0]) + 8;
		int textOffset = 8;
		if(curX > this.getWidth() - margin) {
			textOffset -= curX - (this.getWidth() - margin);
			curX = this.getWidth() - margin;
		}
//		if(textOffset > margin) textOffset = margin;
		g.drawString(text[0], textOffset, 16);
		g.drawRect(curX, 4, 0, 16);
		g.drawString(text[1], curX, 16);
	}
	
	@Override
	protected void processMouseEvent(MouseEvent e) {
		switch(e.getID()) {
		case MouseEvent.MOUSE_ENTERED:
			this.hovering = true;
			break;
		case MouseEvent.MOUSE_EXITED:
			this.hovering = false;
			break;
		}
		repaint();
	}
	
	@Override
	protected void processKeyEvent(KeyEvent e)
	{
		if(e.getID() == KeyEvent.KEY_RELEASED)
		{
			
			switch(e.getKeyCode())
			{
				case KeyEvent.VK_BACK_SPACE:
					if(this.text[0].length() > 0)
						this.text[0] = this.text[0].substring(0, this.text[0].length() - 1);
				break;
				case KeyEvent.VK_LEFT:
					if(this.text[0].length() <= 0) break;
					this.text[1] = this.text[0].substring(this.text[0].length()-1) + text[1];
					this.text[0] = this.text[0].substring(0, this.text[0].length()-1); 
				break;
				case KeyEvent.VK_RIGHT:
					if(this.text[1].length() <= 0) break;
					this.text[0] = text[0] + this.text[1].substring(0, 1);
					this.text[1] = this.text[1].substring(1, this.text[1].length()); 
				break;
				case KeyEvent.VK_ENTER:
					dispatchActionEvent(new ActionEvent(this, ActionIDList.SUBMITTED, text[0] + text[1]));
					break;
				default:
					this.text[0] += (this.isNumericInputbox && isNumber(e.getKeyChar()) ? e.getKeyChar() : "");
				break;
			}
		}
		
		this.repaint();
	}

	public void setNumericOnly(boolean b) {
		this.isNumericInputbox = b;
	}
	
	public boolean isNumber(char c)
	{
		try {
			Integer.parseInt(c + "");
		} catch(NumberFormatException nfe) { return false; }
		return true;
	}

	public String getText() {
		return this.text[0] + text[1];
	}
}
