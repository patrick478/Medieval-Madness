package initial3d.engine.xhaust;

import java.awt.Graphics;

public class LabelV0 extends Component {

	private String text = "";
	
	public LabelV0(int width_, int height_, String text_) {
		super(width_, height_);
		text = text_;
	}
	
	@Override
	protected void paintComponent(Graphics g) {		
		g.setColor(this.getBackgroundColor());
		g.fillRoundRect(2, 2, this.getWidth()-4, this.getHeight()-4, 4, 4);
		
		g.setColor(this.getForegroundColor());
		int baseline = (this.getHeight() / 2) + (g.getFontMetrics().getHeight() / 2) - 2;
		g.drawString(text, 6, baseline);
	}
}
