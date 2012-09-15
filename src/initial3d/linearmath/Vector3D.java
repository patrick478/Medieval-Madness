package initial3d.linearmath;

public final class Vector3D {

	private Vector3D() {
		throw new AssertionError();
	}

	/** Create a 3-vector. Purely convenience. */
	public static double[][] create() {
		return new double[3][1];
	}

	/** Create a 3-vector from double values. */
	public static double[][] create(double a, double b, double c) {
		double[][] v = new double[3][1];
		v[0][0] = a;
		v[1][0] = b;
		v[2][0] = c;
		return v;
	}
	
	/** Initialise a 3-vector as (1,0,0). */
	public static void i(double[][] target) {
		target[0][0] = 1;
		target[1][0] = 0;
		target[2][0] = 0;
	}
	
	/** Initialise a 3-vector as (0,1,0). */
	public static void j(double[][] target) {
		target[0][0] = 0;
		target[1][0] = 1;
		target[2][0] = 0;
	}
	
	/** Initialise a 3-vector as (0,0,1). */
	public static void k(double[][] target) {
		target[0][0] = 0;
		target[1][0] = 0;
		target[2][0] = 1;
	}

	/** Generate a String representation of a 3-vector. */
	public static String toString(double[][] source, int places_front, int places_behind) {
		String fmt = "%" + (places_front + places_behind + 1) + "." + places_behind + "f";
		return String.format(String.format("[%s, %s, %s]", fmt, fmt, fmt), source[0][0], source[1][0], source[2][0]);
	}

	/** Copy a 3-vector to another. */
	public static void copy(double[][] target, double[][] source) {
		target[0][0] = source[0][0];
		target[1][0] = source[1][0];
		target[2][0] = source[2][0];
	}

	/** Scale a 3-vector. Can be done in-place. */
	public static void scale(double[][] target, double[][] source, double factor) {
		target[0][0] = source[0][0] * factor;
		target[1][0] = source[1][0] * factor;
		target[2][0] = source[2][0] * factor;
	}

	/** Add two 3-vectors. Can be done in-place. */
	public static void add(double[][] target, double[][] right, double[][] left) {
		target[0][0] = left[0][0] + right[0][0];
		target[1][0] = left[1][0] + right[1][0];
		target[2][0] = left[2][0] + right[2][0];
	}

	/** Subtract right from left (as 3-vectors). Can be done in-place. */
	public static void subtract(double[][] target, double[][] right, double[][] left) {
		target[0][0] = left[0][0] - right[0][0];
		target[1][0] = left[1][0] - right[1][0];
		target[2][0] = left[2][0] - right[2][0];
	}

	/** Negate a 3-vector. Can be done in-place. */
	public static void negate(double[][] target, double[][] source) {
		target[0][0] = -source[0][0];
		target[1][0] = -source[1][0];
		target[2][0] = -source[2][0];
	}

	/** Compute the magnitude of a 3-vector. */
	public static double magnitude(double[][] source) {
		return Math.sqrt(Math.pow(source[0][0], 2) + Math.pow(source[1][0], 2) + Math.pow(source[2][0], 2));
	}

	/** Normalise a 3-vector (make a unit vector). Can be done in-place. */
	public static void normalise(double[][] target, double[][] source) {
		double im = 1d / magnitude(source);
		target[0][0] = source[0][0] * im;
		target[1][0] = source[1][0] * im;
		target[2][0] = source[2][0] * im;
	}

	/** Compute the dot product of two 3-vectors. */
	public static double dot(double[][] right, double[][] left) {
		return left[0][0] * right[0][0] + left[1][0] * right[1][0] + left[2][0] * right[2][0];
	}

	/** Compute the cross product of two 3-vectors. Cannot be done in-place. */
	public static void cross(double[][] target, double[][] right, double[][] left) {
		target[0][0] = left[1][0] * right[2][0] - left[2][0] * right[1][0];
		target[1][0] = left[2][0] * right[0][0] - left[0][0] * right[2][0];
		target[2][0] = left[0][0] * right[1][0] - left[1][0] * right[0][0];
	}

	/** Compute the included angle between two 3-vectors. */
	public static double includedAngle(double[][] right, double[][] left) {
		return Math.acos(dot(right, left) / (magnitude(left) * magnitude(right)));
	}

}