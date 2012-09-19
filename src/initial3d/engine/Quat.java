package initial3d.engine;

import initial3d.linearmath.Matrix;

/** Represents a Quaternion (for orientation / rotation). */
public class Quat {

	public static final Quat zero = create(0, 0, 0, 0);
	public static final Quat one = create(1, 0, 0, 0);
	public static final Quat i = create(0, 1, 0, 0);
	public static final Quat j = create(0, 0, 1, 0);
	public static final Quat k = create(0, 0, 0, 1);

	public final double w;
	public final double x;
	public final double y;
	public final double z;

	/** Create a Quat from components. */
	public static final Quat create(double w_, double x_, double y_, double z_) {
		return new Quat(w_, x_, y_, z_);
	}

	/** Create a Quat from angle + axis. */
	public static final Quat create(double w_, Vec3 axis) {
		w_ = w_ * 0.5d;
		axis = axis.unit().scale(Math.sin(w_));
		return new Quat(Math.cos(w_), axis.x, axis.y, axis.z);
	}

	/**
	 * Create a Quat from a vector in the direction of the rotation axis whose magnitude corresponds to the angle to
	 * rotate by.
	 */
	public static final Quat create(Vec3 rot) {

		return null;
	}

	private Quat(double w_, double x_, double y_, double z_) {
		w = w_;
		x = x_;
		y = y_;
		z = z_;
	}

	/** Get the norm ( == magnitude) of this Quat. */
	public double norm() {
		return Math.sqrt(w * w + x * x + y * y + z * z);
	}

	/** Create a new Quat from the components of this Quat scaled by a given factor. */
	public Quat scale(double f) {
		return create(w * f, x * f, y * f, z * f);
	}

	/** Scale this Quat to a unit Quat (norm == 1). */
	public Quat unit() {
		return scale(1d / norm());
	}

	/** Get the conjugate of this Quat. If this Quat has norm == 1, then its conjugate is also its inverse. */
	public Quat conj() {
		return create(w, -x, -y, -z);
	}

	/** Get the (multiplicative) inverse of this Quat. */
	public Quat inv() {
		double inorm2 = 1 / (w * w + x * x + y * y + z * z);
		return create(inorm2 * w, -inorm2 * x, -inorm2 * y, -inorm2 * z);
	}

	/** <b><i>Left-multiply</i></b> this Quat by <code>left</code>. Use this to compose rotations. */
	public Quat mul(Quat left) {
		return create(left.w * w - left.x * x - left.y * y - left.z * z, left.w * x + left.x * w + left.y * z - left.z
				* y, left.w * y - left.x * z + left.y * w + left.z * x, left.w * z + left.x * y - left.y * x + left.z
				* w);
	}
	
	/** Calculate the power of this Quat raised to an arbitrary real exponent. */
	public Quat pow(double alpha) {
		
		// TODO Quat.pow()
		return null;
	}
	
	/** Calculate exp(this). */
	public Quat exp() {
		
		// TODO Quat.exp()
		return null;
	}

	/** Probably not very useful. */
	public Quat add(Quat q) {
		return create(w + q.w, x + q.x, y + q.y, z + q.z);
	}

	public double[][] toOrientationMatrix(double[][] m) {
		Matrix.identity(m);
		m[0][0] = w * w + x * x - y * y - z * z;
		m[0][1] = 2 * x * y - 2 * w * z;
		m[0][2] = 2 * x * z + 2 * w * y;
		m[1][0] = 2 * x * y + 2 * w * z;
		m[1][1] = w * w - x * x + y * y - z * z;
		m[1][2] = 2 * y * z + 2 * w * x;
		m[2][0] = 2 * x * z - 2 * w * y;
		m[2][1] = 2 * y * z - 2 * w * x;
		m[2][2] = w * w - x * x - y * y + z * z;
		return m;
	}
	
	public static final Quat slerp(Quat q0, Quat q1, double t) {
		
		// TODO quaternion slerp
		return null;
	}
	
	public static final Quat nlerp(Quat q0, Quat q1, double t) {
		
		// TODO quaternion nlerp
		return null;
	}

}












