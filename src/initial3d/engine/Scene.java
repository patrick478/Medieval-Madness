package initial3d.engine;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Scene {

	BlockingQueue<Drawable> add_drawable = new LinkedBlockingQueue<Drawable>();
	BlockingQueue<Drawable> remove_drawable = new LinkedBlockingQueue<Drawable>();
	BlockingQueue<Light> add_light = new LinkedBlockingQueue<Light>();
	BlockingQueue<Light> remove_light = new LinkedBlockingQueue<Light>();

	public Scene() {
		
	}
	
	public void addDrawable(Drawable d) {
		add_drawable.add(d);
	}

	public void removeDrawable(Drawable d) {
		remove_drawable.add(d);
	}

	public <T extends Collection<Drawable>> T extractDrawables(T c) {

		return null;
	}

	public void clearDrawables() {

	}

	public void addLight(Light l) {
		add_light.add(l);
	}

	public void removeLight(Light l) {
		remove_light.add(l);
	}

	public <T extends Collection<Light>> T extractLights(T c) {

		return null;
	}

	public void clearLights() {

	}

	public Camera getCamera() {

		return null;
	}
	
	/* package-private */
	void processChanges() {
		while(!remove_drawable.isEmpty()) {
			Drawable d = remove_drawable.poll();
			// remove d
		}
		while(!add_drawable.isEmpty()) {
			Drawable d = add_drawable.poll();
			// add d
		}
		while(!remove_light.isEmpty()) {
			Light l = remove_light.poll();
			// remove l
		}
		while(!add_light.isEmpty()) {
			Light l = add_light.poll();
			// add d
		}
	}
	
	/* package-private */
	Iterable<Drawable> getDrawables() {
		
		return null;
	}
	
	/* package-private */
	Iterable<Light> getLights() {
		
		return null;
	}

}
