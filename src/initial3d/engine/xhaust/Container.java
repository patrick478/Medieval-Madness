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

	protected void paintChildren(Graphics g) {
		for (Component c : children) {
			Graphics g2 = g.create(c.getX(), c.getY(), c.getWidth(), c.getHeight());
			c.paint(g2);
		}
	}
	
	@Override
	public int count() {
		int i = 1;
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
	boolean doRepaint(Graphics g, Initial3D i3d, int id, double zview) {
		
		
		return true;
	}
	
}
