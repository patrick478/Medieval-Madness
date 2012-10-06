package initial3d.engine.xhaust;

import initial3d.Initial3D;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Container extends Component {

	private List<Component> children = new ArrayList<Component>();

	public Container(int width_, int height_) {
		super(width_, height_);
	}

	public void add(Component c) {
		children.add(c);
	}

	@Override
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
	public void repaint() {
		super.repaint();
		for (Component c : children) {
			c.repaint();
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
