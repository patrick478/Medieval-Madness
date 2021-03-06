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

	private volatile boolean fog_enabled = false;
	private volatile boolean fog_init = false;
	private volatile float fog_param_a = 255f * 1.5f;
	private volatile float fog_param_b = 4096f * 1.5f;
	private volatile Color fog_color = Color.WHITE;

	private volatile Color scene_ambient = Color.BLACK;

	private volatile Drawable focused = null;

	private Camera cam = new Camera();

	public Scene() {

	}

	/* package-private */
	final boolean pollFogInit() {
		try {
			return fog_init;
		} finally {
			fog_init = false;
		}
	}

	public Color getAmbient() {
		return scene_ambient;
	}

	public void setAmbient(Color c) {
		scene_ambient = c;
	}

	public void setFogEnabled(boolean b) {
		fog_enabled = b;
		fog_init = true;
	}

	public boolean getFogEnabled() {
		return fog_enabled;
	}

	public float getFogParamA() {
		return fog_param_a;
	}

	public float getFogParamB() {
		return fog_param_b;
	}

	public Color getFogColor() {
		return fog_color;
	}

	public void setFogParams(float fog_a, float fog_b) {
		fog_param_a = fog_a;
		fog_param_b = fog_b;
	}

	public void setFogColor(Color c) {
		fog_color = c;
	}

	public Drawable getFocusedDrawable() {
		return focused;
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

	public boolean containsDrawable(Drawable d) {
		// FIXME syncro
		if (d == null) throw new IllegalArgumentException();
		if (remove_drawable.contains(d))
			return false;
		else if (drawables.contains(d) || add_drawable.contains(d))
			return true;
		else
			return false;
	}

	public <T extends Collection<Drawable>> T extractDrawables(T c) {
		// FIXME syncro
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
		// FIXME syncro
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
	final void renderTick() {
		if (clear_drawables) {
			remove_drawable.clear();
			drawables.clear();
			focused = null;
		}
		if (clear_lights) {
			remove_light.clear();
			lights.clear();
		}
		// add / remove up to a limit
		for (int i = CHANGE_LIMIT; i-- > 0 && !remove_drawable.isEmpty();) {
			Drawable d = remove_drawable.poll();
			drawables.remove(d);
			if (d.equals(focused)) focused = d.focusOnRemove();
			d.notifySceneRemove(this);
		}
		for (int i = CHANGE_LIMIT; i-- > 0 && !add_drawable.isEmpty();) {
			Drawable d = add_drawable.poll();
			if (d.pollRemovalRequested()) {
				continue;
			}
			drawables.add(d);
			if (d.pollFocusRequested() && ((focused != null && focused.releaseFocusTo(d)) || focused == null)) {
				focused = d;
			}
			d.notifySceneAdd(this);
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
	final Iterable<Drawable> getDrawables() {
		return drawables;
	}

	/* package-private */
	final Iterable<Light> getLights() {
		return lights;
	}

	/* package-private */
	final void setFocusedDrawable(Drawable d) {
		if (drawables.contains(d) || d == null) {
			focused = d;
		}
	}

	/**
	 * Equals and hashCode are overridden and modified final to prevent further
	 * overriding, reference equality is the desired mode of operation.
	 */
	@Override
	public final boolean equals(Object o) {
		return super.equals(o);
	}

	/**
	 * Equals and hashCode are overridden and modified final to prevent further
	 * overriding, reference equality is the desired mode of operation.
	 */
	@Override
	public final int hashCode() {
		return super.hashCode();
	}

	/** Override this to make a self-controlling Scene subclass. */
	protected void poke() {

	}

}
