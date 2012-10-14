package game;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import initial3d.engine.xhaust.Container;
import initial3d.engine.xhaust.DialogPane;
import initial3d.engine.xhaust.Pane;
import initial3d.engine.xhaust.Picture;

public class InventorySelector extends Container {

	final int width = 400;
	final int height = 200;
	
	Picture bg;
	Picture pos0;
	Picture pos1;
	Picture pos2;
	Picture pos3;
	Picture pos4;
	Picture pos5;
	Picture pos6;
	Picture pos7;

	Picture blank;
	
	Picture[] allPos = new Picture[8];
	
	
	public InventorySelector(int width_, int height_) {
		super(width_, height_);
		
		try {
			BufferedImage background = ImageIO.read(new File("resources/inventory/inventoryselect.png"));
			BufferedImage blankBI = ImageIO.read(new File("resources/inventory/blank.png"));

			
			
			allPos[0] = pos0;
			allPos[1] = pos1;
			allPos[2] = pos2;
			allPos[3] = pos3;
			allPos[4] = pos4;
			allPos[5] = pos5;
			allPos[6] = pos6;
			allPos[7] = pos7;

			bg = new Picture(background, 0, 0, width, height);
			bg.setOpaque(false);
			add(bg);
			
			
			int x = 50;
			int y = 60;
			
			for(Picture p: allPos){
				p = new Picture(blankBI, 0, 0, 50, 50);;
				p.setPosition(x, y);
				p.setOpaque(false);
				add(p);
				x+=50;
				if(x>200){
					x = 50;
					y +=50;
				}
				
			}
			
			
			
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			
		
		// TODO Auto-generated constructor stub
	}
	
	
	
	
	

}
