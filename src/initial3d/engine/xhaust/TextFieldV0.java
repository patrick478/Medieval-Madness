package initial3d.engine.xhaust;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.Timer;

public class TextFieldV0 extends Component {

	private String text = "";
	private boolean carrot = true;
	private long lastSwitch = System.currentTimeMillis();
	int carrotPos = 0;
	
	private ActionListener l = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			TextFieldV0.this.repaint();
		}
		
	};
	
	private Timer carrotTimer = new Timer(500, l);
			
	public TextFieldV0(int width_, int height_) {
		super(width_, height_);
		
		this.carrotTimer.start();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		
		if(System.currentTimeMillis() - this.lastSwitch > 500)
		{
			this.carrot = !this.carrot;
			this.lastSwitch = System.currentTimeMillis();
		}
		
		int width = g.getFontMetrics().stringWidth(text);
		int precarotPx = g.getFontMetrics().stringWidth(text.substring(0, carrotPos));
		int visible = width - this.getWidth();
		if(visible < 0) visible = 0;
		if(precarotPx > this.getWidth()) precarotPx = this.getWidth();
		
		g.drawString(text, 1 - visible, 15);
		g.setColor(Color.WHITE);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
		if(this.carrot)
		{
			g.setColor(new Color(120, 120, 120));		
			g.drawRect(precarotPx + 1, 2, 1, getHeight() - 5);
		}
		
		this.carrotTimer.restart();
	}
	
	@Override
	public void processKeyEvent(KeyEvent e)
	{
		if(e.getID() == KeyEvent.KEY_RELEASED)
		{			
			switch(e.getKeyCode())
			{
				case KeyEvent.VK_BACK_SPACE:
				if(text.length() > 0)
					text = text.substring(0, text.length() - 1);
				break;
				
				case KeyEvent.VK_LEFT:
					if(this.carrotPos > 0)
						this.carrotPos--;
				break;
				
				case KeyEvent.VK_RIGHT:
					if(this.carrotPos < this.text.length())
						this.carrotPos++;
				break;
				
				default:
					text = text.substring(0, this.carrotPos) + e.getKeyChar() + text.substring(this.carrotPos, text.length());
					carrotPos++;
				break;
			}
		}
		
		
		this.repaint();
	}
}
