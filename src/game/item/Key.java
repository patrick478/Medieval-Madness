package game.item;

import game.ItemType;
import game.modelloader.Content;

import initial3d.engine.Vec3;

import java.awt.image.BufferedImage;

public class Key extends Item{

	public Key(Vec3 _pos) {
		this(System.nanoTime(), _pos);
	}
	
	public Key(long id, Vec3 _pos) {
		super(id, Content.<BufferedImage> loadContent("resources/inventory/key.png"), "A key used to unlock doors", ItemType.Key, _pos);
	}

}
