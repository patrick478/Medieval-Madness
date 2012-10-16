package game;

import game.entity.moveable.PlayerEntity;
import game.item.Item;
import game.item.ItemContainer;
import game.states.CreateGameState;
import game.states.FindGameState;
import initial3d.engine.xhaust.Container;
import initial3d.engine.xhaust.EquippedInventoryContainer;
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
	private EquippedInventoryContainer invenCont;

	BufferedImage background;
	BufferedImage blankBI;

	private int selectedPos;

	MouseArea pos0 = new MouseArea(100, 60, 50, 50);
	MouseArea pos1 = new MouseArea(150, 60, 50, 50);
	MouseArea pos2 = new MouseArea(200, 60, 50, 50);
	MouseArea pos3 = new MouseArea(250, 60, 50, 50);
	MouseArea pos4 = new MouseArea(100, 110, 50, 50);
	MouseArea pos5 = new MouseArea(150, 110, 50, 50);
	MouseArea pos6 = new MouseArea(200,110,50,50);
	MouseArea pos7 = new MouseArea(250, 110, 50, 50);

	public InventorySelector(int width_, int height_, PlayerEntity player, EquippedInventoryContainer i, int selectedPos) {
		super(width_, height_);
		this.inventory = player.getInventory();
		equipped = player.getEquippedItems();
		this.invenCont = i;
		this.selectedPos = selectedPos;



		pos0.addActionListener(l);
		pos1.addActionListener(l);
		pos2.addActionListener(l);
		pos3.addActionListener(l);
		pos4.addActionListener(l);
		pos5.addActionListener(l);
		pos6.addActionListener(l);
		pos7.addActionListener(l);

		try {
			background = ImageIO.read(new File("resources/inventory/inventoryselect.png"));
			blankBI = ImageIO.read(new File("resources/inventory/blank.png"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		add(pos0);
		add(pos1);
		add(pos2);
		add(pos3);
		add(pos4);
		add(pos5);
		add(pos6);
		add(pos7);

		// TODO Auto-generated constructor stub
	}

	private final ActionListener l = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			if(e.getActionCommand().equals("mouseover")){
			}


			if(e.getActionCommand().equals("released")){
				if (e.getSource().equals(pos0)){
					for(Item item : equipped){
						if(inventory.getItem(0)==null) return;
						if(inventory.getItem(0).equals(item)) {
							System.out.println("Item already equipped");
							return;
						}
					}
					equipped[selectedPos] = inventory.getItem(0);
					System.out.println("Equipping 0 to "+ selectedPos);
					invenCont.repaint();
				}
				else if (e.getSource().equals(pos1)){
					for(Item item : equipped){
						if(inventory.getItem(1)==null) return;
						if(inventory.getItem(1).equals(item)) {
							System.out.println("Item already equipped");
							return;
						}
					}
					equipped[selectedPos] = inventory.getItem(1);
					System.out.println("Equipping 1");
					invenCont.repaint();	
				}
				else if (e.getSource().equals(pos2)){
					for(Item item : equipped){
						if(inventory.getItem(2)==null) return;
						if(inventory.getItem(2).equals(item)) {
							System.out.println("Item already equipped");
							return;
						}
					}
					equipped[selectedPos] = inventory.getItem(2);
					System.out.println("Equipping 2");
					invenCont.repaint();	
				}
				else if (e.getSource().equals(pos3)){
					
					for(Item item : equipped){
						if(inventory.getItem(3)==null) return;
						if(inventory.getItem(3).equals(item)) {
							System.out.println("Item already equipped");
							return;
						}
					}
					equipped[selectedPos] = inventory.getItem(3);
					System.out.println("Equipping 3");
					invenCont.repaint();	
				}
				else if (e.getSource().equals(pos4)){
					for(Item item : equipped){
						if(inventory.getItem(4)==null) return;
						if(inventory.getItem(4).equals(item)) {
							System.out.println("Item already equipped");
							return;
						}
					}
					equipped[selectedPos] = inventory.getItem(4);
					System.out.println("Equipping 4");
					invenCont.repaint();	
				}
				
				else if (e.getSource().equals(pos5)){
					for(Item item : equipped){
						if(inventory.getItem(5)==null) return;
					if(inventory.getItem(5).equals(item)) {
						System.out.println("Item already equipped");
						return;
					}
					}
					equipped[selectedPos] = inventory.getItem(5);
					System.out.println("Equipping 5");
					invenCont.repaint();	
				}
				else if (e.getSource().equals(pos6)){
					for(Item item : equipped){
						if(inventory.getItem(6)==null) return;
						if(inventory.getItem(6).equals(item)) {
							System.out.println("Item already equipped");
							return;
						}
					}
					equipped[selectedPos] = inventory.getItem(6);
					System.out.println("Equipping 6");
					invenCont.repaint();	
				}
				else if (e.getSource().equals(pos7)){
					for(Item item : equipped){
						if(inventory.getItem(7)==null) return;
						if(inventory.getItem(7).equals(item)) {
							System.out.println("Item already equipped");
							return;
						}
					}
					equipped[selectedPos] = inventory.getItem(7);
					System.out.println("Equipping 7");
					invenCont.repaint();	
				}
			}
		};

	};
	
	@Override
	protected void paintComponent(Graphics g) {


		int x = 100;
		int y = 60;

		g.drawImage(background, 0, 0, null);
		g.drawString("Selected Position" + selectedPos, 20, 20);
		for(int i = 0; i<8; i++){
			Item item = inventory.getItem(i);
			if(item==null)
				g.drawImage(blankBI, x, y, null);
			//	allPos[i] = new Picture(blankBI, 0, 0, 50, 50);

			else 
				g.drawImage(inventory.getItem(i).getIcon(), x, y, null);

			//	allPos[i].setPosition(x, y);
			//	allPos[i].setOpaque(false);
			//	add(allPos[i]);
			x+=50;
			if(x>280){
				x = 100;
				y +=50;
			}


		}




	}

	public void setSelectedPos(int i){
		this.selectedPos = i;
		invenCont.setSelectedPos(i);
		repaint();
	}
	

}
