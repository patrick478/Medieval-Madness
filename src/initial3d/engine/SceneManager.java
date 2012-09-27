package initial3d.engine;

import static initial3d.Initial3D.*;
import initial3d.*;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class SceneManager {

	// note: near clip must be further than near plane, far cull is independent
	// of far plane setting
	private static final double NEAR_PLANE = 0.1;
	private static final double FAR_PLANE = 9001;

	private final int width, height;

	private volatile Scene scene = null;
	private volatile DisplayTarget dtarget = null;
	private final Object lock_scene = new Object();

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

	public void attachToScene(Scene s) {
		synchronized (lock_scene) {
			scene = s;
		}
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

			// TODO add option to control profiler output
			Profiler profiler = i3d.getProfiler();
			profiler.setAutoResetEnabled(true);
			profiler.setResetOutput(System.out);

			// temp light
			i3d.lightfv(LIGHT0, DIFFUSE, new float[] { 1f, 1f, 0.7f });
			i3d.lightfv(LIGHT0, SPECULAR, new float[] { 1f, 1f, 0.7f });
			i3d.lightfv(LIGHT0, AMBIENT, new float[] { 0.01f, 0.01f, 0.01f });
			i3d.lightf(LIGHT0, INTENSITY, 0.9f);
			i3d.enable(LIGHT0);
			double[] light0p = new double[] { 0, 1, 0, 0 };

			while (true) {
				try {
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
							i3d.enable(LIGHTING);
							i3d.lightdv(LIGHT0, POSITION, light0p);
							// TODO proper lighting

							// draw stuff as appropriate
							profiler.startSection("I3D-sceneman_draw");
							for (Drawable d : scene.getDrawables()) {
								if (d.pollRemovalRequested()) {
									scene.removeDrawable(d);
								} else {

									// TODO intelligent selection of what to
									// draw
									d.draw(i3d);

								}
							}
							profiler.endSection("I3D-sceneman_draw");

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
							profiler.startSection("I3D-sceneman_finish");
							i3d.finish();
							profiler.endSection("I3D-sceneman_finish");

							// push frame to output
							profiler.startSection("I3D-sceneman_display");
							dtarget.display(bi);
							profiler.endSection("I3D-sceneman_display");

							// process input events (sending mouse / keyboard
							// events to drawables)
							// TODO input events to drawables

						} else {
							idle = true;
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
				}

			}

		}

		private void loadProjection(double near, double far, double fov, double ratio) {
			i3d.matrixMode(PROJ);
			i3d.loadPerspectiveFOV(near, far, fov, ratio);
			i3d.initFog();
		}

	}

}
