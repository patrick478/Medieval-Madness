package initial3d.linearmath;

import java.io.PrintStream;

/**
 * Library of functions for manipulating arrays as matrices. Uses arrays of double[rows][cols]. Fairly basic. As few
 * checks as possible are performed for the sake of speed. If the target matrix of any operation is too big, no error
 * will be thrown. If the target is too small, all operations will error. No null checks are performed. If an exception
 * is thrown, the state of the target array is not defined. If a method requires a temp array, it must be at least as
 * big as the minimum size of the target array. Methods whose name ends in NT (no temp, i.e. they don't allow you to
 * specify a temporary array) automatically allocate a new temp array at each call, so don't call them frequently;
 * instead recycle your temp arrays.
 * 
 * WATCH THE ORDER OF YOUR ARGUMENTS.
 * 
 * @author Ben Allen
 * @date 2012-03-10
 * @version 0.9.5
 */
public final class Matrix {

	/** Static class. */
	private Matrix() {
		throw new AssertionError();
	}

	/** Create a matrix. Purely convenience. */
	public static double[][] create(int rows, int cols) {
		return new double[rows][cols];
	}

	/**
	 * Create a square matrix and initialise it as an identity. Purely convenience.
	 */
	public static double[][] createIdentity(int rows) {
		double[][] m = new double[rows][rows];
		for (int i = 0; i < rows; i++) {
			m[i][i] = 1;
		}
		return m;
	}

	/** Test the equality of the sizes of two matrices. */
	public static boolean dimensionEquals(double[][] left, double[][] right) {
		return (left.length == right.length) && (left[0].length == right[0].length);
	}

	/** Test the equality of two matrices. */
	public static boolean equals(double[][] left, double[][] right) {
		int leftrows = left.length;
		int leftcols = left[0].length;
		int rightrows = right.length;
		int rightcols = right[0].length;
		if ((leftrows != rightrows) || (leftcols != rightcols)) {
			return false;
		}
		for (int i = 0; i < leftrows; i++) {
			for (int j = 0; j < leftcols; j++) {
				if (left[i][j] != right[i][j]) {
					return false;
				}
			}
		}
		return true;
	}

	/** Print a matrix to a PrintStream. */
	public static void print(PrintStream o, double[][] source, int places_front, int places_behind) {
		String fmt = "%" + (places_front + places_behind + 1) + "." + places_behind + "f";
		int rows = source.length;
		int cols = source[0].length;
		int lastcol = cols - 1;
		for (int i = 0; i < rows; i++) {
			o.print("[");
			for (int j = 0; j < cols; j++) {
				o.printf(fmt, source[i][j]);
				if (j < lastcol) {
					o.print(", ");
				}
			}
			o.println("]");
		}
	}

	/** Generate a String representation of a matrix. */
	public static String toString(double[][] source, int places_front, int places_behind) {
		String fmt = "%" + (places_front + places_behind + 1) + "." + places_behind + "f";
		StringBuilder sb = new StringBuilder();
		int rows = source.length;
		int cols = source[0].length;
		int lastcol = cols - 1;
		for (int i = 0; i < rows; i++) {
			sb.append("[");
			for (int j = 0; j < cols; j++) {
				sb.append(String.format(fmt, source[i][j]));
				if (j < lastcol) {
					sb.append(", ");
				}
			}
			sb.append("]\n");
		}
		return sb.toString();
	}

	/** Generate a String representation of a row of a matrix. */
	public static String rowToString(double[][] source, int row, int places_front, int places_behind) {
		String fmt = "%" + (places_front + places_behind + 1) + "." + places_behind + "f";
		StringBuilder sb = new StringBuilder();
		int cols = source[0].length;
		int lastcol = cols - 1;
		sb.append("[");
		for (int j = 0; j < cols; j++) {
			sb.append(String.format(fmt, source[row][j]));
			if (j < lastcol) {
				sb.append(", ");
			}
		}
		sb.append("]");
		return sb.toString();
	}

	/** Fill a matrix with zeroes. */
	public static void zero(double[][] target) {
		int rows = target.length;
		int cols = target[0].length;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				target[i][j] = 0;
			}
		}
	}

	/**
	 * Fill a matrix as an Identity. If the matrix is not square, the extra region will be zeroed.
	 */
	public static void identity(double[][] target) {
		int rows = target.length;
		int cols = target[0].length;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				target[i][j] = i == j ? 1 : 0;
			}
		}
	}

	/** Copy the contents of one matrix to another. Can be done in-place. */
	public static void copy(double[][] target, double[][] source) {
		copy(target, 0, source, 0, source[0].length);
	}

	/**
	 * Copy the contents of one column-range of one matrix to one column-range of another. Cannot be done in-place.
	 */
	public static void copy(double[][] target, int tcol, double[][] source, int scol, int range) {
		int offset = tcol - scol;
		int rows = source.length;
		int maxcol = scol + range;
		for (int i = 0; i < rows; i++) {
			for (int j = scol; j < maxcol; j++) {
				target[i][j + offset] = source[i][j];
			}
		}
	}

	/**
	 * Copy the contents of one region of one matrix to one region of another. Cannot be done in-place.
	 */
	public static void copy(double[][] target, int trow, int tcol, double[][] source, int srow, int scol,
			int row_range, int col_range) {
		int roffset = trow - srow;
		int coffset = tcol - scol;
		int maxrow = srow + row_range;
		int maxcol = scol + col_range;
		for (int i = srow; i < maxrow; i++) {
			for (int j = scol; j < maxcol; j++) {
				target[i + roffset][j + coffset] = source[i][j];
			}
		}
	}

	/** Transpose a matrix. Can be done in-place if the matrix is square. */
	public static void transpose(double[][] target, double[][] source) {
		double temp;
		int rows = source.length;
		int cols = source[0].length;
		for (int i = 0; i < rows; i++) {
			for (int j = i + 1; j < cols; j++) {
				temp = source[i][j];
				target[i][j] = source[j][i];
				target[j][i] = temp;
			}
		}
	}

	/** Join the rows of two matrices. Uses the row count of the right matrix. */
	public static void augment(double[][] target, double[][] right, double[][] left) {
		int rows = right.length;
		int leftcols = left[0].length;
		int rightcols = right[0].length;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < leftcols; j++) {
				target[i][j] = left[i][j];
			}
			for (int j = 0; j < rightcols; j++) {
				target[i][leftcols + j] = right[i][j];
			}
		}

	}

	/** Swap two rows of a matrix. Must be done in-place. */
	public static void rowSwap(double[][] target, int row1, int row2) {
		double[] temp;
		temp = target[row1];
		target[row1] = target[row2];
		target[row2] = temp;
	}

	/**
	 * Multiply a row of a matrix by a scalar (factor_num / factor_denom). Must be done in-place.
	 */
	public static void rowScale(double[][] target, int row, double factor_num, double factor_denom) {
		int cols = target[0].length;
		for (int i = 0; i < cols; i++) {
			target[row][i] *= factor_num;
			target[row][i] /= factor_denom;
		}
	}

	/**
	 * Subtract a scalar multiple (factor_num / factor_denom) of target[srow] from target[trow]. Must be done in-place.
	 */
	public static void rowSubtract(double[][] target, int trow, int srow, double factor_num, double factor_denom) {
		int cols = target[0].length;
		for (int i = 0; i < cols; i++) {
			target[trow][i] -= (factor_num * target[srow][i]) / factor_denom;
		}
	}

	/**
	 * Add scalar multiples of two matrices. Can be done in-place. Will error if left is smaller than right.
	 */
	public static void add(double[][] target, double[][] right, double r_factor, double[][] left, double l_factor) {
		int rows = right.length;
		int cols = right[0].length;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				target[i][j] = left[i][j] * l_factor + right[i][j] * r_factor;
			}
		}
	}

	/**
	 * Multiply a chain of matrices. Each of the matrices listed after right will be successively multiplied on the left
	 * from first to last. For example, multiplyChain(T, R, L1, L2) results in T == L2 * L1 * R. The left matrices will
	 * be treated as square matrices of size right.rows. Automatically allocates a new temp array.
	 */
	public static void multiplyChainNT(double[][] target, double[][] right, double[][]... left) {
		Matrix.multiplyChain(new double[right.length][right[0].length], target, right, left);
	}

	/**
	 * Multiply a chain of matrices. Each of the matrices listed after right will be successively multiplied on the left
	 * from first to last. For example, multiplyChain(temp, T, R, L1, L2) results in T == L2 * L1 * R. The left matrices
	 * will be treated as square matrices of size right.rows.
	 */
	public static void multiplyChain(double[][] temp, double[][] target, double[][] right, double[][]... left) {
		int rows = right.length;
		int cols = right[0].length;
		double[][][] t = new double[2][][];
		// avoid having to copy the result over to target by setting it up so
		// that the last multiplication goes there anyway.
		if (left.length % 2 == 0) {
			// even number of matrices to left multiply
			t[0] = temp;
			t[1] = target;
		} else {
			// odd number
			t[0] = target;
			t[1] = temp;
		}
		// do first multiplication
		multiply(t[0], right, left[0], rows, cols, rows);
		// loop through remaining left matrices
		for (int i = 1; i < left.length; i++) {
			multiply(t[i % 2], t[(i + 1) % 2], left[i], rows, cols, rows);
		}
	}

	/**
	 * Multiply two matrices. Cannot be done in-place. Will error if left.cols > right.rows.
	 */
	public static void multiply(double[][] target, double[][] right, double[][] left) {
		multiply(target, right, left, left.length, right[0].length, left[0].length);
	}

	/**
	 * Multiply two matrices, producing a matrix [rows][cols] in size using [rows][mlength] elements from left and
	 * [mlength][cols] from right, such that target == left * right.
	 */
	public static void multiply(double[][] target, double[][] right, double[][] left, int rows, int cols, int mlength) {
		int i, j, k;
		for (i = 0; i < rows; i++) {
			for (j = 0; j < cols; j++) {
				target[i][j] = 0;
				for (k = 0; k < mlength; k++) {
					target[i][j] += left[i][k] * right[k][j];
				}
			}
		}
	}

	/** Multiply a matrix by a scalar. Can be done in-place. */
	public static void multiplyScalar(double[][] target, double[][] right, double left) {
		int rows = right.length;
		int cols = right[0].length;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				target[i][j] = left * right[i][j];
			}
		}
	}

	/**
	 * Multiply a 4-vector on the left by a 4x4 matrix. This is an unrolling of the normal matrix multiplication
	 * routine. Cannot be done in-place.
	 */
	public static void multiply4Vector(double[][] target, double[][] right, double[][] left) {
		// this is just an unrolling of the matrix multiplication routine
		target[0][0] = left[0][0] * right[0][0] + left[0][1] * right[1][0] + left[0][2] * right[2][0] + left[0][3]
				* right[3][0];
		target[1][0] = left[1][0] * right[0][0] + left[1][1] * right[1][0] + left[1][2] * right[2][0] + left[1][3]
				* right[3][0];
		target[2][0] = left[2][0] * right[0][0] + left[2][1] * right[1][0] + left[2][2] * right[2][0] + left[2][3]
				* right[3][0];
		target[3][0] = left[3][0] * right[0][0] + left[3][1] * right[1][0] + left[3][2] * right[2][0] + left[3][3]
				* right[3][0];
	}

	/**
	 * Multiply a 3-vector on the left by a 3x3 matrix. This is an unrolling of the normal matrix multiplication
	 * routine. Cannot be done in-place.
	 */
	public static void multiply3Vector(double[][] target, double[][] right, double[][] left) {
		target[0][0] = left[0][0] * right[0][0] + left[0][1] * right[1][0] + left[0][2] * right[2][0];
		target[1][0] = left[1][0] * right[0][0] + left[1][1] * right[1][0] + left[1][2] * right[2][0];
		target[2][0] = left[2][0] * right[0][0] + left[2][1] * right[1][0] + left[2][2] * right[2][0];
	}

	/**
	 * Multiply a 2-vector on the left by a 2x2 matrix. This is an unrolling of the normal matrix multiplication
	 * routine. Cannot be done in-place.
	 */
	public static void multiply2Vector(double[][] target, double[][] right, double[][] left) {
		target[0][0] = left[0][0] * right[0][0] + left[0][1] * right[1][0];
		target[1][0] = left[1][0] * right[0][0] + left[1][1] * right[1][0];
	}

	/**
	 * Calculate the diagonal product of a matrix. Useful for finding determinants after LUP factorisation.
	 */
	public static double diagonalProduct(double[][] source) {
		double prod = 1d;
		int rows = source.length;
		for (int i = 0; i < rows; i++) {
			prod *= source[i][i];
		}
		return prod;
	}

	/**
	 * Calculate the determinant of a matrix. For larger than 3x3 matrices, uses an algorithm based on LU factorisation.
	 * Due to precision issues, may return a value close to zero when it should be exactly zero, and vice versa.
	 * Automatically allocates a temp array if there are more than 3 rows.
	 */
	public static double determinantNT(double[][] source) {
		int l = source.length;
		return determinant(l < 4 ? null : new double[l][l], source);
	}

	/**
	 * Calculate the determinant of a matrix. For larger than 3x3 matrices, uses an algorithm based on LU factorisation.
	 * Due to precision issues, may return a value close to zero when it should be exactly zero, and vice versa. For 3x3
	 * or smaller, temp may be null.
	 */
	public static double determinant(double[][] temp, double[][] source) {
		int rows = source.length;
		if (rows != source[0].length) {
			// not square => no determinant
			throw new NonSquareMatrixException();
		}
		switch (rows) {
		case 1:
			return source[0][0];
		case 2:
			return source[0][0] * source[1][1] - source[0][1] * source[1][0];
		case 3:
			return source[0][0] * source[1][1] * source[2][2] + source[0][1] * source[1][2] * source[2][0]
					+ source[0][2] * source[1][0] * source[2][1] - source[0][0] * source[1][2] * source[2][1]
					- source[0][1] * source[1][0] * source[2][2] - source[0][2] * source[1][1] * source[2][0];
		default:
			// like LU decomposition, except that we don't need L, because we
			// know that its determinant == (-1)^(number of row swaps made)
			// copy source over to U
			copy(temp, source);
			// perform partial gaussian elimination
			// also, we're not interested in doing a full reduction to
			// row-echelon form, but rather zeroing out anything below the
			// diagonal.
			double det = 1d;
			int swrow;
			for (int i = 0; i < rows; i++) {
				// if possible, execute a row swap to make [i][i] non-zero
				for (swrow = i; swrow < rows; swrow++) {
					if (temp[swrow][i] != 0) {
						if (swrow != i) {
							rowSwap(temp, i, swrow);
							det *= -1d;
						}
						// zero out (lower) rest of column
						for (int j = i + 1; j < rows; j++) {
							if (temp[j][i] != 0) {
								rowSubtract(temp, j, i, temp[j][i], temp[i][i]);
							}
						}
						break;
					}
				}
				det *= temp[i][i];
			}
			return det;
		}
	}

	/**
	 * Calculate the rank of a matrix by partial Gaussian elimination. Automatically allocates a temp array.
	 */
	public static int rankNT(double[][] source) {
		return rank(new double[source.length][source[0].length], source);
	}

	/** Calculates the rank of a matrix by partial Gaussian elimination. */
	public static int rank(double[][] temp, double[][] source) {
		copy(temp, source);
		return partialGaussEliminate(temp);
	}

	/**
	 * Calculate the inverse of a matrix by Gauss-Jordan elimination. Will error if non-invertible, but doesn't check
	 * the determinant first. Can be done in-place. Due to precision issues, may report an inverse when it should not,
	 * and could (in theory) deny an inverse when there should be one. These problems mainly occur when internal
	 * calculations result in numbers that do not have an exact floating-point representation. Advise checking the
	 * determinant. Automatically allocates a temp array.
	 */
	public static void inverseNT(double[][] target, double[][] source) {
		inverse(new double[source.length][source[0].length], target, source);
	}

	/**
	 * Calculate the inverse of a matrix by Gauss-Jordan elimination. Will error if non-invertible, but doesn't check
	 * the determinant first. Can be done in-place. Due to precision issues, may report an inverse when it should not,
	 * and could (in theory) deny an inverse when there should be one. These problems mainly occur when internal
	 * calculations result in numbers that do not have an exact floating-point representation. Advise checking the
	 * determinant.
	 */
	public static void inverse(double[][] temp, double[][] target, double[][] source) {
		if (source.length != source[0].length) {
			// not square => not invertible
			throw new NonSquareMatrixException();
		}
		int rows = source.length;
		// copy source
		copy(temp, 0, source, 0, rows);
		// init target to identity
		identity(target);
		// eliminate and check rank
		if (parallelGaussJordanEliminate(temp, target) != rows) {
			throw new NonInvertibleMatrixException();
		}
	}

	/**
	 * Perform LU decomposition of a matrix. Can be done in-place, but L and U must be different arrays. Swaps rows in L
	 * as necessary to ensure that source == L * U. Source does not have to be a square matrix. If source is [m][n], L
	 * must be at least [m][m] and U [m][n]. Returns (-1)^(number of row swaps made).
	 */
	public static int decomposeLU(double[][] L, double[][] U, double[][] source) {
		// copy source over to U
		copy(U, source);
		// initialise L to zero matrix
		zero(L);
		// perform partial gaussian elimination, recording row subtraction
		// multipliers in swapL and swapping rows.
		// also, we're not interested in doing a full reduction to row-echelon
		// form, but rather zeroing out anything below the diagonal.
		int rows = source.length;
		int rswap_mul = 1;
		double[][] swapL = new double[rows][];
		// initialise row swap map
		for (int i = 0; i < rows; i++) {
			swapL[i] = L[i];
		}
		int swrow;
		for (int i = 0; i < rows; i++) {
			// if possible, execute a row swap to make [i][i] non-zero
			for (swrow = i; swrow < rows; swrow++) {
				if (U[swrow][i] != 0) {
					if (swrow != i) {
						rowSwap(U, i, swrow);
						rowSwap(swapL, i, swrow);
						rswap_mul *= -1;
					}
					// zero out (lower) rest of column
					for (int j = i + 1; j < rows; j++) {
						// record row sub multiplier
						swapL[j][i] = U[j][i] / U[i][i];
						if (U[j][i] != 0) {
							rowSubtract(U, j, i, U[j][i], U[i][i]);
						}
					}
					break;
				}
			}
		}
		// fix diagonal in swapL. should be all 1's.
		for (int i = 0; i < rows; i++) {
			swapL[i][i] = 1;
		}
		return rswap_mul;
	}

	/**
	 * Perform LUP decomposition of a matrix. Can be done in-place, but L, U and P must be diferrent arrays. The result
	 * is such that P * source == L * U, where P is a row permutation matrix. Source does not have to be a square
	 * matrix. If source is [m][n], L must be at least [m][m], U [m][n] and P [m][m]. Returns (-1)^(number of row swaps
	 * made).
	 */
	public static int decomposeLUP(double[][] L, double[][] U, double[][] P, double[][] source) {
		// copy source over to U
		copy(U, source);
		// initialise L to zero matrix and P to identity
		zero(L);
		identity(P);
		// perform partial gaussian elimination, recording row subtraction
		// multipliers in L and swapping rows.
		// also, we're not interested in doing a full reduction to row-echelon
		// form, but rather zeroing out anything below the diagonal.
		int rows = source.length;
		int rswap_mul = 1;
		int swrow;
		for (int i = 0; i < rows; i++) {
			// if possible, execute a row swap to make [i][i] non-zero
			for (swrow = i; swrow < rows; swrow++) {
				if (U[swrow][i] != 0) {
					if (swrow != i) {
						rowSwap(U, i, swrow);
						rowSwap(L, i, swrow);
						rowSwap(P, i, swrow);
						rswap_mul *= -1;
					}
					// zero out (lower) rest of column
					for (int j = i + 1; j < rows; j++) {
						// record row sub multiplier
						L[j][i] = U[j][i] / U[i][i];
						if (U[j][i] != 0) {
							rowSubtract(U, j, i, U[j][i], U[i][i]);
						}
					}
					break;
				}
			}
		}
		// fix diagonal in L. should be all 1's.
		for (int i = 0; i < rows; i++) {
			L[i][i] = 1;
		}
		return rswap_mul;
	}

	/**
	 * Perform a partial Gaussian elimination of a matrix. Converts the matrix to a form resembling row-echelon, except
	 * that the rows are not scaled to have a leading '1'. Returns the rank of the matrix. Must be done in-place.
	 */
	public static int partialGaussEliminate(double[][] target) {
		int row = 0;
		int cols = target[0].length;
		int rows = target.length;
		int swrow;
		out: for (int i = 0; i < cols; i++) {
			// if possible, execute a row swap to make [row][i] non-zero
			for (swrow = row; swrow < rows; swrow++) {
				if (target[swrow][i] != 0) {
					if (row != swrow) rowSwap(target, row, swrow);
					// zero out (lower) rest of column
					for (int j = row + 1; j < rows; j++) {
						if (target[j][i] != 0) {
							rowSubtract(target, j, row, target[j][i], target[row][i]);
						}
					}
					row++;
					if (row >= rows) break out;
					break;
				}
			}
		}
		return row;
	}

	/**
	 * Perform Gaussian elimination of a matrix. Converts the matrix to row-echelon form. Returns the rank of the
	 * matrix. Must be done in-place.
	 */
	public static int gaussEliminate(double[][] target) {
		int row = 0;
		int cols = target[0].length;
		int rows = target.length;
		int swrow;
		out: for (int i = 0; i < cols; i++) {
			// if possible, execute a row swap to make [row][i] non-zero
			for (swrow = row; swrow < rows; swrow++) {
				if (target[swrow][i] != 0) {
					if (row != swrow) rowSwap(target, row, swrow);
					// zero out (lower) rest of column
					for (int j = row + 1; j < rows; j++) {
						if (target[j][i] != 0) {
							rowSubtract(target, j, row, target[j][i], target[row][i]);
						}
					}
					// get leading '1'
					rowScale(target, row, 1d, target[row][i]);
					row++;
					if (row >= rows) break out;
					break;
				}
			}
		}
		return row;
	}

	/**
	 * Perform Gauss-Jordan elimination of a matrix. Converts the matrix to reduced row-echelon form. Returns the rank
	 * of the matrix. Must be done in-place.
	 */
	public static int gaussJordanEliminate(double[][] target) {
		int row = 0;
		int cols = target[0].length;
		int rows = target.length;
		int swrow;
		out: for (int i = 0; i < cols; i++) {
			// if possible, execute a row swap to make [row][i] non-zero
			for (swrow = row; swrow < rows; swrow++) {
				if (target[swrow][i] != 0) {
					if (row != swrow) rowSwap(target, row, swrow);
					// zero out entire rest of column
					for (int j = 0; j < rows; j++) {
						if (j != row && target[j][i] != 0) {
							rowSubtract(target, j, row, target[j][i], target[row][i]);
						}
					}
					// get leading '1'
					rowScale(target, row, 1d, target[row][i]);
					row++;
					if (row >= rows) break out;
					break;
				}
			}
		}
		return row;
	}

	/**
	 * Perform Gauss-Jordan elimination of a matrix (target) and apply all the same row ops to another matrix (p_target)
	 * in parallel. Returns the rank of the matrix. Must be done in-place. The arrays target and p_target must be
	 * different. This method is suitable for solving systems of equations if the inverse is not needed.
	 */
	public static int parallelGaussJordanEliminate(double[][] target, double[][] p_target) {
		int row = 0;
		int cols = target[0].length;
		int rows = target.length;
		int swrow;
		out: for (int i = 0; i < cols; i++) {
			// if possible, execute a row swap to make [row][i] non-zero
			for (swrow = row; swrow < rows; swrow++) {
				if (target[swrow][i] != 0) {
					if (row != swrow) {
						rowSwap(p_target, row, swrow);
						rowSwap(target, row, swrow);
					}
					// zero out entire rest of column
					for (int j = 0; j < rows; j++) {
						if (j != row && target[j][i] != 0) {
							rowSubtract(p_target, j, row, target[j][i], target[row][i]);
							rowSubtract(target, j, row, target[j][i], target[row][i]);
						}
					}
					// get leading '1'
					rowScale(p_target, row, 1d, target[row][i]);
					rowScale(target, row, 1d, target[row][i]);
					row++;
					if (row >= rows) break out;
					break;
				}
			}
		}
		return row;
	}
}
