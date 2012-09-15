package initial3d.renderer;

import initial3d.linearmath.Matrix;

final class ComposedMatrixStack extends AbstractDerivedMatrixStack {

	private AbstractMatrixStack left, right;

	private double[][] matrix = Matrix.createIdentity(4);
	private double[][] temp_l = Matrix.create(4, 4);
	private double[][] temp_r = Matrix.create(4, 4);

	public ComposedMatrixStack(AbstractMatrixStack left_, AbstractMatrixStack right_) {
		left = left_;
		right = right_;
		left.addDeriver(this);
		right.addDeriver(this);
	}

	@Override
	public void composeNow() {
		left.extractMatrix(temp_l);
		right.extractMatrix(temp_r);
		Matrix.multiply(matrix, temp_r, temp_l);
		notifyDerived();
	}

	@Override
	public void extractMatrix(double[][] m) {
		Matrix.copy(m, matrix);
	}

	@Override
	public void transformOne(double[][] target, double[][] right) {
		Matrix.multiply(target, right, matrix);
	}

}
