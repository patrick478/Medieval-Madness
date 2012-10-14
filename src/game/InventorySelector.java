package game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import game.entity.moveable.PlayerEntity;
import game.item.Item;
import game.item.ItemContainer;
import game.modelloader.Content;
import initial3d.engine.xhaust.Container;
import initial3d.engine.xhaust.DialogPane;
import initial3d.engine.xhaust.Pane;
import initial3d.engine.xhaust.Picture;

public class InventorySelector extends Container {

	final int width = 400;
	final int height = 200;
	ItemContainer inventory;
	Picture bg;

	Picture blank;

	Picture[] allPos = new Picture[8];
	BufferedImage background;
	BufferedImage blankBI;

	public InventorySelector(int width_, int height_, ItemContainer inventory_) {
		super(width_, height_);

		this.inventory = inventory_;
		try {
			background = ImageIO.read(new File("resources/inventory/inventoryselect.png"));
			blankBI = ImageIO.read(new File("resources/inventory/blank.png"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




		// TODO Auto-generated constructor stub
	}

	@Override
	protected void paintComponent(Graphics g) {

		bg = new Picture(background, 0, 0, width, height);
		bg.setOpaque(false);
		add(bg);


		int x = 50;
		int y = 60;

		for(int i = 0; i<8; i++){
			Item item = inventory.getItem(i);
			if(item==null)
				allPos[i] = new Picture(blankBI, 0, 0, 50, 50);

			else 
				allPos[i] = new Picture(item.getIcon(), 0, 0, 50, 50);

			allPos[i].setPosition(x, y);
			allPos[i].setOpaque(false);
			add(allPos[i]);
			x+=50;
			if(x>200){
				x = 50;
				y +=50;
			}


		}




	}

	public void setItem(int i, BufferedImage image){
		System.out.println(allPos[i]);
		allPos[i].setPicture(image);
	}

}