package initial3d.engine;

import initial3d.linearmath.Matrix;

/**
 * Represents a Quaternion (for orientation / rotation).
 * 
 * @author Ben Allen
 */
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
	public static final Quat create(double angle, Vec3 axis) {
		// scale so that +-PI <=> -1 and 0 <=> 0
		angle = angle * 0.5d;
		axis = axis.unit().scale(Math.sin(angle));
		return new Quat(Math.cos(angle), axis.x, axis.y, axis.z);
	}

	/**
	 * Create a Quat from a vector in the direction of the rotation axis whose
	 * magnitude is equal to the angle to rotate by.
	 */
	public static final Quat create(Vec3 rot) {
		double angle = rot.mag();
		if (Double.isInfinite(rot.invmag())) return one;
		Vec3 axis = rot.unit();
		return create(angle, axis);
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

	/**
	 * Create a new Quat from the components of this Quat scaled by a given
	 * factor.
	 */
	public Quat scale(double f) {
		return create(w * f, x * f, y * f, z * f);
	}

	/** Scale this Quat to a unit Quat (norm == 1). */
	public Quat unit() {
		return scale(1d / norm());
	}

	/**
	 * Get the conjugate of this Quat. If this Quat has norm == 1, then its
	 * conjugate is also its inverse.
	 */
	public Quat conj() {
		return create(w, -x, -y, -z);
	}

	/** Get the (multiplicative) inverse of this Quat. */
	public Quat inv() {
		double inorm2 = 1 / (w * w + x * x + y * y + z * z);
		return create(inorm2 * w, -inorm2 * x, -inorm2 * y, -inorm2 * z);
	}

	/**
	 * <b><i>Left-multiply</i></b> this Quat by <code>left</code>. Use this to
	 * compose rotations, where <code>this</code> is rotated by
	 * <code>left</code>.
	 */
	public Quat mul(Quat left) {
		return create(left.w * w - left.x * x - left.y * y - left.z * z, left.w * x + left.x * w + left.y * z - left.z
				* y, left.w * y - left.x * z + left.y * w + left.z * x, left.w * z + left.x * y - left.y * x + left.z
				* w);
	}

	/**
	 * <b><i>Left-multiply</i></b> this Quat by the Quat described by
	 * <code>(w,x,y,z)</code>.
	 */
	public Quat mul(double leftw, double leftx, double lefty, double leftz) {
		return create(leftw * w - leftx * x - lefty * y - leftz * z, leftw * x + leftx * w + lefty * z - leftz * y,
				leftw * y - leftx * z + lefty * w + leftz * x, leftw * z + leftx * y - lefty * x + leftz * w);
	}

	/**
	 * Rotate a Vec3 by the rotation described by this Quat. Assumes this Quat
	 * has norm == 1.
	 */
	public Vec3 rot(Vec3 v) {
		// this * v * this^(-1)
		Quat q = conj().mul(0, v.x, v.y, v.z).mul(this);
		return Vec3.create(q.x, q.y, q.z);
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
		throw new AssertionError("Quaternion addition doesn't do what you think it does. Also, read the javadoc.");
	}

	/** Probably not very useful. */
	public Quat addAndYesIKnowWhatImDoing(Quat q) {
		return create(w + q.w, x + q.x, y + q.y, z + q.z);
	}

	public double[][] toOrientationMatrix(double[][] m) {
		Matrix.identity(m);
		m[0][0] = w * w + x * x - y * y - z * z;
		m[0][1] = 2 * x * y + 2 * w * z;
		m[0][2] = 2 * x * z - 2 * w * y;
		m[1][0] = 2 * x * y - 2 * w * z;
		m[1][1] = w * w - x * x + y * y - z * z;
		m[1][2] = 2 * y * z + 2 * w * x;
		m[2][0] = 2 * x * z + 2 * w * y;
		m[2][1] = 2 * y * z - 2 * w * x;
		m[2][2] = w * w - x * x - y * y + z * z;
		m[3][3] = w * w + x * x + y * y + z * z;
		// FIXME is this correct? I have no idea anymore
		Matrix.transpose(m, m);
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(w);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Quat other = (Quat) obj;
		if (Double.doubleToLongBits(w) != Double.doubleToLongBits(other.w)) return false;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x)) return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y)) return false;
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z)) return false;
		return true;
	}

}
