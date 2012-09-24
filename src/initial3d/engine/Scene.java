package initial3d.engine;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Scene {

	private static final int CHANGE_LIMIT = 10;

	private final BlockingQueue<Drawable> add_drawable = new LinkedBlockingQueue<Drawable>();
	private final BlockingQueue<Drawable> remove_drawable = new LinkedBlockingQueue<Drawable>();
	private final BlockingQueue<Light> add_light = new LinkedBlockingQueue<Light>();
	private final BlockingQueue<Light> remove_light = new LinkedBlockingQueue<Light>();

	private volatile boolean clear_drawables = false;
	private volatile boolean clear_lights = false;

	private final Set<Drawable> drawables = new HashSet<Drawable>();
	private final Set<Light> lights = new HashSet<Light>();

	public Scene() {

	}

	public void addDrawable(Drawable d) {
		add_drawable.add(d);
	}

	public void removeDrawable(Drawable d) {
		remove_drawable.add(d);
	}

	public <T extends Collection<Drawable>> T extractDrawables(T c) {
		c.addAll(drawables);
		return c;
	}

	public void clearDrawables() {
		clear_drawables = true;
	}

	public void addLight(Light l) {
		add_light.add(l);
	}

	public void removeLight(Light l) {
		remove_light.add(l);
	}

	public <T extends Collection<Light>> T extractLights(T c) {
		c.addAll(lights);
		return c;
	}

	public void clearLights() {
		clear_lights = true;
	}

	public OldCamera getCamera() {

		return null;
	}

	/* package-private */
	void processChanges() {
		if (clear_drawables) {
			remove_drawable.clear();
			drawables.clear();
		}
		if (clear_lights) {
			remove_light.clear();
			lights.clear();
		}
		// add / remove up to a limit
		for (int i = CHANGE_LIMIT; i-- > 0;) {
			drawables.remove(remove_drawable.poll());
		}
		for (int i = CHANGE_LIMIT; i-- > 0;) {
			drawables.add(add_drawable.poll());
		}
		for (int i = CHANGE_LIMIT; i-- > 0;) {
			lights.remove(remove_light.poll());
		}
		for (int i = CHANGE_LIMIT; i-- > 0;) {
			lights.add(add_light.poll());
		}
	}

	/* package-private */
	Iterable<Drawable> getDrawables() {

		return drawables;
	}

	/* package-private */
	Iterable<Light> getLights() {

		return lights;
	}

}
