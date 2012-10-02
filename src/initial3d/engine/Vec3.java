package initial3d.engine;

import initial3d.linearmath.TransformationMatrix4D;

import java.util.Scanner;

/**
 * Vec3 represents an immutable 3-dimensional vector.
 * 
 * @author Ben Allen
 */

public final class Vec3 {

	/** The zero vector, equal to (0,0,0). */
	public static final Vec3 zero;
	
	/** The one vector, equal to (1, 1, 1). */
	public static final Vec3 one;

	/** The (first) basis vector i, equal to (1,0,0). */
	public static final Vec3 i;

	/** The (second) basis vector j, equal to (0,1,0). */
	public static final Vec3 j;

	/** The (third) basis vector k, equal to (0,0,1). */
	public static final Vec3 k;

	/** The first component of this vector. */
	public final double x;

	/** The second component of this vector. */
	public final double y;

	/** The third component of this vector. */
	public final double z;

	// private cache

	// magnitude can never be < 0, so -1 means not initialised
	private double m = -1;
	private double im = -1;
	private Vec3 unitvec, negvec, flatx, flaty, flatz;

	static {
		// i suppose this could be done with another private constructor?
		zero = new Vec3(0, 0, 0);
		zero.m = 0;
		zero.im = 0;
		one = new Vec3(1, 1, 1);
		i = new Vec3(1, 0, 0);
		i.m = 1;
		i.im = 1;
		i.unitvec = i;
		j = new Vec3(0, 1, 0);
		j.m = 1;
		j.im = 1;
		j.unitvec = j;
		k = new Vec3(0, 0, 1);
		k.m = 1;
		k.im = 1;
		k.unitvec = k;
	}

	/**
	 * Private constructor. Do not call this, use <code>create()</code> instead.
	 */
	private Vec3(double x_, double y_, double z_) {
		x = x_;
		y = y_;
		z = z_;
	}

	/** Create a Vec 3 from components. */
	public static final Vec3 create(double x, double y, double z) {
		return new Vec3(x, y, z);
	}

	/** Create a Vec 3 by reading x, y, z components successively as doubles from a Scanner. */
	public static final Vec3 create(Scanner scan) {
		return create(scan.nextDouble(), scan.nextDouble(), scan.nextDouble());
	}

	/** Create a Vec 3 from its 2D column vector (row-major) array representation. */
	public static final Vec3 create(double[][] v) {
		return create(v[0][0], v[1][0], v[2][0]);
	}

	/**
	 * Create a Vec3 that is the normal of a plane defined by 3 points such that the normal lies on the side of the
	 * plane from which the points appear to be ordered anticlockwise, ie normal == (p1 - p0) x (p2 - p1).
	 */
	public static final Vec3 createPlaneNorm(Vec3 p0, Vec3 p1, Vec3 p2) {
		return p1.sub(p0).cross(p2.sub(p1));
	}

	/** Create a Vec3 from the positive extremes (component-wise) of two Vec3s. */
	public static final Vec3 positiveExtremes(Vec3 a, Vec3 b) {
		return create(Math.max(a.x, b.x), Math.max(a.y, b.y), Math.max(a.z, b.z));
	}

	/** Create a Vec3 from the negative extremes (component-wise) of two Vec3s. */
	public static final Vec3 negativeExtremes(Vec3 a, Vec3 b) {
		return create(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.min(a.z, b.z));
	}

	/**
	 * Compute the addition of this Vec3 and vec.
	 */
	public Vec3 add(Vec3 vec) {
		return create(x + vec.x, y + vec.y, z + vec.z);
	}

	/** Compute the addition of this Vec3 and the Vec3 implied by the specified components. */
	public Vec3 add(double dx, double dy, double dz) {
		return create(x + dx, y + dy, z + dz);
	}

	/**
	 * Compute the subtraction of vec from this Vec3.
	 */
	public Vec3 sub(Vec3 vec) {
		return create(x - vec.x, y - vec.y, z - vec.z);
	}

	/**
	 * Generate a copy of this Vec3, but pointing in the opposite direction.
	 */
	public Vec3 neg() {
		if (negvec == null) {
			negvec = create(-x, -y, -z);
			// if mag and/or invmag are known, set
			if (m > 0) negvec.m = -m;
			if (im > 0) negvec.im = -im;
		}
		return negvec;
	}

	/**
	 * Compute the dot product of this Vec3 and vec.
	 */
	public double dot(Vec3 vec) {
		return x * vec.x + y * vec.y + z * vec.z;
	}

	/**
	 * Compute the cross product of this Vec3 and vec.
	 */
	public Vec3 cross(Vec3 vec) {
		return create(y * vec.z - z * vec.y, z * vec.x - x * vec.z, x * vec.y - y * vec.x);
	}

	/**
	 * Calculate the magnitude of the cross product of this Vec3 and vec.
	 */
	public double crossmag(Vec3 vec) {
		return Math.sqrt(Math.pow(y * vec.z - z * vec.y, 2) + Math.pow(z * vec.x - x * vec.z, 2)
				+ Math.pow(x * vec.y - y * vec.x, 2));
	}

	/**
	 * Compute the included angle between this Vec3 and vec, in radians.
	 */
	public double inc(Vec3 vec) {
		return Math.acos(dot(vec) / (mag() * vec.mag()));
	}

	/**
	 * Generate a copy of this Vec3 scaled by r.
	 */
	public Vec3 scale(double r) {
		Vec3 scalevec = create(x * r, y * r, z * r);
		// if mag was already calculated, scale that as well
		if (m > 0) scalevec.m = m * r;
		return scalevec;
	}

	/**
	 * Compute the magnitude of this Vec3.
	 */
	public double mag() {
		if (m < 0) m = Math.sqrt(x * x + y * y + z * z);
		return m;
	}

	/**
	 * Compute the inverse magnitude of this Vec3.
	 */
	public double invmag() {
		if (im < 0) im = 1d / mag();
		return im;
	}

	/**
	 * Get a copy of this Vec3 scaled to a magnitude of 1.
	 */
	public Vec3 unit() {
		if (unitvec == null) {
			unitvec = scale(invmag());
			// if we know the mag, already set by scale
		}
		return unitvec;
	}

	/** Get a copy of this Vec3, except set x == 0. */
	public Vec3 flattenX() {
		if (flatx == null) flatx = create(0, y, z);
		return flatx;
	}

	/** Get a copy of this Vec3, except set y == 0. */
	public Vec3 flattenY() {
		if (flaty == null) flaty = create(x, 0, z);
		return flaty;
	}

	/** Get a copy of this Vec3, except set z == 0. */
	public Vec3 flattenZ() {
		if (flatz == null) flatz = create(x, y, 0);
		return flatz;
	}

	/** Generate a copy of this Vec3 with x set to the specified value. */
	public Vec3 setX(double x) {
		return create(x, y, z);
	}

	/** Generate a copy of this Vec3 with y set to the specified value. */
	public Vec3 setY(double y) {
		return create(x, y, z);
	}

	/** Generate a copy of this Vec3 with z set to the specified value. */
	public Vec3 setZ(double z) {
		return create(x, y, z);
	}

	public double[][] to3Array() {
		return to3Array(new double[3][1]);
	}

	public double[][] to3Array(double[][] v) {
		v[0][0] = x;
		v[1][0] = y;
		v[2][0] = z;
		return v;
	}

	public double[][] to4ArrayPosition() {
		return to4ArrayPosition(new double[4][1]);
	}

	public double[][] to4ArrayPosition(double[][] v) {
		to3Array(v);
		v[3][0] = 1d;
		return v;
	}

	public double[][] to4ArrayNormal() {
		return to4ArrayNormal(new double[4][1]);
	}

	public double[][] to4ArrayNormal(double[][] v) {
		to3Array(v);
		v[3][0] = 0d;
		return v;
	}

	public double[][] toTranslationMatrix(double[][] m) {
		TransformationMatrix4D.translate(m, x, y, z);
		return m;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vec3) {
			Vec3 vec = (Vec3) obj;
			return (x == vec.x) && (y == vec.y) && (z == vec.z);
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("(%.4f, %.4f, %.4f)", x, y, z);
	}
}
