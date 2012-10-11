package initial3d.engine.xhaust;

import static initial3d.Initial3D.*;
import initial3d.Initial3D;
import initial3d.engine.ActionSource;
import initial3d.engine.EventDispatcher;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.Collections;

public abstract class Component extends ActionSource {

	private final int width, height;
	private volatile int x, y;
	private volatile boolean opaque = true;
	private volatile Color col_bg = Color.WHITE;
	private volatile Color col_fg = Color.BLACK;

	private volatile boolean repaint_required = true;
	private boolean repainted = false;
	private int drawid;

	private volatile Component parent = null;

	private volatile Pane pane = null;

	private final EventDispatcher dispatcher = new EventDispatcher();

	public Component(int width_, int height_) {
		width = width_;
		height = height_;

	}

	/* package-private */
	final void setPane(Pane p) {
		pane = p;
	}

	public Pane getPane() {
		return pane;
	}
	
	public boolean isFocused() {
		return this.equals(pane.getLocalFocused());
	}

	/* package-private */
	final void setParent(Component c) {
		parent = c;
	}

	/* package-private */
	final int getDrawID() {
		return drawid;
	}

	/* package-private */
	int doRepaint(Graphics g, Initial3D i3d, int id, double zview) {
		drawid = id;

		i3d.objectID(id);

		i3d.begin(POLYGON);
		i3d.vertex3d(0, -height, zview);
		i3d.vertex3d(-width, -height, zview);
		i3d.vertex3d(-width, 0, zview);
		i3d.vertex3d(0, 0, zview);
		i3d.end();

		if (repaint_required) {
			repaint_required = false;
			repainted = true;
			paintComponent(g);
			paintBorder(g);
		} else {
			repainted = false;
		}

		return id + 1;
	}

	/* package-private */
	boolean repainted() {
		return repainted;
	}

	/* package-private */
	Component findByID(int id) {
		if (drawid == id) {
			return this;
		}
		return null;
	}

	public Iterable<Component> getChildren() {
		return Collections.<Component> emptyList();
	}

	public int count() {
		return 1;
	}

	/**
	 * Invoke this to get this component (and anything else necessary)
	 * repainted.
	 */
	public final void repaint() {
		// recurses up until opaque or null parent
		if (!isOpaque() && parent != null) {
			parent.repaint();
		} else {
			repaintDown();
		}
	}

	protected void repaintDown() {
		// recurses down setting repaint_required
		repaint_required = true;
	}

	protected void paintComponent(Graphics g) {
		if (opaque) {
			g.setColor(col_bg);
			g.fillRect(0, 0, width, height);
		}
	}

	protected void paintBorder(Graphics g) {

	}
	
	public boolean contains(Component c) {
		 return this.equals(c);
	}

	public final void requestLocalFocus() {
		pane.requestLocalFocusFor(this);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setPosition(int x_, int y_) {
		x = x_;
		y = y_;
	}

	public boolean isOpaque() {
		return opaque;
	}

	public void setOpaque(boolean b) {
		opaque = b;
	}

	public Color getBackgroundColor() {
		return col_bg;
	}

	public void setBackgroundColor(Color c) {
		col_bg = c;
	}
	
	public Color getForegroundColor() {
		return col_fg;
	}
	
	public void setForegroundColor(Color c) {
		col_fg = c;
	}

	final void dispatchKeyEvent(KeyEvent e) {
		processKeyEvent(e);
		dispatcher.dispatchEvent(e);
	}

	final void dispatchMouseEvent(MouseEvent e) {
		processMouseEvent(e);
		dispatcher.dispatchEvent(e);
	}

	/** Override for custom processing of key events. */
	protected void processKeyEvent(KeyEvent e) {

	}

	/** Override for custom processing of mouse events. */
	protected void processMouseEvent(MouseEvent e) {

	}
	
	@Override
	public final int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public final boolean equals(Object o) {
		return super.equals(o);
	}

	public void addKeyListener(KeyListener l) {
		dispatcher.addKeyListener(l);
	}

	public void addMouseListener(MouseListener l) {
		dispatcher.addMouseListener(l);
	}

	public void addMouseMotionListener(MouseMotionListener l) {
		dispatcher.addMouseMotionListener(l);
	}

	public void addMouseWheelListener(MouseWheelListener l) {
		dispatcher.addMouseWheelListener(l);
	}

	public void removeKeyListener(KeyListener l) {
		dispatcher.removeKeyListener(l);
	}

	public void removeMouseListener(MouseListener l) {
		dispatcher.removeMouseListener(l);
	}

	public void removeMouseMotionListener(MouseMotionListener l) {
		dispatcher.removeMouseMotionListener(l);
	}

	public void removeMouseWheelListener(MouseWheelListener l) {
		dispatcher.removeMouseWheelListener(l);
	}

}
