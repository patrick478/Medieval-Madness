package initial3d.engine.xhaust;

import java.util.HashSet;
import java.util.Set;

import initial3d.engine.Drawable;
import initial3d.engine.Scene;

public class DialogPane extends Pane {

	private final Pane parent;
	private volatile boolean modal;

	private Set<Pane> children = new HashSet<Pane>();

	public DialogPane(int width_, int height_, Pane parent_, boolean modal_) {
		super(width_, height_, parent_.getZLevel() + 1);
		parent = parent_;
		modal = modal_;
		if (parent instanceof DialogPane) {
			((DialogPane) parent).addChild(this);
		}
		requestFocus();
	}

	protected void addChild(Pane p) {
		children.add(p);
	}

	protected void removeChild(Pane p) {
		children.remove(p);
	}

	@Override
	protected void onSceneRemove(Scene s) {
		if (parent instanceof DialogPane) {
			((DialogPane) parent).removeChild(this);
		}
		parent.requestFocus();
	}

	public boolean isModal() {
		return modal;
	}

	public void setModal(boolean b) {
		modal = b;
	}

	@Override
	public boolean releaseFocusTo(Drawable d) {
		if (!modal) return true;
		if (this.equals(d)) return true;
		if (children.contains(d)) return true;
		return false;
	}
	
	@Override
	public Drawable focusOnRemove() {
		return parent;
	}

}
