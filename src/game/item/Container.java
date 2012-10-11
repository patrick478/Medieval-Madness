package game.item;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Container extends Item{
	
	private final int capacity;
	private final List<Item> items;
	
	public Container(BufferedImage _icon, String _descript, int _capacity) {
		super(_icon, _descript);
		capacity = _capacity;
		items = new ArrayList<Item>(capacity);
	}
	/**
	 * If the Container has enough room adds the item to the list of 
	 * items in this Container, sets the item's parent to this and 
	 * returns true. Otherwise returns false.
	 * 
	 * @return Whether the item was successfully added
	 * @Override
	 */
	public boolean addItem(Item _item){
		if(items.size()>=capacity){
			return false;
		}
		items.add(_item);
		return super.addItem(_item);
	}
}
