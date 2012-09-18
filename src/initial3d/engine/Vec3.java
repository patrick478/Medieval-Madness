package initial3d.engine;

import initial3d.linearmath.TransformationMatrix4D;

import java.util.Scanner;

/**
 * Vec3 represents an immutable 3-dimensional vector.
 * 
 * @author Ben Allen
 */

public final class Vec3 {

	public static final Vec3 zero = new Vec3(0, 0, 0);
	public static final Vec3 i = new Vec3(1, 0, 0);
	public static final Vec3 j = new Vec3(0, 1, 0);
	public static final Vec3 k = new Vec3(0, 0, 1);

	public final double x;
	public final double y;
	public final double z;

	// private cache

	// magnitude can never be < 0, so -1 means not initialised
	private double m = -1;
	private double im = -1;
	private Vec3 unitvec, negvec, flatx, flaty, flatz;

	/**
	 * Private constructor. Do not call this, use <code>create()</code> instead.
	 */
	private Vec3(double x_, double y_, double z_) {
		x = x_;
		y = y_;
		z = z_;
	}

	public static final Vec3 create(double x, double y, double z) {
		return new Vec3(x, y, z);
	}

	public static final Vec3 create(Scanner scan) {
		return create(scan.nextDouble(), scan.nextDouble(), scan.nextDouble());
	}

	public static final Vec3 create(double[][] v) {
		return create(v[0][0], v[1][0], v[2][0]);
	}

	public static final Vec3 createPlaneNorm(Vec3 p0, Vec3 p1, Vec3 p2) {
		return p1.sub(p0).cross(p2.sub(p1));
	}

	/**
	 * Compute the addition of this Vector3D and vec.
	 */
	public Vec3 add(Vec3 vec) {
		return create(x + vec.x, y + vec.y, z + vec.z);
	}

	/**
	 * Compute the subtraction of vec from this Vector3D.
	 */
	public Vec3 sub(Vec3 vec) {
		return create(x - vec.x, y - vec.y, z - vec.z);
	}

	/**
	 * Generate a copy of this Vector3D, but pointing in the opposite direction.
	 */
	public Vec3 neg() {
		if (negvec == null) negvec = create(-x, -y, -z);
		return negvec;
	}

	/**
	 * Compute the dot product of this Vector3D and vec.
	 */
	public double dot(Vec3 vec) {
		return x * vec.x + y * vec.y + z * vec.z;
	}

	/**
	 * Compute the cross product of this Vector3D and vec.
	 */
	public Vec3 cross(Vec3 vec) {
		return create(y * vec.z - z * vec.y, z * vec.x - x * vec.z, x * vec.y - y * vec.x);
	}

	/**
	 * Calculate the magnitude of the cross product of this Vector3D and vec.
	 */
	public double crossmag(Vec3 vec) {
		return Math.sqrt(Math.pow(y * vec.z - z * vec.y, 2) + Math.pow(z * vec.x - x * vec.z, 2)
				+ Math.pow(x * vec.y - y * vec.x, 2));
	}

	/**
	 * Compute the included angle between this Vector3D and vec, in radians.
	 */
	public double inc(Vec3 vec) {
		return Math.acos(dot(vec) / (mag() * vec.mag()));
	}

	/**
	 * Generate a copy of this Vector3D scaled by r.
	 */
	public Vec3 scale(double r) {
		return create(x * r, y * r, z * r);
	}

	/**
	 * Compute the magnitude of this Vector3D.
	 */
	public double mag() {
		if (m < 0) m = Math.sqrt(x * x + y * y + z * z);
		return m;
	}

	/**
	 * Compute the inverse magnitude of this Vector3D.
	 */
	public double invmag() {
		if (im < 0) im = 1d / mag();
		return im;
	}

	/**
	 * Generate a copy of this Vector3D scaled to a magnitude of 1.
	 */
	public Vec3 unit() {
		if (unitvec == null) unitvec = scale(invmag());
		return unitvec;
	}

	public Vec3 flattenX() {
		if (flatx == null) flatx = create(0, y, z);
		return flatx;
	}

	public Vec3 flattenY() {
		if (flaty == null) flaty = create(x, 0, z);
		return flaty;
	}

	public Vec3 flattenZ() {
		if (flatz == null) flatz = create(x, y, 0);
		return flatz;
	}

	public Vec3 setX(double x) {
		return create(x, y, z);
	}

	public Vec3 setY(double y) {
		return create(x, y, z);
	}

	public Vec3 setZ(double z) {
		return create(x, y, z);
	}

	public Vec3 delta(double dx, double dy, double dz) {
		return create(x + dx, y + dy, z + dz);
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
