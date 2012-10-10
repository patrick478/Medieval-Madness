package initial3d.engine.xhaust;

import java.awt.Graphics;
import java.awt.Image;

public class Picture extends Component{

	public Picture(Image i, int width_, int height_) {
		super(width_, height_);
		img = i;
		// TODO Auto-generated constructor stub
	}


	Image img;

	public void setPicture(Image img){
		this.img = img;
		repaint();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}

}


