package initial3d.engine;

public class MovableReferenceFrame implements ReferenceFrame {

	private final ReferenceFrame parent;

	private volatile Vec3 position = Vec3.zero;
	private volatile Quat orientation = Quat.one;

	public MovableReferenceFrame(ReferenceFrame parent_) {
		parent = parent_;
	}

	@Override
	public ReferenceFrame getParent() {
		return parent;
	}

	@Override
	public Vec3 getPosition() {
		return position;
	}

	@Override
	public Quat getOrientation() {
		return orientation;
	}

	public void setPosition(Vec3 p) {
		position = p;
	}

	public void setOrientation(Quat o) {
		orientation = o;
	}

}
