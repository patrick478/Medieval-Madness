package initial3d.engine;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/** JFrame specialised for game-oriented active rendering. */
public class RenderFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private static final Point point_zero = new Point(0, 0);
	private final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

	private Canvas canvas;
	private BufferStrategy bs;

	private static final long FR_SAMPLE_HISTORY = 1000;
	private long t_lastbad = 0;
	private final LinkedList<Long> ftimes = new LinkedList<Long>();

	// TODO should probably just use arrays as lookup tables
	private final Set<Integer> activekeys = Collections.synchronizedSet(new HashSet<Integer>());
	private final Set<Integer> activebuttons = Collections.synchronizedSet(new HashSet<Integer>());

	private final Object lock_mousedata = new Object();
	private volatile int mousetravelx = 0;
	private volatile int mousetravely = 0;
	private volatile int mousescrollclicks = 0;
	private volatile int mousex = 0;
	private volatile int mousey = 0;
	private volatile boolean mousecaptured = false;

	private volatile boolean draw_crosshair = false;

	private volatile Cursor oldcursor = Cursor.getDefaultCursor();

	private final Robot robot;
	private final Cursor blankcursor;

	static {
		// hack to fix awful flicker on resize (well, make it slightly less awful)
		System.setProperty("sun.awt.noerasebackground", "true");
	}

	protected RenderFrame(int w, int h) {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		canvas = new Canvas();
		setLayout(new BorderLayout());
		add(canvas, BorderLayout.CENTER);
		canvas.setPreferredSize(new Dimension(w, h));
		canvas.setFocusable(false);

		setIgnoreRepaint(true);
		canvas.setIgnoreRepaint(true);
		pack();

		canvas.createBufferStrategy(2);
		bs = canvas.getBufferStrategy();

		BufferedImage cursorimg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		blankcursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorimg, new Point(0, 0), "blankcursor");

		try {
			robot = new Robot();
		} catch (AWTException e) {
			// FIXME if robot cannot be instantiated?
			throw new AssertionError(e);
		}

		Toolkit.getDefaultToolkit().addAWTEventListener(new MouseEventHandler(),
				AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
		Toolkit.getDefaultToolkit().addAWTEventListener(new MouseWheelEventHandler(), AWTEvent.MOUSE_WHEEL_EVENT_MASK);
		Toolkit.getDefaultToolkit().addAWTEventListener(new KeyEventHandler(), AWTEvent.KEY_EVENT_MASK);

	}

	/** Create a RenderWindow on the AWT Event Thread, and wait for completion. */
	public static final RenderFrame create(final int width, final int height) {
		// hackity hack hack hack...
		final RenderFrame[] win = new RenderFrame[1];
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				synchronized (win) {
					win[0] = new RenderFrame(width, height);
					win.notify();
				}
			}
		});
		synchronized (win) {
			while (win[0] == null) {
				try {
					win.wait();
				} catch (InterruptedException e) {
					// do nothing
				}
			}
		}
		return win[0];
	}

	public void display(BufferedImage bi) {
		Graphics g = null;
		try {
			if (bi != null) {
				g = bs.getDrawGraphics();
				g.drawImage(bi, 0, 0, canvas.getWidth(), canvas.getHeight(), null);
				long ct = System.currentTimeMillis();
				long lt = ct - FR_SAMPLE_HISTORY;
				long t, dt;
				long maxdt = 0;
				long mindt = FR_SAMPLE_HISTORY;
				double ft = 0, fr = 0;
				int fc = 1;
				Iterator<Long> it = ftimes.iterator();
				while (it.hasNext()) {
					t = it.next();
					if (ct - t > FR_SAMPLE_HISTORY) {
						it.remove();
					} else {
						dt = t - lt;
						ft += dt;
						fc++;
						mindt = dt < mindt ? dt : mindt;
						maxdt = dt > maxdt ? dt : maxdt;
					}
					lt = t;
				}
				ft += ct - lt;
				ft /= fc;
				fr = 1000d / ft;
				g.setColor(Color.MAGENTA);
				g.drawString(String.format("%05.1f|%03d|%05.1f|%03d|%d", fr, maxdt, ft, mindt, ct - t_lastbad), 5, 15);
				if (draw_crosshair) {
					g.setColor(Color.WHITE);
					int ch_x = canvas.getWidth() / 2 - 10;
					int ch_y = canvas.getHeight() / 2 - 10;
					g.fillRect(ch_x, ch_y + 9, 20, 2);
					g.fillRect(ch_x + 9, ch_y, 2, 20);
				}
				if (!bs.contentsLost()) {
					bs.show();
					ct = System.currentTimeMillis();
					ftimes.add(ct);
					if (ct - lt > 33) {
						// System.out.println("Detected frametime = " + (ct - lt) + "ms.");
						t_lastbad = ct;
					}
				} else {
					System.out.println("Frame dropped: Buffer contents lost.");
				}
			}
		} finally {
			if (g != null) {
				g.dispose();
			}
		}
	}

	/**
	 * Get the current state of a key.
	 * 
	 * @param vk
	 *            The virtual key code of the key to check
	 * @return True iff the key is down
	 */
	public boolean getKey(int vk) {
		return activekeys.contains(vk);
	}

	/**
	 * Get the current state of a key, and if it is down clear its state.
	 * 
	 * @param vk
	 *            The virtual key code of the key to check
	 * @return True iff the key is down
	 */
	public boolean pollKey(int vk) {
		return activekeys.remove(vk);
	}

	/**
	 * Get the current state of a mouse button.
	 * 
	 * @param button
	 *            The code of the mouse button to check, per <code>java.awt.MouseEvent</code> (although the
	 *            corresponding values are 1, 2 and 3)
	 * @return True iff the key is down
	 */
	public boolean getMouseButton(int button) {
		return activebuttons.contains(button);
	}

	/**
	 * Get the current state of a mousebutton, and if it is down clear its state.
	 * 
	 * @param button
	 *            The code of the mouse button to check, per <code>java.awt.MouseEvent</code> (although the
	 *            corresponding values are 1, 2 and 3)
	 * @return True iff the key is down
	 */
	public boolean pollMouseButton(int button) {
		return activebuttons.remove(button);
	}

	/** Determine if this frame is currently fullscreen. */
	public boolean isFullscreen() {
		return gd.getFullScreenWindow() == this;
	}

	/** Set whether this frame should be fullscreen or not. */
	public void setFullscreen(boolean b) {
		if (b && gd.isFullScreenSupported()) {
			Rectangle bounds = gd.getDefaultConfiguration().getBounds();
			gd.setFullScreenWindow(this);
			setBounds(bounds);
			canvas.setBounds(bounds);
			canvas.createBufferStrategy(2);
			bs = canvas.getBufferStrategy();
			System.out.println("RenderFrame : Going fullscreen, using page flipping: "
					+ bs.getCapabilities().isPageFlipping());
		} else {
			gd.setFullScreenWindow(null);
			canvas.createBufferStrategy(2);
			bs = canvas.getBufferStrategy();
		}

	}

	/** Determine if the mouse is currently 'captured', i.e. locked to the centre of the frame. */
	public boolean isMouseCaptured() {
		synchronized (lock_mousedata) {
			return mousecaptured;
		}
	}

	/** Set whether the mouse should be 'captured', i.e. locked to the centre of the frame. */
	public void setMouseCapture(boolean b) {
		synchronized (lock_mousedata) {
			mousecaptured = b;
			if (b) {
				int centrex = canvas.getWidth() / 2;
				int centrey = canvas.getHeight() / 2;
				Point cloc;
				if (isFullscreen()) {
					// HACK for getLocationOnScreen() not giving 0,0 in fullscreen 
					cloc = point_zero;
				} else {
					cloc = canvas.getLocationOnScreen();
				}
				// put the mouse in the centre of the canvas
				robot.mouseMove(cloc.x + centrex, cloc.y + centrey);
			}
		}
	}

	/**
	 * Determine if the cursor has been made invisible by a call to <code>setCursorVisible()</code>.
	 * 
	 * @return false iff <code>setCursorVisible(false)</code> has been called, and setting a custom cursor is allowed.
	 */
	public boolean isCursorVisible() {
		return getCursor() != blankcursor;
	}

	/**
	 * Set the visibility of the cursor over this frame. The cursor can only be hidden if setting a custom cursor is
	 * allowed.
	 */
	public void setCursorVisible(boolean b) {
		if (b) {
			super.setCursor(oldcursor);
		} else if (!b && getCursor() != blankcursor) {
			super.setCursor(blankcursor);
		}
	}

	@Override
	public void setCursor(Cursor cursor) {
		oldcursor = cursor;
		super.setCursor(cursor);
	}

	/** Get the x-position of the mouse within the content pane of this frame. */
	public int getMouseX() {
		synchronized (lock_mousedata) {
			return mousex;
		}
	}

	/** Get the y-position of the mouse within the content pane of this frame. */
	public int getMouseY() {
		synchronized (lock_mousedata) {
			return mousey;
		}
	}

	/**
	 * Get how much x-travel has been made by the mouse. While the mouse is captured, its travel is accumulated
	 * internally and then depleted up to a specified limit by calling this method.
	 */
	public int pollMouseTravelX(int limit) {
		synchronized (lock_mousedata) {
			int mxsign = mousetravelx > 0 ? 1 : mousetravelx < 0 ? -1 : 0;
			if (mousetravelx * mxsign >= limit) {
				mousetravelx -= mxsign * limit;
				return limit;
			} else {
				int t = mousetravelx;
				mousetravelx = 0;
				return t;
			}
		}
	}

	/** Like <code>pollMouseTravelX(int)</code> except all the accumulated travel is read and returned. */
	public int pollMouseTravelX() {
		synchronized (lock_mousedata) {
			int t = mousetravelx;
			mousetravelx = 0;
			return t;
		}
	}

	/**
	 * Get how much y-travel has been made by the mouse. While the mouse is captured, its travel is accumulated
	 * internally and then depleted up to a specified limit by calling this method.
	 */
	public int pollMouseTravelY(int limit) {
		synchronized (lock_mousedata) {
			int mysign = mousetravely > 0 ? 1 : mousetravely < 0 ? -1 : 0;
			if (mousetravely * mysign >= limit) {
				mousetravely -= mysign * limit;
				return limit;
			} else {
				int t = mousetravely;
				mousetravely = 0;
				return t;
			}
		}
	}

	/** Like <code>pollMouseTravelY(int)</code> except all the accumulated travel is read and returned. */
	public int pollMouseTravelY() {
		synchronized (lock_mousedata) {
			int t = mousetravely;
			mousetravely = 0;
			return t;
		}
	}

	/**
	 * Get how much travel has been made by the mouse wheel. The mouse wheel travel is accumulated internally and then
	 * depleted up to a specified limit by calling this method.
	 */
	public int pollMouseWheelClicks(int limit) {
		synchronized (lock_mousedata) {
			int mssign = mousescrollclicks > 0 ? 1 : mousescrollclicks < 0 ? -1 : 0;
			if (mousescrollclicks * mssign >= limit) {
				mousescrollclicks -= mssign * limit;
				return limit;
			} else {
				int t = mousescrollclicks;
				mousescrollclicks = 0;
				return t;
			}
		}
	}

	/** Like <code>pollMouseWheelClicks(int)</code> except all the accumulated travel is read and returned. */
	public int pollMouseWheelClicks() {
		synchronized (lock_mousedata) {
			int t = mousescrollclicks;
			mousescrollclicks = 0;
			return t;
		}
	}

	/** Get the width that displayed images will be drawn at. */
	public int getRenderWidth() {
		return canvas.getWidth();
	}

	/** Get the height that displayed images will be drawn at. */
	public int getRenderHeight() {
		return canvas.getHeight();
	}

	public void setCrosshairVisible(boolean b) {
		draw_crosshair = b;
	}

	public boolean isCrosshairVisible(boolean b) {
		return draw_crosshair;
	}

	private class MouseEventHandler implements AWTEventListener {

		@Override
		public void eventDispatched(AWTEvent event) {
			MouseEvent me = (MouseEvent) event;
			if (event.getID() == MouseEvent.MOUSE_MOVED || event.getID() == MouseEvent.MOUSE_DRAGGED) {
				synchronized (lock_mousedata) {
					Point cloc;
					if (isFullscreen()) {
						// HACK for getLocationOnScreen() not giving 0,0 in fullscreen
						cloc = point_zero;
					} else {
						cloc = canvas.getLocationOnScreen();
					}
					mousex = me.getXOnScreen() - cloc.x;
					mousey = me.getYOnScreen() - cloc.y;
					if (mousecaptured) {
						int centrex = canvas.getWidth() / 2;
						int centrey = canvas.getHeight() / 2;
						mousetravelx += (mousex - centrex);
						mousetravely += (mousey - centrey);
						// put the mouse in the centre of the canvas again
						robot.mouseMove(cloc.x + centrex, cloc.y + centrey);
					}
				}
			}

			if (me.getButton() != MouseEvent.NOBUTTON) {
				synchronized (activebuttons) {
					if (event.getID() == MouseEvent.MOUSE_PRESSED) activebuttons.add(me.getButton());
					if (event.getID() == MouseEvent.MOUSE_RELEASED) activebuttons.remove(me.getButton());
				}
			}
		}

	}

	private class MouseWheelEventHandler implements AWTEventListener {

		@Override
		public void eventDispatched(AWTEvent event) {
			MouseWheelEvent me = (MouseWheelEvent) event;
			synchronized (lock_mousedata) {
				mousescrollclicks += me.getWheelRotation();
			}
		}

	}

	private class KeyEventHandler implements AWTEventListener {

		@Override
		public void eventDispatched(AWTEvent event) {
			KeyEvent ke = (KeyEvent) event;
			synchronized (activekeys) {
				if (ke.getID() == KeyEvent.KEY_PRESSED) activekeys.add(ke.getKeyCode());
				if (ke.getID() == KeyEvent.KEY_RELEASED) activekeys.remove(ke.getKeyCode());
			}
		}

	}

}
