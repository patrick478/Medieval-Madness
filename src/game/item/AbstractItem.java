package game.item;

import java.awt.image.BufferedImage;

import game.entity.Entity;
import game.entity.moveable.PlayerEntity;

public class AbstractItem {

	private final BufferedImage icon;
	private final String description;
	
	public AbstractItem(BufferedImage _icon, String _descript){
		icon = _icon;
		description = _descript;
	}
	
	public BufferedImage getIcon(){
		return icon;
	}
	
	public String getDescription(){
		return description;
	}
}
