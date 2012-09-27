package initial3d.engine;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Scene {

	private static final int CHANGE_LIMIT = 100;

	private final BlockingQueue<Drawable> add_drawable = new LinkedBlockingQueue<Drawable>();
	private final BlockingQueue<Drawable> remove_drawable = new LinkedBlockingQueue<Drawable>();
	private final BlockingQueue<Light> add_light = new LinkedBlockingQueue<Light>();
	private final BlockingQueue<Light> remove_light = new LinkedBlockingQueue<Light>();

	private volatile boolean clear_drawables = false;
	private volatile boolean clear_lights = false;

	private final Set<Drawable> drawables = new HashSet<Drawable>();
	private final Set<Light> lights = new HashSet<Light>();

	private Camera cam = new Camera();

	public Scene() {

	}

	public void addDrawable(Drawable d) {
		if (d == null) throw new IllegalArgumentException();
		add_drawable.add(d);
	}
	
	public void addDrawables(Collection<? extends Drawable> d) {
		if (d == null) throw new IllegalArgumentException();
		add_drawable.addAll(d);
	}

	public void removeDrawable(Drawable d) {
		if (d == null) throw new IllegalArgumentException();
		remove_drawable.add(d);
	}
	
	public void removeDrawables(Collection<? extends Drawable> d) {
		if (d == null) throw new IllegalArgumentException();
		remove_drawable.addAll(d);
	}

	public <T extends Collection<Drawable>> T extractDrawables(T c) {
		c.addAll(drawables);
		return c;
	}

	public void clearDrawables() {
		clear_drawables = true;
	}

	public void addLight(Light l) {
		if (l == null) throw new IllegalArgumentException();
		add_light.add(l);
	}

	public void removeLight(Light l) {
		if (l == null) throw new IllegalArgumentException();
		remove_light.add(l);
	}

	public <T extends Collection<Light>> T extractLights(T c) {
		c.addAll(lights);
		return c;
	}

	public void clearLights() {
		clear_lights = true;
	}

	public Camera getCamera() {

		return cam;
	}

	/* package-private */
	void renderTick() {
		if (clear_drawables) {
			remove_drawable.clear();
			drawables.clear();
		}
		if (clear_lights) {
			remove_light.clear();
			lights.clear();
		}
		// add / remove up to a limit
		for (int i = CHANGE_LIMIT; i-- > 0 && !remove_drawable.isEmpty();) {
			Drawable d = remove_drawable.poll();
			drawables.remove(d);
		}
		for (int i = CHANGE_LIMIT; i-- > 0 && !add_drawable.isEmpty();) {
			drawables.add(add_drawable.poll());
		}
		for (int i = CHANGE_LIMIT; i-- > 0 && !remove_light.isEmpty();) {
			lights.remove(remove_light.poll());
		}
		for (int i = CHANGE_LIMIT; i-- > 0 && !add_light.isEmpty();) {
			lights.add(add_light.poll());
		}
		poke();
	}

	/* package-private */
	Iterable<Drawable> getDrawables() {

		return drawables;
	}

	/* package-private */
	Iterable<Light> getLights() {

		return lights;
	}
	
	protected void poke() {
		
	}

}
