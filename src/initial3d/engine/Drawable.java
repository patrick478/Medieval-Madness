package initial3d.engine;

import initial3d.Initial3D;

public abstract class Drawable {

	private volatile boolean focus_requested = false;
	private volatile boolean removal_requested = false;

	public void requestFocus() {
		focus_requested = true;
	}

	public void requestRemoval() {
		removal_requested = true;
	}

	/* package-private */
	boolean pollFocusRequested() {
		try {
			return focus_requested;
		} finally {
			focus_requested = false;
		}
	}

	/* package-private */
	boolean pollRemovalRequested() {
		try {
			return removal_requested;
		} finally {
			removal_requested = false;
		}
	}

	/* package-private */
	abstract void draw(Initial3D i3d);

}
