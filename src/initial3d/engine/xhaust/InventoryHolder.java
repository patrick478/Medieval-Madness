package initial3d.engine.xhaust;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class InventoryHolder extends Container {

	Picture first ;
	Picture second ;
	Picture third ;
	Picture fourth ;
	Picture fifth ;

	BufferedImage blank;
	//TODO: add fields for different items
	public InventoryHolder() {
		super(250, 50);
		try {
			blank = ImageIO.read(new File("resources/inventory/blank.png"));
			//TODO: Read in all different items
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated constructor stub
		first = new Picture(blank, 0, 0, 50, 50);
		second = new Picture(blank, 50, 0, 50, 50);
		third = new Picture(blank, 100, 0, 50, 50);
		fourth = new Picture(blank, 150, 0, 50, 50);
		fifth = new Picture(blank, 200, 0, 50, 50);
		
		add(first);
		add(second);
		add(third);
		add(fourth);
		add(fifth);


	}

	public void setItem(int inventoryPosition, String item){
		//TODO: Add cases for the different items to be added
	}
	


	
}
