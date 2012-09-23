package initial3d.engine;

public class SceneManager {

	private final int width, height;

	private volatile Scene scene = null;
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

	private class SceneManagerWorker extends Thread {

		public SceneManagerWorker() {
			setDaemon(true);
		}

		public void run() {

			// do init here

			while (true) {
				try {
					boolean idle = false;
					synchronized (lock_scene) {

						if (scene != null) {

							// check ar of output, reset projection / fog / whatever if needed

							// load lights as appropriate

							// draw stuff as appropriate

							// finish

							// push frame to output

							// process input events (sending mouse / keyboard events to drawables)

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

	}

}
