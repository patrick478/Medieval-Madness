package initial3d.engine.xhaust.vision;

import java.awt.Color;
import java.awt.Graphics;

import initial3d.engine.xhaust.Component;

public class Label extends Component {
	private static final int defaultHeight = 24;
	private static final int defaultWidth = 120;
	private static final int margin = 8;
	
	private String text = "";
	
	private Color defaultColor = new Color(115, 115, 115);
	
	public Label(int width_, int height_) {
		super(width_, height_);
	}

	public Label(String string) {
		super(defaultWidth, defaultHeight);
		this.text = string;
	}
	
	public Label(int i) {
		super(i, defaultHeight);
	}
	
	public void setText(String text){
		this.text = text;
	}
	
	
	
	@Override
	protected void paintComponent(Graphics g)
	{
		g.setColor(defaultColor);
		g.fillRect(0,  0,  this.getWidth(),  this.getHeight());
		g.setColor(new Color(230, 230, 230));
		g.drawString(text, margin,  16);
	}
}
