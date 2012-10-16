package game.item;

import game.ItemType;
import game.entity.moveable.ItemEntity;

import initial3d.engine.ReferenceFrame;
import initial3d.engine.Vec3;

import java.awt.image.BufferedImage;

public abstract class Item {
	private final BufferedImage icon;
	private final String description;
	private Item parent = null;
	private final Vec3 pos;
	private final ItemEntity ie;
	
	public final long id;
	public final ItemType type;
	
	public Item(long id, BufferedImage _icon, String _descript, ItemType _type, Vec3 _pos){
		this.id = id;
		icon = _icon;
		description = _descript;
		this.type = _type;
		this.pos = _pos;
		this.ie = new ItemEntity(id, _pos, this);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (id != other.id)
			return false;
		return true;
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

	public Vec3 getPosition() {
		return this.ie.getPosition();
	}
	

	public ItemEntity getItemEntity() {
		return this.ie;
	}
}