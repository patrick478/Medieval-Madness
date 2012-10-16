package game.item;

import game.ItemType;

import initial3d.engine.Vec3;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemContainer extends Item{

	private final int capacity;
	private final List<Item> items;

	public ItemContainer(BufferedImage _icon, String _descript, int _capacity) {
		super(System.nanoTime(), _icon, _descript, ItemType.Container, Vec3.zero);
		if(_capacity >8 || _capacity <1){
			throw new IllegalArgumentException();
		}
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
			for(Item i : items){
				if(i instanceof ItemContainer){
					if(((ItemContainer)i).addItem(_item)){
						return true;
					}
				}
			}
			return false;
		}
		_item.setParent(this);
		return items.add(_item);
	}

	public boolean isFull(){
		if(items.size()<capacity){
			return false;
		}
		for(Item i : items){
			if(i instanceof ItemContainer){
				if(!((ItemContainer)i).isFull()){
					return false;
				}
			}
		}
		return true;
	}

	public boolean containsItem(Item _item){
		for(Item i : items){
			if(i.equals(_item)){
				return true;
			} else if(i instanceof ItemContainer){
				if(((ItemContainer)i).containsItem(_item)){
					return true;
				}
			}
		}
		return false;
	}

	public boolean removeItem(Item _item){
		Iterator<Item> iter = items.iterator();

		while(iter.hasNext()){
			Item i = iter.next();
			if(i.equals(_item)){
				iter.remove();
				return true;
			} else if(i instanceof ItemContainer){
				if(((ItemContainer)i).removeItem(_item)){
					return true;
				}
			}

		}
		return false;
	}

	public Item getItem(int i){
		if(items.size()>i)
		return items.get(i);
		else return null;
	}
}
