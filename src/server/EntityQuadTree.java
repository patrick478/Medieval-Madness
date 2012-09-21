package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import common.entity.BoundingBox;
import common.entity.Entity;

public class EntityQuadTree {

	private HashMap<Entity, QuadTreeNode> nodeParent = new HashMap<Entity, QuadTreeNode>();
	private QuadTreeNode root;

	public EntityQuadTree(double xPos, double zPos, double xSize, double zSize) {
		if (xSize <= 0 || zSize <= 0) {
			throw new IllegalArgumentException(
					"Cannot have negtive size value.");
		}
		root = new QuadTreeNode(null, xPos, zPos, xSize, zSize);
	}

	public void add(Entity item) {
		if (root.contains(item)) {
			root.add(item);
		} else {

			// special case to make the world bigger...
		}
	}

	public List<Entity> collisions(Entity e) {
		List<Entity> c = new ArrayList<Entity>();
		if (nodeParent.containsKey(e)) {
			nodeParent.get(e).collisionsUp(e, c);
			nodeParent.get(e).collisionsDown(e, c);
		} else {
			root.collisionsDown(e, c);
		}
		return c;
	}

	/**
	 * Inner node class that makes up the structure of the quad-tree
	 * each node can have either 4 or no children and each contains
	 * the entities at the lowest level such that only that node wholly
	 * contains that entities bounding box.
	 */
	private class QuadTreeNode {

		private static final int MAX_CAPACITY = 5;

		private final QuadTreeNode parent;

		private final double xPos;
		private final double zPos;
		private final double xSize;
		private final double zSize;

		private Set<Entity> content = new HashSet<Entity>();
		private QuadTreeNode[] children = null;

		private int lowerCount = 0;

		public QuadTreeNode(QuadTreeNode _parent, double _xPos, double _zPos,
				double _xSize, double _zSize) {
			parent = _parent;
			xPos = _xPos;
			zPos = _zPos;
			xSize = _xSize;
			zSize = _zSize;
		}

		public boolean isEmpty() {
			return content.isEmpty();
		}

		public void add(Entity item) {

			if (children == null) {
				// if currently over capacity create the children and distribute
				if (content.size() > MAX_CAPACITY) {
					children = new QuadTreeNode[4];
					for (int i = 0; i < 4; i++) {
						children[i] = new QuadTreeNode(this, xPos + xSize * 0.5
								* ((i / 2) % 2), zPos + zSize * 0.5 * (i % 2),
								xSize * 0.5, zSize * 0.5);
					}
					for (Entity e : new ArrayList<Entity>(content)) {
						content.remove(e);
						this.add(e);
					}
				} else {
					content.add(item);
					nodeParent.put(item, this);
					return;
				}
			}
			// here we know that children is not null
			for (QuadTreeNode n : children) {
				if (n.contains(item)) {
					n.add(item);
					lowerCount++;
					return;
				}
			}
			// intersects with but not contained in any children
			content.add(item);
			nodeParent.put(item, this);
		}

		public boolean contains(Entity item) {
			BoundingBox bound = item.getBound();
			return xPos <= bound.getPosition().x
					&& zPos <= bound.getPosition().z
					&& xPos + xSize > bound.getPosition().x + bound.getSize().x
					&& zPos + zSize > bound.getPosition().z + bound.getSize().z;
		}

		public boolean intersects(Entity item) {
			BoundingBox itemBound = item.getBound();

			// create the total size of the sides of the bounding box that
			// contains both boxes
			double xBound = Math.max(xPos + xSize, itemBound.getPosition().x
					+ itemBound.getSize().x)
					- Math.min(xPos, itemBound.getPosition().x);
			double zBound = Math.max(zPos + zSize, itemBound.getPosition().z
					+ itemBound.getSize().z)
					- Math.min(zPos, itemBound.getPosition().z);

			// if the sides of the bound that contains both boxes is less than
			// the sum of the sides of both boxes then those boxes intersect
			return xBound <= xSize + itemBound.getSize().x
					&& zBound <= zSize + itemBound.getSize().z;
		}

		public boolean remove(Entity obj) {
			return false;
			// boolean returnVal = content.remove(obj);
			// if(this.content.size()+this.lowerCount < THRESHOLD_YAY){
			// for(OctreeNode n : children){
			// this.content.addAll(n.content);
			// }
			// this.lowerCount = 0;
			// this.children = null;
			// }
			// if (parent!=null){
			// parent.reduceLowerCount();
			// if (parent.content.size()+parent.lowerCount < THRESHOLD_YAY){
			// for(OctreeNode n : children){
			// parent.content.addAll(n.content);
			// }
			// parent.lowerCount = 0;
			// parent.children = null;
			// }
			// }
			// return returnVal;
		}

		// helper method
		private void reduceLowerCount() {
			this.lowerCount = (this.lowerCount <= 0) ? 0 : this.lowerCount--;
			if (this.parent != null) {
			}
		}

		// all nodes above but excluding this node
		public void collisionsUp(Entity obj, List<Entity> set) {
			for (Entity e : content) {
				if (e.getBound().intersects(obj.getBound())) {
					set.add(e);
				}
			}
			if (parent != null) {
				parent.collisionsUp(obj, set);
			}
		}

		// returns the collsions of both this node and all nodes below it
		public void collisionsDown(Entity obj, List<Entity> set) {
			for (Entity e : content) {
				if (e.getBound().intersects(obj.getBound())) {
					set.add(e);
				}
			}
			if (children != null) {
				for (QuadTreeNode n : children) {
					if (n.intersects(obj)) {
						n.collisionsDown(obj, set);
					}
				}
			}
		}

	}

}
