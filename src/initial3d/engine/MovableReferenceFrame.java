package initial3d.engine;

public class MovableReferenceFrame extends ReferenceFrame {

	private volatile Vec3 position = Vec3.zero;
	private volatile Quat orientation = Quat.one;

	public MovableReferenceFrame(ReferenceFrame parent_) {
		super(parent_);
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
