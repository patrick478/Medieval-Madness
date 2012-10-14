package initial3d.engine;

public abstract class ReferenceFrame {

	public static final ReferenceFrame SCENE_ROOT = new ReferenceFrame() {

		@Override
		public Vec3 getPosition() {
			return Vec3.zero;
		}

		@Override
		public Quat getOrientation() {
			return Quat.one;
		}

	};

	private final ReferenceFrame parent;
	private Vec3 draw_position = Vec3.zero;
	private Quat draw_orientation = Quat.one;

	private ReferenceFrame() {
		// this constructor only for SCENE_ROOT
		parent = this;
	}
	
	protected ReferenceFrame(ReferenceFrame parent_) {
		if (parent_ == null) throw new IllegalArgumentException("ReferenceFrame cannot have null parent.");
		parent = parent_;
	}

	public final ReferenceFrame getParent() {
		return parent;
	}

	public abstract Vec3 getPosition();

	public abstract Quat getOrientation();

	/* package-private */
	final void lockForDraw() {
		draw_position = getPosition();
		draw_orientation = getOrientation();
		if (parent != SCENE_ROOT) {
			parent.lockForDraw();
		}
	}

	public final Vec3 getDrawPosition() {
		return draw_position;
	}

	public final Quat getDrawOrientation() {
		return draw_orientation;
	}

}
