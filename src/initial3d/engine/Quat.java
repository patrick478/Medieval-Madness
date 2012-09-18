package initial3d.engine;

/** Represents a Quaternion (for orientation / rotation). */
public class Quat {

	public final double w;
	public final double x;
	public final double y;
	public final double z;

	public static final Quat create(double w_, double x_, double y_, double z_) {
		return new Quat(w_, x_, y_, z_);
	}
	
	public static final Quat create(double w_, Vec3 axis) {
		return new Quat(w_, axis.x, axis.y, axis.z);
	}

	private Quat(double w_, double x_, double y_, double z_) {
		w = w_;
		x = x_;
		y = y_;
		z = z_;
	}

	public double norm() {

		// TODO
		return 0;
	}

	public Quat conj() {

		// TODO
		return null;
	}

	public Quat inv() {

		// TODO
		return null;
	}

	/** <b><i>Left-multiply</i></b> this Quat by <code>left</code>. */
	public Quat mul(Quat left) {

		// TODO
		return null;
	}
	
	public double[][] toOrientationMatrix(double[][] m) {
		
		// TODO
		return m;
	}

}
