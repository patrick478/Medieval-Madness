package game;

import game.entity.moveable.PlayerEntity;
import game.item.Item;
import game.item.ItemContainer;
import game.states.CreateGameState;
import game.states.FindGameState;
import initial3d.engine.xhaust.Container;
import initial3d.engine.xhaust.MouseArea;
import initial3d.engine.xhaust.Picture;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class InventorySelector extends Container {

	final int width = 400;
	final int height = 200;
	ItemContainer inventory;
	Item[] equipped;
	Picture bg;

	Picture blank;

	Picture[] allPos = new Picture[8];
	BufferedImage background;
	BufferedImage blankBI;

	private int posToAdd=0;

	static MouseArea pos0 = new MouseArea(50, 60, 50, 50);
	static MouseArea pos1 = new MouseArea(100, 60, 50, 50);
	static MouseArea pos2 = new MouseArea(150, 60, 50, 50);
	static MouseArea pos3 = new MouseArea(200, 60, 50, 50);
	static MouseArea pos4 = new MouseArea(50, 110, 50, 50);
	static MouseArea pos5 = new MouseArea(50, 160, 50, 50);
	static MouseArea pos6 = new MouseArea(50, 210, 50, 50);
	static MouseArea pos7 = new MouseArea(50, 260, 50, 50);

	public InventorySelector(int width_, int height_, PlayerEntity player) {
		super(width_, height_);
		addActionListener(l);
		this.inventory = player.getInventory();
		equipped = player.getEquippedItems();
		try {
			background = ImageIO.read(new File("resources/inventory/inventoryselect.png"));
			blankBI = ImageIO.read(new File("resources/inventory/blank.png"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




		// TODO Auto-generated constructor stub
	}

	private final ActionListener l = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("released")){
				System.out.println("Action");
				if (e.getSource()==pos0){
					equipped[posToAdd] = inventory.getItem(0);
					inventory.removeItem(inventory.getItem(0));
					System.out.println("Equipping 0");
					if (e.getSource()==pos1)
						equipped[posToAdd] = inventory.getItem(1);
					if (e.getSource()==pos2)
						equipped[posToAdd] = inventory.getItem(2);
					if (e.getSource()==pos3)
						equipped[posToAdd] = inventory.getItem(3);
					if (e.getSource()==pos4)
						equipped[posToAdd] = inventory.getItem(4);
					if (e.getSource()==pos5)
						equipped[posToAdd] = inventory.getItem(5);
					if (e.getSource()==pos6)
						equipped[posToAdd] = inventory.getItem(6);
					if (e.getSource()==pos7)
						equipped[posToAdd] = inventory.getItem(7);
				}
			}
		};
	};







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