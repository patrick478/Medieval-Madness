package initial3d.renderer;

import java.nio.ByteBuffer;

import java.lang.reflect.Field;
import sun.misc.Unsafe;

/**
 * Initial3D internal utility method class. This delicacy goes very well with a dash of <code>import static</code> and a
 * generous seasoning of <code>sun.misc.Unsafe</code>.
 * 
 * @author Ben Allen
 */
@SuppressWarnings("restriction")
public final class Util {

	private static final Unsafe unsafe;

	static {
		Unsafe u = null;
		try {
			Field field = Unsafe.class.getDeclaredField("theUnsafe");
			field.setAccessible(true);
			u = (Unsafe) field.get(null);
		} catch (Exception e) {
			throw new AssertionError(e);
		}
		unsafe = u;
	}

	/** This class is not instantiable. */
	private Util() {
		throw new AssertionError("You're doing it wrong.");
	}

	/** Get the <code>sun.misc.Unsafe</code> singleton. */
	public static final Unsafe getUnsafe() {
		return unsafe;
	}

	/**
	 * Fast approximation for inverse square root of a positive float. From <a
	 * href=http://en.wikipedia.org/wiki/Fast_inverse_square_root>Wikipedia</a>.
	 */
	public static final float fastInverseSqrt(float number) {
		float x2 = number * 0.5f;
		// evil floating point bit level hacking
		number = Float.intBitsToFloat(0x5f3759df - (Float.floatToRawIntBits(number) >>> 1));
		// 1st iteration of newton's method
		number = number * (1.5f - (x2 * number * number));
		return number;
	}

	/**
	 * Fast approximation for inverse of a positive float. Identical to <code>fastInverseSqrt()</code>, except the
	 * operand is squared.
	 */
	public static final float fastInverse(float number) {
		number *= number;
		float x2 = number * 0.5f;
		// evil floating point bit level hacking
		number = Float.intBitsToFloat(0x5f3759df - (Float.floatToRawIntBits(number) >>> 1));
		// 1st iteration of newton's method
		number = number * (1.5f - (x2 * number * number));
		return number;
	}

	public static final double clamp(double value, double lower, double upper) {
		return value < lower ? lower : value > upper ? upper : value;
	}

	public static final float clamp(float value, float lower, float upper) {
		return value < lower ? lower : value > upper ? upper : value;
	}

	public static final long clamp(long value, long lower, long upper) {
		return value < lower ? lower : value > upper ? upper : value;
	}

	public static final int clamp(int value, int lower, int upper) {
		return value < lower ? lower : value > upper ? upper : value;
	}

	/** Wrapper for <code>System.currentTimeMillis()</code>. */
	public static final long time() {
		return System.currentTimeMillis();
	}

	/** Wrapper for <code>System.nanoTime()</code>. */
	public static final long timenanos() {
		return System.nanoTime();
	}

	/** Wrapper for <code>System.out.printf()</code>. */
	public static final void printf(String fmt, Object... args) {
		System.out.printf(fmt, args);
	}

	/** Wrapper for <code>String.format()</code>. */
	public static final String sprintf(String fmt, Object... args) {
		return String.format(fmt, args);
	}

	/** Wrapper for <code>System.out.println()</code>. */
	public static final void puts(String s) {
		System.out.println(s);
	}

	/** Wrapper for <code>Thread.sleep()</code> that suppresses InterruptedException. */
	public static final void pause(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// do nothing
		}
	}

	public static final void writeVector(Unsafe unsafe, long pTarget, double x, double y, double z, double w) {
		unsafe.putDouble(pTarget, x);
		unsafe.putDouble(pTarget + 8, y);
		unsafe.putDouble(pTarget + 16, z);
		unsafe.putDouble(pTarget + 24, w);
	}

	public static final double vectorDot(Unsafe unsafe, long pVec, double cx, double cy, double cz) {
		double dot = unsafe.getDouble(pVec) * cx;
		dot += unsafe.getDouble(pVec + 8) * cy;
		dot += unsafe.getDouble(pVec + 16) * cz;
		return dot;
	}

	public static final double vectorDot(Unsafe unsafe, long pVecA, long pVecB) {
		double dot = unsafe.getDouble(pVecA) * unsafe.getDouble(pVecB);
		dot += unsafe.getDouble(pVecA + 8) * unsafe.getDouble(pVecB + 8);
		dot += unsafe.getDouble(pVecA + 16) * unsafe.getDouble(pVecB + 16);
		return dot;
	}

	public static final void vectorPlaneNorm(Unsafe unsafe, long pTarget, long pVec0, long pVec1, long pVec2) {
		double dx01 = unsafe.getDouble(pVec1) - unsafe.getDouble(pVec0);
		double dy01 = unsafe.getDouble(pVec1 + 8) - unsafe.getDouble(pVec0 + 8);
		double dz01 = unsafe.getDouble(pVec1 + 16) - unsafe.getDouble(pVec0 + 16);
		double dx12 = unsafe.getDouble(pVec2) - unsafe.getDouble(pVec1);
		double dy12 = unsafe.getDouble(pVec2 + 8) - unsafe.getDouble(pVec1 + 8);
		double dz12 = unsafe.getDouble(pVec2 + 16) - unsafe.getDouble(pVec1 + 16);
		// now do d01 cross d12
		unsafe.putDouble(pTarget, dy01 * dz12 - dz01 * dy12);
		unsafe.putDouble(pTarget + 8, dz01 * dx12 - dx01 * dz12);
		unsafe.putDouble(pTarget + 16, dx01 * dy12 - dy01 * dx12);
		unsafe.putDouble(pTarget + 24, 1);
	}

	public static final void vectorCross(Unsafe unsafe, long pTarget, long pVec0, long pVec1) {
		double x0 = unsafe.getDouble(pVec0);
		double y0 = unsafe.getDouble(pVec0 + 8);
		double z0 = unsafe.getDouble(pVec0 + 16);
		double x1 = unsafe.getDouble(pVec1);
		double y1 = unsafe.getDouble(pVec1 + 8);
		double z1 = unsafe.getDouble(pVec1 + 16);
		unsafe.putDouble(pTarget, y0 * z1 - z0 * y1);
		unsafe.putDouble(pTarget + 8, z0 * x1 - x0 * z1);
		unsafe.putDouble(pTarget + 16, x0 * y1 - y0 * x1);
		unsafe.putDouble(pTarget + 24, 1);
	}

	public static final void computeProjectionClipFunc(Unsafe unsafe, long pBase, long pOut, double x0, double y0,
			double z0, double x1, double y1, double z1, double x2, double y2, double z2) {
		final long pX_V0_in = 0x000B6910 + pBase;
		final long pX_V1_in = pX_V0_in + 32;
		final long pX_V2_in = pX_V1_in + 32;
		final long pX_V0_out = pX_V2_in + 32;
		final long pX_V1_out = pX_V0_out + 32;
		final long pX_V2_out = pX_V1_out + 32;

		writeVector(unsafe, pX_V0_in, x0, y0, z0, 1);
		writeVector(unsafe, pX_V1_in, x1, y1, z1, 1);
		writeVector(unsafe, pX_V2_in, x2, y2, z2, 1);

		// use inverse projection transform
		multiply4VectorBlock_pos_unsafe(unsafe, pX_V0_out, 3, pX_V0_in, pBase + 0x00080D00);

		// calculate view space normal, doesn't need normalising
		vectorPlaneNorm(unsafe, pOut, pX_V0_out, pX_V1_out, pX_V2_out);

		// eval clip func at one of the points specified
		double cutoff = vectorDot(unsafe, pX_V0_out, pOut);

		unsafe.putDouble(pOut + 24, cutoff);

	}

	public static final void copyMatrixFrom2D(ByteBuffer mem, int qdTarget, double[][] source, int rows, int cols) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				mem.putDouble(qdTarget + (cols * i + j) * 8, source[i][j]);
			}
		}
	}

	public static final void copyMatrixFrom2D_unsafe(Unsafe unsafe, long pdTarget, double[][] source, int rows, int cols) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				unsafe.putDouble(pdTarget + (cols * i + j) * 8, source[i][j]);
			}
		}
	}

	public static final void copyMatrixTo2D(ByteBuffer mem, double[][] target, int qdSource, int rows, int cols) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				target[i][j] = mem.getDouble(qdSource + (cols * i + j) * 8);
			}
		}
	}

	public static final void copyMatrixTo2D_unsafe(Unsafe unsafe, double[][] target, long pdSource, int rows, int cols) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				target[i][j] = unsafe.getDouble(pdSource + (cols * i + j) * 8);
			}
		}
	}

	public static final void copy4VectorBlock(ByteBuffer mem_t, ByteBuffer mem_s, int qdTarget, int qdSource, int size) {
		final int end = qdSource + (size << 5);
		while (qdSource < end) {
			mem_t.putDouble(qdTarget, mem_s.getDouble(qdSource));
			mem_t.putDouble(qdTarget += 8, mem_s.getDouble(qdSource += 8));
			mem_t.putDouble(qdTarget += 8, mem_s.getDouble(qdSource += 8));
			mem_t.putDouble(qdTarget += 8, mem_s.getDouble(qdSource += 8));
			qdTarget += 8;
			qdSource += 8;
		}
	}

	public static final void copy4VectorBlock_unsafe(Unsafe unsafe, long pdTarget, long pdSource, int size) {
		unsafe.copyMemory(pdSource, pdTarget, size * 32);
	}

	/**
	 * Homogenise a block of 4D vectors.
	 * 
	 * @param size
	 *            number of vectors in block
	 */
	public static final void homogenise4VectorBlock(ByteBuffer mem_t, ByteBuffer mem_s, int qdTarget, int qdSource,
			int size) {
		final int end = qdSource + (size << 5);
		while (qdSource < end) {
			// read vector
			double s0 = mem_s.getDouble(qdSource);
			double s1 = mem_s.getDouble(qdSource += 8);
			double s2 = mem_s.getDouble(qdSource += 8);
			double is3 = 1d / mem_s.getDouble(qdSource += 8);

			// write homogenised vector to target
			mem_t.putDouble(qdTarget, s0 * is3);
			mem_t.putDouble(qdTarget += 8, s1 * is3);
			mem_t.putDouble(qdTarget += 8, s2 * is3);
			mem_t.putDouble(qdTarget += 8, 1d);

			qdSource += 8;
			qdTarget += 8;
		}
	}

	/**
	 * Homogenise a block of 4D vectors in a <code>sun.misc.Unsafe</code> manner.
	 * 
	 * @param size
	 *            number of vectors in block
	 */
	public static final void homogenise4VectorBlock_unsafe(Unsafe unsafe, long pdTarget, long pdSource, int size) {
		final long end = pdSource + (size << 5);
		while (pdSource < end) {
			// read vector
			double s0 = unsafe.getDouble(pdSource);
			double s1 = unsafe.getDouble(pdSource += 8);
			double s2 = unsafe.getDouble(pdSource += 8);
			double is3 = 1d / unsafe.getDouble(pdSource += 8);

			// write homogenised vector to target
			unsafe.putDouble(pdTarget, s0 * is3);
			unsafe.putDouble(pdTarget += 8, s1 * is3);
			unsafe.putDouble(pdTarget += 8, s2 * is3);
			unsafe.putDouble(pdTarget += 8, 1d);

			pdSource += 8;
			pdTarget += 8;
		}
	}

	/**
	 * Normalise a block of 4D vectors in a <code>sun.misc.Unsafe</code> manner.
	 * 
	 * @param size
	 *            number of vectors in block
	 */
	public static final void normalise4VectorBlock_unsafe(Unsafe unsafe, long pdTarget, long pdSource, int size) {
		final long end = pdSource + (size << 5);
		while (pdSource < end) {
			// read vector
			double s0 = unsafe.getDouble(pdSource);
			double s1 = unsafe.getDouble(pdSource += 8);
			double s2 = unsafe.getDouble(pdSource += 8);

			double im = 1d / Math.sqrt(s0 * s0 + s1 * s1 + s2 * s2);

			// write normalised vector to target
			unsafe.putDouble(pdTarget, s0 * im);
			unsafe.putDouble(pdTarget += 8, s1 * im);
			unsafe.putDouble(pdTarget += 8, s2 * im);
			unsafe.putDouble(pdTarget += 8, 0d);

			pdSource += 16;
			pdTarget += 8;
		}
	}

	/**
	 * Left-multiply a block of position vectors by a transformation matrix and homogenise them.
	 * 
	 * @param rsize
	 *            number of vectors in block
	 */
	public static final void multiply4VectorBlock_pos(ByteBuffer mem_t, ByteBuffer mem_r, ByteBuffer mem_l,
			int qdTarget, int rsize, int qdRight, int qdLeft) {
		// read in xform
		final double x00 = mem_l.getDouble(qdLeft);
		final double x01 = mem_l.getDouble(qdLeft += 8);
		final double x02 = mem_l.getDouble(qdLeft += 8);
		final double x03 = mem_l.getDouble(qdLeft += 8);

		final double x10 = mem_l.getDouble(qdLeft += 8);
		final double x11 = mem_l.getDouble(qdLeft += 8);
		final double x12 = mem_l.getDouble(qdLeft += 8);
		final double x13 = mem_l.getDouble(qdLeft += 8);

		final double x20 = mem_l.getDouble(qdLeft += 8);
		final double x21 = mem_l.getDouble(qdLeft += 8);
		final double x22 = mem_l.getDouble(qdLeft += 8);
		final double x23 = mem_l.getDouble(qdLeft += 8);

		final double x30 = mem_l.getDouble(qdLeft += 8);
		final double x31 = mem_l.getDouble(qdLeft += 8);
		final double x32 = mem_l.getDouble(qdLeft += 8);
		final double x33 = mem_l.getDouble(qdLeft += 8);

		final int rend = qdRight + (rsize << 5);

		// xform and homogenise
		while (qdRight < rend) {
			// read vector to apply xform to
			double r0 = mem_r.getDouble(qdRight);
			double r1 = mem_r.getDouble(qdRight += 8);
			double r2 = mem_r.getDouble(qdRight += 8);
			// assume r3 == 1

			double it3 = 1d / (x30 * r0 + x31 * r1 + x32 * r2 + x33);

			// xform
			mem_t.putDouble(qdTarget, (x00 * r0 + x01 * r1 + x02 * r2 + x03) * it3);
			mem_t.putDouble(qdTarget += 8, (x10 * r0 + x11 * r1 + x12 * r2 + x13) * it3);
			mem_t.putDouble(qdTarget += 8, (x20 * r0 + x21 * r1 + x22 * r2 + x23) * it3);
			mem_t.putDouble(qdTarget += 8, 1d);

			qdRight += 16;
			qdTarget += 8;
		}
	}

	/**
	 * Left-multiply a block of normal vectors by a transformation matrix and normalise them.
	 * 
	 * @param rsize
	 *            number of vectors in block
	 */
	public static final void multiply4VectorBlock_norm(ByteBuffer mem_t, ByteBuffer mem_r, ByteBuffer mem_l,
			int qdTarget, int rsize, int qdRight, int qdLeft) {
		// read in xform (don't actually need all of it)
		final double x00 = mem_l.getDouble(qdLeft);
		final double x01 = mem_l.getDouble(qdLeft += 8);
		final double x02 = mem_l.getDouble(qdLeft += 8);
		// final double x03 = mem_l.getDouble(qdLeft += 8);
		qdLeft += 8;

		final double x10 = mem_l.getDouble(qdLeft += 8);
		final double x11 = mem_l.getDouble(qdLeft += 8);
		final double x12 = mem_l.getDouble(qdLeft += 8);
		// final double x13 = mem_l.getDouble(qdLeft += 8);
		qdLeft += 8;

		final double x20 = mem_l.getDouble(qdLeft += 8);
		final double x21 = mem_l.getDouble(qdLeft += 8);
		final double x22 = mem_l.getDouble(qdLeft += 8);
		// final double x23 = mem_l.getDouble(qdLeft += 8);

		// final double x30 = mem_l.getDouble(qdLeft += 8);
		// final double x31 = mem_l.getDouble(qdLeft += 8);
		// final double x32 = mem_l.getDouble(qdLeft += 8);
		// final double x33 = mem_l.getDouble(qdLeft += 8);

		final int rend = qdRight + (rsize << 5);

		// xform and normalise
		while (qdRight < rend) {
			// read vector to apply xform to
			double r0 = mem_r.getDouble(qdRight);
			double r1 = mem_r.getDouble(qdRight += 8);
			double r2 = mem_r.getDouble(qdRight += 8);
			// assume r3 == 0

			// System.out.printf("in  %.3f, %.3f, %.3f\n", r0, r1, r2);

			// xform
			double t0 = x00 * r0 + x01 * r1 + x02 * r2;
			double t1 = x10 * r0 + x11 * r1 + x12 * r2;
			double t2 = x20 * r0 + x21 * r1 + x22 * r2;

			// TODO possibly use double precision version of fastInvSqrt() here
			double im = 1d / Math.sqrt(t0 * t0 + t1 * t1 + t2 * t2);

			mem_t.putDouble(qdTarget, t0 * im);
			mem_t.putDouble(qdTarget += 8, t1 * im);
			mem_t.putDouble(qdTarget += 8, t2 * im);
			mem_t.putDouble(qdTarget += 8, 0d);

			// System.out.printf("out %.3f, %.3f, %.3f\n", t0 * im, t1 * im, t2 * im);

			qdRight += 16;
			qdTarget += 8;
		}
	}

	/**
	 * Left-multiply a block of 4D vectors by a transformation matrix, optionally homogenising them.
	 * 
	 * @param rsize
	 *            number of vectors in block
	 */
	public static final void multiply4VectorBlock(ByteBuffer mem, int qdTarget, int rsize, int qdRight, int qdLeft,
			boolean homogenise) {
		// read in xform
		final double x00 = mem.getDouble(qdLeft);
		final double x01 = mem.getDouble(qdLeft += 8);
		final double x02 = mem.getDouble(qdLeft += 8);
		final double x03 = mem.getDouble(qdLeft += 8);

		final double x10 = mem.getDouble(qdLeft += 8);
		final double x11 = mem.getDouble(qdLeft += 8);
		final double x12 = mem.getDouble(qdLeft += 8);
		final double x13 = mem.getDouble(qdLeft += 8);

		final double x20 = mem.getDouble(qdLeft += 8);
		final double x21 = mem.getDouble(qdLeft += 8);
		final double x22 = mem.getDouble(qdLeft += 8);
		final double x23 = mem.getDouble(qdLeft += 8);

		final double x30 = mem.getDouble(qdLeft += 8);
		final double x31 = mem.getDouble(qdLeft += 8);
		final double x32 = mem.getDouble(qdLeft += 8);
		final double x33 = mem.getDouble(qdLeft += 8);

		final int rend = qdRight + (rsize << 5);

		if (!homogenise) {
			while (qdRight < rend) {
				// read vector to apply xform to
				double r0 = mem.getDouble(qdRight);
				double r1 = mem.getDouble(qdRight += 8);
				double r2 = mem.getDouble(qdRight += 8);
				double r3 = mem.getDouble(qdRight += 8);

				// xform
				mem.putDouble(qdTarget, x00 * r0 + x01 * r1 + x02 * r2 + x03 * r3);
				mem.putDouble(qdTarget += 8, x10 * r0 + x11 * r1 + x12 * r2 + x13 * r3);
				mem.putDouble(qdTarget += 8, x20 * r0 + x21 * r1 + x22 * r2 + x23 * r3);
				mem.putDouble(qdTarget += 8, x30 * r0 + x31 * r1 + x32 * r2 + x33 * r3);

				qdRight += 8;
				qdTarget += 8;
			}
			return;
		}
		// homogenise
		while (qdRight < rend) {
			// read vector to apply xform to
			double r0 = mem.getDouble(qdRight);
			double r1 = mem.getDouble(qdRight += 8);
			double r2 = mem.getDouble(qdRight += 8);
			double r3 = mem.getDouble(qdRight += 8);

			double it3 = 1d / x30 * r0 + x31 * r1 + x32 * r2 + x33 * r3;

			// xform
			mem.putDouble(qdTarget, (x00 * r0 + x01 * r1 + x02 * r2 + x03 * r3) * it3);
			mem.putDouble(qdTarget += 8, (x10 * r0 + x11 * r1 + x12 * r2 + x13 * r3) * it3);
			mem.putDouble(qdTarget += 8, (x20 * r0 + x21 * r1 + x22 * r2 + x23 * r3) * it3);
			mem.putDouble(qdTarget += 8, 1d);

			qdRight += 8;
			qdTarget += 8;
		}
	}

	/**
	 * Left-multiply a block of position vectors by a transformation matrix and homogenise them in a
	 * <code>sun.misc.Unsafe</code> manner.
	 * 
	 * @param rsize
	 *            number of vectors in block
	 */
	public static final void multiply4VectorBlock_pos_unsafe(Unsafe unsafe, long pdTarget, long rsize, long pdRight,
			long pdLeft) {
		// read in xform
		final double x00 = unsafe.getDouble(pdLeft);
		final double x01 = unsafe.getDouble(pdLeft += 8);
		final double x02 = unsafe.getDouble(pdLeft += 8);
		final double x03 = unsafe.getDouble(pdLeft += 8);

		final double x10 = unsafe.getDouble(pdLeft += 8);
		final double x11 = unsafe.getDouble(pdLeft += 8);
		final double x12 = unsafe.getDouble(pdLeft += 8);
		final double x13 = unsafe.getDouble(pdLeft += 8);

		final double x20 = unsafe.getDouble(pdLeft += 8);
		final double x21 = unsafe.getDouble(pdLeft += 8);
		final double x22 = unsafe.getDouble(pdLeft += 8);
		final double x23 = unsafe.getDouble(pdLeft += 8);

		final double x30 = unsafe.getDouble(pdLeft += 8);
		final double x31 = unsafe.getDouble(pdLeft += 8);
		final double x32 = unsafe.getDouble(pdLeft += 8);
		final double x33 = unsafe.getDouble(pdLeft += 8);

		final long rend = pdRight + (rsize << 5);

		// xform and homogenise
		while (pdRight < rend) {
			// read vector to apply xform to
			double r0 = unsafe.getDouble(pdRight);
			double r1 = unsafe.getDouble(pdRight += 8);
			double r2 = unsafe.getDouble(pdRight += 8);
			// assume r3 == 1

			double it3 = 1d / (x30 * r0 + x31 * r1 + x32 * r2 + x33);

			// xform
			unsafe.putDouble(pdTarget, (x00 * r0 + x01 * r1 + x02 * r2 + x03) * it3);
			unsafe.putDouble(pdTarget += 8, (x10 * r0 + x11 * r1 + x12 * r2 + x13) * it3);
			unsafe.putDouble(pdTarget += 8, (x20 * r0 + x21 * r1 + x22 * r2 + x23) * it3);
			unsafe.putDouble(pdTarget += 8, 1d);

			pdRight += 16;
			pdTarget += 8;
		}
	}

	/**
	 * Left-multiply a block of normal vectors by a transformation matrix and normalise them in a
	 * <code>sun.misc.Unsafe</code> manner.
	 * 
	 * @param rsize
	 *            number of vectors in block
	 */
	public static final void multiply4VectorBlock_norm_unsafe(Unsafe unsafe, long pdTarget, long rsize, long pdRight,
			long pdLeft) {
		// read in xform (don't actually need all of it)
		final double x00 = unsafe.getDouble(pdLeft);
		final double x01 = unsafe.getDouble(pdLeft += 8);
		final double x02 = unsafe.getDouble(pdLeft += 8);
		// final double x03 = mem_l.getDouble(qdLeft += 8);
		pdLeft += 8;

		final double x10 = unsafe.getDouble(pdLeft += 8);
		final double x11 = unsafe.getDouble(pdLeft += 8);
		final double x12 = unsafe.getDouble(pdLeft += 8);
		// final double x13 = mem_l.getDouble(qdLeft += 8);
		pdLeft += 8;

		final double x20 = unsafe.getDouble(pdLeft += 8);
		final double x21 = unsafe.getDouble(pdLeft += 8);
		final double x22 = unsafe.getDouble(pdLeft += 8);
		// final double x23 = mem_l.getDouble(qdLeft += 8);

		// final double x30 = mem_l.getDouble(qdLeft += 8);
		// final double x31 = mem_l.getDouble(qdLeft += 8);
		// final double x32 = mem_l.getDouble(qdLeft += 8);
		// final double x33 = mem_l.getDouble(qdLeft += 8);

		final long rend = pdRight + (rsize << 5);

		// xform and normalise
		while (pdRight < rend) {
			// read vector to apply xform to
			double r0 = unsafe.getDouble(pdRight);
			double r1 = unsafe.getDouble(pdRight += 8);
			double r2 = unsafe.getDouble(pdRight += 8);
			// assume r3 == 0

			// System.out.printf("in  %.3f, %.3f, %.3f\n", r0, r1, r2);

			// xform
			double t0 = x00 * r0 + x01 * r1 + x02 * r2;
			double t1 = x10 * r0 + x11 * r1 + x12 * r2;
			double t2 = x20 * r0 + x21 * r1 + x22 * r2;

			// TODO possibly use double precision version of fastInvSqrt() here
			double im = 1d / Math.sqrt(t0 * t0 + t1 * t1 + t2 * t2);

			unsafe.putDouble(pdTarget, t0 * im);
			unsafe.putDouble(pdTarget += 8, t1 * im);
			unsafe.putDouble(pdTarget += 8, t2 * im);
			unsafe.putDouble(pdTarget += 8, 0d);

			// System.out.printf("out %.3f, %.3f, %.3f\n", t0 * im, t1 * im, t2 * im);

			pdRight += 16;
			pdTarget += 8;
		}
	}

	/**
	 * Left-multiply a block of 4D vectors by a transformation matrix in a <code>sun.misc.Unsafe</code> manner,
	 * optionally homogenising them.
	 * 
	 * @param rsize
	 *            number of vectors in block
	 */
	public static final void multiply4VectorBlock_unsafe(Unsafe unsafe, long pdTarget, long rsize, long pdRight,
			long pdLeft, boolean homogenise) {
		// read in xform
		final double x00 = unsafe.getDouble(pdLeft);
		final double x01 = unsafe.getDouble(pdLeft += 8);
		final double x02 = unsafe.getDouble(pdLeft += 8);
		final double x03 = unsafe.getDouble(pdLeft += 8);

		final double x10 = unsafe.getDouble(pdLeft += 8);
		final double x11 = unsafe.getDouble(pdLeft += 8);
		final double x12 = unsafe.getDouble(pdLeft += 8);
		final double x13 = unsafe.getDouble(pdLeft += 8);

		final double x20 = unsafe.getDouble(pdLeft += 8);
		final double x21 = unsafe.getDouble(pdLeft += 8);
		final double x22 = unsafe.getDouble(pdLeft += 8);
		final double x23 = unsafe.getDouble(pdLeft += 8);

		final double x30 = unsafe.getDouble(pdLeft += 8);
		final double x31 = unsafe.getDouble(pdLeft += 8);
		final double x32 = unsafe.getDouble(pdLeft += 8);
		final double x33 = unsafe.getDouble(pdLeft += 8);

		final long rend = pdRight + (rsize << 5);

		if (!homogenise) {
			while (pdRight < rend) {
				// read vector to apply xform to
				double r0 = unsafe.getDouble(pdRight);
				double r1 = unsafe.getDouble(pdRight += 8);
				double r2 = unsafe.getDouble(pdRight += 8);
				double r3 = unsafe.getDouble(pdRight += 8);

				// xform
				unsafe.putDouble(pdTarget, x00 * r0 + x01 * r1 + x02 * r2 + x03 * r3);
				unsafe.putDouble(pdTarget += 8, x10 * r0 + x11 * r1 + x12 * r2 + x13 * r3);
				unsafe.putDouble(pdTarget += 8, x20 * r0 + x21 * r1 + x22 * r2 + x23 * r3);
				unsafe.putDouble(pdTarget += 8, x30 * r0 + x31 * r1 + x32 * r2 + x33 * r3);

				pdRight += 8;
				pdTarget += 8;
			}
			return;
		}
		// homogenise
		while (pdRight < rend) {
			// read vector to apply xform to
			double r0 = unsafe.getDouble(pdRight);
			double r1 = unsafe.getDouble(pdRight += 8);
			double r2 = unsafe.getDouble(pdRight += 8);
			double r3 = unsafe.getDouble(pdRight += 8);

			double it3 = 1d / x30 * r0 + x31 * r1 + x32 * r2 + x33 * r3;

			// xform
			unsafe.putDouble(pdTarget, (x00 * r0 + x01 * r1 + x02 * r2 + x03 * r3) * it3);
			unsafe.putDouble(pdTarget += 8, (x10 * r0 + x11 * r1 + x12 * r2 + x13 * r3) * it3);
			unsafe.putDouble(pdTarget += 8, (x20 * r0 + x21 * r1 + x22 * r2 + x23 * r3) * it3);
			unsafe.putDouble(pdTarget += 8, 1d);

			pdRight += 8;
			pdTarget += 8;
		}
	}

}
