package game.item;

import java.awt.image.BufferedImage;

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
	
	public Item getParent(){
		return this.parent;
	}

	public void setParent(Item _parent){
		parent = _parent;
	}
}