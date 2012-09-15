package initial3d.linearmath;

public class Vector4D {

	private Vector4D() {
		throw new AssertionError();
	}

	/** Create a 4-vector. Purely convenience. */
	public static double[][] create() {
		return new double[4][1];
	}

	/** Create a 4-vector from double values. */
	public static double[][] create(double a, double b, double c, double d) {
		double[][] v = new double[4][1];
		v[0][0] = a;
		v[1][0] = b;
		v[2][0] = c;
		v[3][0] = d;
		return v;
	}

	/** Generate a String representation of a 4-vector. */
	public static String toString(double[][] source, int places_front, int places_behind) {
		String fmt = "%" + (places_front + places_behind + 1) + "." + places_behind + "f";
		return String.format(String.format("[%s, %s, %s, %s]", fmt, fmt, fmt, fmt), source[0][0], source[1][0], source[2][0], source[3][0]);
	}

	/** Copy a 4-vector to another. */
	public static void copy(double[][] target, double[][] source) {
		target[0][0] = source[0][0];
		target[1][0] = source[1][0];
		target[2][0] = source[2][0];
		target[3][0] = source[3][0];
	}

	/**
	 * Scale a 4-vector by 1 / its homogeneous component. Can be done
	 * in-place.
	 */
	public static void homogenise(double[][] target, double[][] source) {
		double d = 1d / source[3][0];
		target[0][0] = source[0][0] * d;
		target[1][0] = source[1][0] * d;
		target[2][0] = source[2][0] * d;
		target[3][0] = 1d;
	}

	/** Scale a 4-vector. Can be done in-place. */
	public static void scale(double[][] target, double[][] source,
			double factor) {
		target[0][0] = source[0][0] * factor;
		target[1][0] = source[1][0] * factor;
		target[2][0] = source[2][0] * factor;
		target[3][0] = source[3][0] * factor;
	}

	/** Add two 4-vectors. Can be done in-place. */
	public static void add(double[][] target, double[][] right,
			double[][] left) {
		target[0][0] = left[0][0] + right[0][0];
		target[1][0] = left[1][0] + right[1][0];
		target[2][0] = left[2][0] + right[2][0];
		target[3][0] = left[3][0] + right[3][0];
	}

	/** Subtract right from left (as 4-vectors). Can be done in-place. */
	public static void subtract(double[][] target, double[][] right,
			double[][] left) {
		target[0][0] = left[0][0] - right[0][0];
		target[1][0] = left[1][0] - right[1][0];
		target[2][0] = left[2][0] - right[2][0];
		target[3][0] = left[3][0] - right[3][0];
	}

	/** Negate a 4-vector. Can be done in-place. */
	public static void negate(double[][] target, double[][] source) {
		target[0][0] = -source[0][0];
		target[1][0] = -source[1][0];
		target[2][0] = -source[2][0];
		target[3][0] = -source[3][0];
	}

	/** Compute the magnitude of a 4-vector. */
	public static double magnitude(double[][] source) {
		return Math.sqrt(Math.pow(source[0][0], 2)
				+ Math.pow(source[1][0], 2) + Math.pow(source[2][0], 2)
				+ Math.pow(source[3][0], 2));
	}
}