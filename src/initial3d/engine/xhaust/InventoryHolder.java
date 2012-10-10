package initial3d.engine.xhaust;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class InventoryHolder extends Component{

	Picture first ;
	Picture second ;
	Picture third ;
	Picture fourth ;
	Picture fifth ;

	BufferedImage blank;
	//TODO: add fields for different items
	public InventoryHolder(int width_, int height_) {
		super(width_, height_);
		try {
			blank = ImageIO.read(new File("resources/inventory/blank.png"));
			//TODO: Read in all different items
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated constructor stub
		first = new Picture(blank, 0, 0, 50, 50);
		second = new Picture(blank, 0, 50, 50, 50);
		third = new Picture(blank, 0, 100, 50, 50);
		fourth = new Picture(blank, 0, 150, 50, 50);
		fifth = new Picture(blank, 0, 200, 50, 50);

	}

	public void setItem(int inventoryPosition, String item){
		//TODO: Add cases for the different items to be added
	}
	

	
	
	@Override
	protected void paintComponent(Graphics g) {
			first.repaint();
			second.repaint();
			third.repaint();
			fourth.repaint();
			fifth.repaint();
	}
	
	
}
