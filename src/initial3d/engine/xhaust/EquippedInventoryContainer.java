package initial3d.engine.xhaust;

import game.entity.moveable.PlayerEntity;
import game.item.Item;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class EquippedInventoryContainer extends Container {

	
	BufferedImage blank;
	BufferedImage selected;

	PlayerEntity player;
	private int selectedPos = 0;
	
	
	//TODO: add fields for different items
	public EquippedInventoryContainer(PlayerEntity playerEntity) {
		super(250, 50);

		player = playerEntity;
		try {
			blank = ImageIO.read(new File("resources/inventory/blank.png"));
			selected = ImageIO.read(new File("resources/inventory/selected.png"));

			//TODO: Read in all different items
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setOpaque(false);


	}

	

	@Override
	protected void paintComponent(Graphics g) {
		int x = 0;
		for(Item item : player.getEquippedItems()){
			if(item==null)
				g.drawImage(blank, x, 0, null);

			else 
				g.drawImage(item.getIcon(), x, 0, null);


		
			x+=50;


		}
		g.drawImage(selected, selectedPos*50, 0, null);


	}

	
	public void setSelectedPos(int i){
		this.selectedPos = i;
		repaint();
	}
}


