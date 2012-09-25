package initial3d.engine;

import static initial3d.Initial3D.*;
import initial3d.*;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class SceneManager {

	// note: near clip must be further than near plane, far cull is independent of far plane setting
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

		private double display_ar = 1;
		private double camera_fov = Math.PI / 3;
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
							scene.renderTick();

							Camera cam = scene.getCamera();

							// check ar of output and camera fov, reset projection / fog / whatever if needed
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
							i3d.lightdv(LIGHT0, POSITION, light0p);
							// TODO proper lighting

							// draw stuff as appropriate
							for (Drawable d : scene.getDrawables()) {
								if (d.pollRemovalRequested()) {
									scene.removeDrawable(d);
								} else {

									// TODO intelligent selection of what to draw
									d.draw(i3d);

								}
							}

							// finish
							i3d.finish();

							// push frame to output
							dtarget.display(bi);

							// process input events (sending mouse / keyboard events to drawables)
							// TODO input events to drawables

						} else {
							idle = true;
						}
					}

					// don't want to sleep inside the syncro block now do we
					if (idle) Thread.sleep(10);

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
