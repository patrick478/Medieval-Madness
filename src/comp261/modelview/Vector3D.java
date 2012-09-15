package comp261.modelview;

import java.util.Scanner;

/**
 * Vector3D represents a 3-dimensional vector.
 * 
 * @author Ben Allen
 */

public class Vector3D {
	
	public final double x;
	public final double y;
	public final double z;

	public static final Vector3D zero = new Vector3D(0, 0, 0);
	public static final Vector3D i = new Vector3D(1, 0, 0);
	public static final Vector3D j = new Vector3D(0, 1, 0);
	public static final Vector3D k = new Vector3D(0, 0, 1);

	public Vector3D(double x_, double y_, double z_) {
		x = x_;
		y = y_;
		z = z_;
	}
	
	public Vector3D(Scanner scan) {
		x = scan.nextDouble();
		y = scan.nextDouble();
		z = scan.nextDouble();
	}

	/** Compute the addition of this Vector3D and vec. */
	public Vector3D add(Vector3D vec) {
		return new Vector3D(x + vec.x, y + vec.y, z + vec.z);
	}

	/**
	 * Generate a copy of this Vector3D, but pointing in the opposite direction.
	 */
	public Vector3D negate() {
		return new Vector3D(-x, -y, -z);
	}

	/** Compute the dot product of this Vector3D and vec. */
	public double dot(Vector3D vec) {
		return x * vec.x + y * vec.y + z * vec.z;
	}

	/**
	 * Compute the cross product of this Vector3D and vec as another Vector3D.
	 */
	public Vector3D cross(Vector3D vec) {
		return new Vector3D(y * vec.z - z * vec.y, z * vec.x - x * vec.z, x * vec.y - y * vec.x);
	}

	/**
	 * Calculate the magnitude of the cross product of this Vector3D and vec.
	 */
	public double crossMagnitude(Vector3D vec) {
		return Math.sqrt(Math.pow(y * vec.z - z * vec.y, 2) + Math.pow(z * vec.x - x * vec.z, 2)
				+ Math.pow(x * vec.y - y * vec.x, 2));
	}

	/**
	 * Compute the included angle between this Vector3D and vec, in radians.
	 */
	public double incAngle(Vector3D vec) {
		return Math.acos(dot(vec) / (magnitude() * vec.magnitude()));
	}

	/** Generate a copy of this Vector3D scaled by r. */
	public Vector3D scale(double r) {
		return new Vector3D(x * r, y * r, z * r);
	}

	/** Compute the magnitude of this Vector3D. */
	public double magnitude() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	/** Generate a copy of this Vector3D scaled to a magnitude of 1. */
	public Vector3D unit() {
		double im = 1d / magnitude();
		return new Vector3D(x * im, y * im, z * im);
	}

	public boolean equals(Object obj) {
		if (obj instanceof Vector3D) {
			Vector3D vec = (Vector3D) obj;
			return (x == vec.x) && (y == vec.y) && (z == vec.z);
		}
		return false;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public String toString() {
		return String.format("(%.4f, %.4f, %.4f)", x, y, z);
	}
}
