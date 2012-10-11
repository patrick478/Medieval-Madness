package initial3d.engine.xhaust;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class Picture extends Component{
	private Boolean isVisible = true;
	BufferedImage img;

	
	public Picture(BufferedImage i, int x, int y, int width_, int height_) {
		super(width_, height_);
		img = i;
		this.setPosition(x, y);
		// TODO Auto-generated constructor stub
	}

	public void setVisible(){
		isVisible = true;
	}

	public void setHidden(){
		isVisible = false;
	}

	public void setPicture(BufferedImage img){
		this.img = img;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		if(isVisible)
			g.drawImage(img, 0, 0, null);
	}

}


