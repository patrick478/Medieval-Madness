package initial3d.engine;

import java.awt.AWTEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashSet;
import java.util.Set;

public class EventDispatcher {

	Set<KeyListener> keylisteners = new HashSet<KeyListener>();
	Set<MouseListener> mouselisteners = new HashSet<MouseListener>();
	Set<MouseMotionListener> mousemotionlisteners = new HashSet<MouseMotionListener>();
	Set<MouseWheelListener> mousewheellisteners = new HashSet<MouseWheelListener>();

	public void dispatchEvent(AWTEvent e) {
		switch (e.getID()) {
		case KeyEvent.KEY_PRESSED:
			for (KeyListener l : keylisteners) {
				l.keyPressed((KeyEvent) e);
			}
			break;
		case KeyEvent.KEY_RELEASED:
			for (KeyListener l : keylisteners) {
				l.keyReleased((KeyEvent) e);
			}
			break;
		case KeyEvent.KEY_TYPED:
			for (KeyListener l : keylisteners) {
				l.keyTyped((KeyEvent) e);
			}
			break;
		case MouseEvent.MOUSE_PRESSED:
			for (MouseListener l : mouselisteners) {
				l.mousePressed((MouseEvent) e);
			}
			break;
		case MouseEvent.MOUSE_RELEASED:
			for (MouseListener l : mouselisteners) {
				l.mouseReleased((MouseEvent) e);
			}
			break;
		case MouseEvent.MOUSE_CLICKED:
			for (MouseListener l : mouselisteners) {
				l.mouseClicked((MouseEvent) e);
			}
			break;
		case MouseEvent.MOUSE_ENTERED:
			for (MouseListener l : mouselisteners) {
				l.mouseEntered((MouseEvent) e);
			}
			break;
		case MouseEvent.MOUSE_EXITED:
			for (MouseListener l : mouselisteners) {
				l.mouseExited((MouseEvent) e);
			}
			break;
		case MouseEvent.MOUSE_MOVED:
			for (MouseMotionListener l : mousemotionlisteners) {
				l.mouseMoved((MouseEvent) e);
			}
			break;
		case MouseEvent.MOUSE_DRAGGED:
			for (MouseMotionListener l : mousemotionlisteners) {
				l.mouseDragged((MouseEvent) e);
			}
			break;
		case MouseWheelEvent.MOUSE_WHEEL:
			for (MouseWheelListener l : mousewheellisteners) {
				l.mouseWheelMoved((MouseWheelEvent) e);
			}
			break;
		default:
			// don't think i've forgotten anything?
		}
	}

	public void addKeyListener(KeyListener l) {
		keylisteners.add(l);
	}

	public void addMouseListener(MouseListener l) {
		mouselisteners.add(l);
	}

	public void addMouseMotionListener(MouseMotionListener l) {
		mousemotionlisteners.add(l);
	}

	public void addMouseWheelListener(MouseWheelListener l) {
		mousewheellisteners.add(l);
	}

	public void removeKeyListener(KeyListener l) {
		keylisteners.remove(l);
	}

	public void removeMouseListener(MouseListener l) {
		mouselisteners.remove(l);
	}

	public void removeMouseMotionListener(MouseMotionListener l) {
		mousemotionlisteners.remove(l);
	}

	public void removeMouseWheelListener(MouseWheelListener l) {
		mousewheellisteners.remove(l);
	}

}
