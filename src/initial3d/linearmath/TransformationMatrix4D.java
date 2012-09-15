package initial3d.linearmath;

/**
 * Functions for initialising 4x4 transformation matrices (affine)
 * 
 * @author Ben Allen
 */
public final class TransformationMatrix4D {

	private TransformationMatrix4D() {
		throw new AssertionError();
	}

	/** Shear dimension t as a function of dimension s, using coef k. */
	public static void shear(double[][] target, int t_dim, int s_dim, double k) {
		Matrix.identity(target);
		target[t_dim][s_dim] = k;
	}
	
	/** Translate x, y, z, assuming w == 1. */
	public static void translate(double[][] target, double dx, double dy, double dz) {
		Matrix.identity(target);
		target[0][3] = dx;
		target[1][3] = dy;
		target[2][3] = dz;
	}

	/** Scale the x, y, z and w components of a 4-vector. */
	public static void scale(double[][] target, double scale_x, double scale_y, double scale_z, double scale_w) {
		Matrix.identity(target);
		target[0][0] = scale_x;
		target[1][1] = scale_y;
		target[2][2] = scale_z;
		target[3][3] = scale_w;
	}

	/** Rotate the y and z components of a 4-vector around the x-axis. */
	public static void rotateX(double[][] target, double theta) {
		Matrix.identity(target);
		target[1][1] = Math.cos(theta);
		target[1][2] = -Math.sin(theta);
		target[2][1] = Math.sin(theta);
		target[2][2] = Math.cos(theta);
	}

	/** Rotate the x and z components of a 4-vector around the y-axis. */
	public static void rotateY(double[][] target, double theta) {
		Matrix.identity(target);
		target[0][0] = Math.cos(theta);
		target[0][2] = Math.sin(theta);
		target[2][0] = -Math.sin(theta);
		target[2][2] = Math.cos(theta);
	}

	/** Rotate the x and y components of a 4-vector around the z-axis. */
	public static void rotateZ(double[][] target, double theta) {
		Matrix.identity(target);
		target[0][0] = Math.cos(theta);
		target[0][1] = -Math.sin(theta);
		target[1][0] = Math.sin(theta);
		target[1][1] = Math.cos(theta);
	}

}
