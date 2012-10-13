package initial3d.engine.xhaust;

import initial3d.Initial3D;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Xhaust Component for holding other components.
 * 
 * @author Ben Allen
 */
public class Container extends Component {

	private List<Component> children = new ArrayList<Component>();

	public Container(int width_, int height_) {
		super(width_, height_);
	}

	public void add(Component c) {
		// avoid adding to multiple panes
		c.remove();
		children.add(c);
		c.setParent(this);
	}

	public boolean remove(Component c) {
		if (children.remove(c)) {
			c.remove();
			return true;
		}
		return false;
	}

	public boolean contains(Component c) {
		if (super.contains(c)) return true;
		for (Component c2 : children) {
			if (c2.contains(c)) {
				return true;
			}
		}
		return false;
	}

	public void clear() {
		for (Component c : new ArrayList<Component>(children)) {
			remove(c);
		}
	}

	public Iterable<Component> getChildren() {
		return Collections.unmodifiableList(children);
	}

	@Override
	public int count() {
		int i = super.count();
		for (Component c : children) {
			i += c.count();
		}
		return i;
	}

	@Override
	protected void repaintDown() {
		// recurses down setting repaint_required
		super.repaintDown();
		for (Component c : children) {
			c.repaintDown();
		}
	}

	@Override
	boolean repainted() {
		boolean r = super.repainted();
		for (Component c : children) {
			r |= c.repainted();
		}
		return r;
	}

	@Override
	Component findByID(int id) {
		Component cid = super.findByID(id);
		if (cid != null) {
			return cid;
		}
		for (Component c : children) {
			cid = c.findByID(id);
			if (cid != null) {
				return cid;
			}
		}
		return null;
	}

	@Override
	int doRepaint(Graphics g, Initial3D i3d, int id, double zview) {
		id = super.doRepaint(g, i3d, id, zview);

		for (Component c : children) {
			Graphics g2 = g.create(c.getX(), c.getY(), c.getWidth(), c.getHeight());
			i3d.pushMatrix();
			i3d.translateX(-c.getX());
			i3d.translateY(-c.getY());
			id = c.doRepaint(g2, i3d, id, zview);
			i3d.popMatrix();
			g2.dispose();
		}

		return id;
	}

}
