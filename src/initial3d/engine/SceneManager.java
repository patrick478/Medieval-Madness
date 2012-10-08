package initial3d.engine;

import static initial3d.Initial3D.*;
import initial3d.*;

import java.awt.AWTEvent;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Initial3D SceneManager. Handles rendering of Scenes consisting of Drawables.
 * 
 * @author Ben Allen
 */
public class SceneManager implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener,
		AWTEventListener {

	// note: near clip must be further than near plane, far cull is independent
	// of far plane setting
	private static final double NEAR_PLANE = 0.1;
	private static final double FAR_PLANE = 9001;

	private final int width, height;

	private volatile Scene scene = null;
	private volatile DisplayTarget dtarget = null;
	private final Object lock_scene = new Object();
	private volatile boolean scene_change = false;

	private final BlockingQueue<AWTEvent> eventqueue = new LinkedBlockingQueue<AWTEvent>();
	private volatile boolean eventsenabled = true;

	private volatile Profiler profiler = null;

	public SceneManager(int width_, int height_) {
		width = width_;
		height = height_;

		new SceneManagerWorker().start();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setEventsEnabled(boolean b) {
		eventsenabled = b;
	}

	public Profiler getProfiler() {
		return profiler;
	}

	public void attachToScene(Scene s) {
		scene_change = true;
		synchronized (lock_scene) {
			scene = s;
		}
		scene_change = false;
	}

	public void setDisplayTarget(DisplayTarget dtarget_) {
		synchronized (lock_scene) {
			dtarget = dtarget_;
		}
	}

	private class SceneManagerWorker extends Thread {

		private double display_ar = 0;
		private double camera_fov = 0;
		private Initial3D i3d;

		public SceneManagerWorker() {
			setDaemon(true);
			display_ar = width / (double) height;
			i3d = Initial3D.createInstance();
			profiler = i3d.getProfiler();
			profiler.setAutoResetEnabled(true);
			profiler.setResetOutput(System.out);
		}

		public void run() {

			BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			int[] bidata = ((DataBufferInt) (bi.getRaster().getDataBuffer())).getData();

			i3d.projectionMode(PERSPECTIVE);
			i3d.useFrameBuffer(bidata, width);
			i3d.viewportSize(width, height);
			i3d.cullFace(BACK);
			i3d.polygonMode(FRONT_AND_BACK, POLY_FILL);
			i3d.shadeModel(SHADEMODEL_FLAT);
			i3d.enable(MIPMAPS);

			// temp light
			long light = LIGHT0;
			i3d.lightfv(light, DIFFUSE, new float[] { 1f, 1f, 0.7f });
			i3d.lightfv(light, SPECULAR, new float[] { 1f, 1f, 0.7f });
			i3d.lightfv(light, AMBIENT, new float[] { 0.01f, 0.01f, 0.01f });
			i3d.lightf(light, INTENSITY, 0.9f);
			i3d.enable(light);
			double[] light0p = new double[] { 0, 0.5, 0.5, 0 };

			List<Drawable> event_drawables = new ArrayList<Drawable>();
			List<Drawable> focus_drawables = new ArrayList<Drawable>();

			Drawable last_target = null;
			int last_target_id_offset = 0;

			int drawid = 1;

			while (true) {
				try {
					if (scene_change) {
						// this is to stop this thread hogging the lock
						Thread.yield();
					}
					boolean idle = false;
					synchronized (lock_scene) {
						if (scene != null) {

							// allow scene to add / remove stuff etc
							profiler.startSection("I3D-sceneman_scene-tick");
							scene.renderTick();
							profiler.endSection("I3D-sceneman_scene-tick");

							Camera cam = scene.getCamera();

							// check ar of output and camera fov, reset
							// projection / fog / whatever if needed
							if (Math.abs(cam.getFOV() - camera_fov) > 0.001
									|| Math.abs(dtarget.getDisplayWidth() / (double) dtarget.getDisplayHeight()
											- display_ar) > 0.001) {
								camera_fov = cam.getFOV();
								display_ar = dtarget.getDisplayWidth() / (double) dtarget.getDisplayHeight();
								loadProjection(NEAR_PLANE, FAR_PLANE, camera_fov, display_ar);
							}

							// load view transform
							cam.loadViewTransform(i3d);

							// load lights as appropriate
							i3d.lightdv(light, POSITION, light0p);
							// TODO proper lighting

							// optimised id buffer clearing
							if (drawid < 0) {
								// drawid overflowed, reset and clear buffer
								// buffer cleared to 0 so can't use that as an
								// id
								drawid = 1;
								i3d.clear(ID_BUFFER_BIT);
							}

							// draw stuff as appropriate
							profiler.startSection("I3D-sceneman_draw");
							for (Drawable d : scene.getDrawables()) {
								d.update();
								if (d.pollRemovalRequested()) {
									scene.removeDrawable(d);
								} else {

									boolean focus_requested = d.pollFocusRequested();
									if (d.isVisible()) {

										// TODO intelligent selection of what to
										// draw

										if (d.isInputEnabled()) {
											event_drawables.add(d);
											if (focus_requested) focus_drawables.add(d);

											// set draw id range and increment
											// for next
											int idcount = d.getRequestedIDCount();
											d.setDrawIDs(drawid, idcount);
											drawid += idcount;

											i3d.enable(WRITE_ID);
										} else {
											i3d.disable(WRITE_ID);
										}

										// near clip + far cull
										i3d.nearClip(0.2);
										i3d.farCull(8800);

										i3d.enable(LIGHTING | DEPTH_TEST | WRITE_COLOR | WRITE_Z);
										d.draw(i3d, width, height);

									}
								}
							}

							// draw sky
							// i3d.disable(LIGHTING);
							// i3d.matrixMode(MODEL);
							// i3d.pushMatrix();
							// i3d.loadIdentity();
							// i3d.matrixMode(VIEW);
							// i3d.pushMatrix();
							// i3d.loadIdentity();
							// i3d.begin(POLYGON);
							// i3d.normal3d(0, 0, -1);
							// i3d.color3d(0.1, 0.1, 0.9);
							// i3d.vertex3d(9000, -100, 48);
							// i3d.vertex3d(-9000, -100, 48);
							// i3d.vertex3d(-9000, 100, 48);
							// i3d.vertex3d(9000, 100, 48);
							// i3d.end();
							// i3d.popMatrix();
							// i3d.matrixMode(MODEL);
							// i3d.popMatrix();
							// end sky

							// finish
							i3d.finish();
							profiler.endSection("I3D-sceneman_draw");

							// push frame to output
							if (dtarget != null) {
								profiler.startSection("I3D-sceneman_display");
								dtarget.display(bi);
								profiler.endSection("I3D-sceneman_display");
							}

							// process input events (sending mouse / keyboard
							// events to drawables)
							if (eventsenabled) {
								profiler.startSection("I3D-sceneman_events");

								// deal with requested focus changes first
								Drawable focused = scene.getFocusedDrawable();
								if (focused == null) {
									if (!focus_drawables.isEmpty()) {
										focused = focus_drawables.get(0);
									}
								} else {
									for (Drawable d : focus_drawables) {
										if (focused.releaseFocusTo(d)) {
											focused = d;
											break;
										}
									}
								}
								scene.setFocusedDrawable(focused);

								// push events to the correct drawable
								AWTEvent e;
								while ((e = eventqueue.poll()) != null) {
									try {
										switch (e.getID()) {
										case KeyEvent.KEY_PRESSED:
										case KeyEvent.KEY_RELEASED:
										case KeyEvent.KEY_TYPED:
											// send all key events to focused
											// drawable
											if (focused != null) {
												focused.dispatchKeyEvent((KeyEvent) e);
											}
											break;
										case MouseEvent.MOUSE_PRESSED:
										case MouseEvent.MOUSE_RELEASED:
										case MouseEvent.MOUSE_CLICKED:
										case MouseEvent.MOUSE_MOVED:
										case MouseEvent.MOUSE_DRAGGED:
										case MouseWheelEvent.MOUSE_WHEEL:
											// mouse event behaviour determined
											// by screen location
											MouseEvent me = (MouseEvent) e;
											int framex = getFrameX(me.getX());
											int framey = getFrameY(me.getY());

											// find target of mouse event
											int event_drawid = i3d.queryBuffer(ID_BUFFER_BIT, framex, framey);
											Drawable target = null;
											for (Drawable d : event_drawables) {
												if (d.ownsDrawID(event_drawid)) {
													target = d;
													break;
												}
											}

											// try to switch focus, if needed,
											// on mouse pressed
											if (me.getID() == MouseEvent.MOUSE_PRESSED
													&& (focused == null || (!focused.equals(target) && focused
															.releaseFocusTo(target)))) {
												focused = target;
												scene.setFocusedDrawable(focused);
											}

											// about entered / exited events:
											// they rely on the order ids are allocated within each
											// Drawable being consistent

											switch (me.getID()) {
											case MouseEvent.MOUSE_MOVED:
											case MouseEvent.MOUSE_DRAGGED:
												// send regardless of focus
												if (target != null) {
													if (last_target != target
															|| (event_drawid - target.getDrawIDStart()) != last_target_id_offset) {
														// fire mouse entered
														MouseEvent enterevent = new MouseEvent(me.getComponent(),
																MouseEvent.MOUSE_ENTERED, me.getWhen(),
																me.getModifiersEx(), me.getX(), me.getY(),
																me.getXOnScreen(), me.getYOnScreen(),
																me.getClickCount(), me.isPopupTrigger(), me.getButton());
														target.dispatchMouseEvent(enterevent, event_drawid, framex,
																framey);
													}
													target.dispatchMouseEvent(me, event_drawid, framex, framey);
												}
												if (last_target != null
														&& (last_target != target || (event_drawid - target
																.getDrawIDStart()) != last_target_id_offset)) {
													// fire mouse exited
													MouseEvent exitevent = new MouseEvent(me.getComponent(),
															MouseEvent.MOUSE_EXITED, me.getWhen(), me.getModifiersEx(),
															me.getX(), me.getY(), me.getXOnScreen(), me.getYOnScreen(),
															me.getClickCount(), me.isPopupTrigger(), me.getButton());
													last_target.dispatchMouseEvent(exitevent,
															last_target.getDrawIDStart() + last_target_id_offset,
															framex, framey);
												}

												break;
											case MouseEvent.MOUSE_PRESSED:
											case MouseEvent.MOUSE_RELEASED:
											case MouseEvent.MOUSE_CLICKED:
											case MouseWheelEvent.MOUSE_WHEEL:
												// send to target if it has
												// focus
												if (focused != null && focused.equals(target)) {
													target.dispatchMouseEvent(me, event_drawid, framex, framey);
												}
												break;
											default:
												// shouldn't happen
											}

											last_target = target;
											last_target_id_offset = last_target == null ? 0 : event_drawid
													- last_target.getDrawIDStart();

											break;
										default:
											// ignore mouse entered, exited
											// actual events
											// (and anything else...)
										}

									} catch (ClassCastException e1) {
										// event wasn't a mouse event... whoops
									}
								}
								profiler.endSection("I3D-sceneman_events");
							} else {
								eventqueue.clear();
							}
							event_drawables.clear();
							focus_drawables.clear();

						} else {
							idle = true;
							i3d.finish();
							if (dtarget != null) {
								profiler.startSection("I3D-sceneman_display");
								dtarget.display(bi);
								profiler.endSection("I3D-sceneman_display");
							}
						}
					}

					// don't want to sleep inside the syncro block now do we
					if (idle) {
						profiler.startSection("I3D-sceneman_idle");
						Thread.sleep(10);
						profiler.endSection("I3D-sceneman_idle");
					}

				} catch (Exception e) {
					// something broke. print error, keep going
					e.printStackTrace();
					// don't know if profiler still in consistent state or not
					profiler.reset();
				}

			}

		}

		private void loadProjection(double near, double far, double fov, double ratio) {
			i3d.matrixMode(PROJ);
			i3d.loadPerspectiveFOV(near, far, fov, ratio);
			i3d.initFog();
		}

		private int getFrameX(int screenx) {
			return screenx * width / dtarget.getDisplayWidth();
		}

		private int getFrameY(int screeny) {
			return screeny * height / dtarget.getDisplayHeight();
		}

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		eventqueue.add(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		eventqueue.add(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		eventqueue.add(e);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		eventqueue.add(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		eventqueue.add(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		eventqueue.add(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		eventqueue.add(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		eventqueue.add(e);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		eventqueue.add(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		eventqueue.add(e);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		eventqueue.add(e);
	}

	@Override
	public void eventDispatched(AWTEvent e) {
		eventqueue.add(e);
	}

}
