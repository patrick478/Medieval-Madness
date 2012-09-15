package initial3d.engine;

import java.lang.reflect.Field;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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

	private static BlockingQueue<Vec3> reclaim = null;
	private static Field field_x, field_y, field_z, field_m, field_im, field_unitvec, field_negvec, field_flatx,
			field_flaty, field_flatz;

	// magnitude can never be < 0
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

	@Override
	protected void finalize() {
		// avoid null pointers without explicit sync
		// doesn't really matter if vec gets added to old reclaim queue
		BlockingQueue<Vec3> reclaimtarget = reclaim;
		if (reclaimtarget != null) reclaimtarget.offer(this);
	}

	/**
	 * Initialise the automatic reclamation of instances that would otherwise be
	 * garbage-collected so that <code>create()</code> can recycle them.
	 */
	public static final boolean initReclaim(int capacity) {
		try {
			field_x = Vec3.class.getDeclaredField("x");
			field_x.setAccessible(true);
			field_y = Vec3.class.getDeclaredField("y");
			field_y.setAccessible(true);
			field_z = Vec3.class.getDeclaredField("z");
			field_z.setAccessible(true);
			field_m = Vec3.class.getDeclaredField("m");
			field_m.setAccessible(true);
			field_im = Vec3.class.getDeclaredField("im");
			field_im.setAccessible(true);
			field_unitvec = Vec3.class.getDeclaredField("unitvec");
			field_unitvec.setAccessible(true);
			field_negvec = Vec3.class.getDeclaredField("negvec");
			field_negvec.setAccessible(true);
			field_flatx = Vec3.class.getDeclaredField("flatx");
			field_flatx.setAccessible(true);
			field_flaty = Vec3.class.getDeclaredField("flaty");
			field_flaty.setAccessible(true);
			field_flatz = Vec3.class.getDeclaredField("flatz");
			field_flatz.setAccessible(true);
			reclaim = new LinkedBlockingQueue<Vec3>(capacity);
			return true;
		} catch (Exception e) {
			reclaim = null;
		}
		return false;
	}

	public static final Vec3 create(double x, double y, double z) {
		Vec3 v = null;
		BlockingQueue<Vec3> reclaimfrom = reclaim;
		if (reclaimfrom != null && (v = reclaimfrom.poll()) != null) {
			try {
				field_x.setDouble(v, x);
				field_y.setDouble(v, y);
				field_z.setDouble(v, z);
				field_m.setDouble(v, -1);
				field_im.setDouble(v, -1);
				field_unitvec.set(v, null);
				field_negvec.set(v, null);
				field_flatx.set(v, null);
				field_flaty.set(v, null);
				field_flatz.set(v, null);
				return v;
			} catch (IllegalAccessException e) {
				// shouldn't happen
			}
		}
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
