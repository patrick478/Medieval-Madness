package game.item;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class Item {
	private final BufferedImage icon;
	private final String description;
	private Item parent = null;
	
	public Item(BufferedImage _icon, String _descript){
		icon = _icon;
		description = _descript;
	}
	
	public BufferedImage getIcon() {
		return icon;
	}

	public String getDescription() {
		return description;
	}
	
	/**
	 * Sets the given Items parent to this item. Returns true
	 * if the method was successful
	 * 
	 * @param _item The item to add
	 * @return True
	 */
	public boolean addItem(Item _item){
		_item.parent = this;
		return true;
	}
}
