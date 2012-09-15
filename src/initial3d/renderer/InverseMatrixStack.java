package initial3d.renderer;

import initial3d.linearmath.Matrix;

public class InverseMatrixStack extends AbstractDerivedMatrixStack {

	private AbstractMatrixStack source;
	
	private double[][] matrix = Matrix.createIdentity(4);
	private double[][] temp = Matrix.create(4, 4);
	
	public InverseMatrixStack(AbstractMatrixStack ms) {
		source = ms;
		source.addDeriver(this);
	}
	
	@Override
	public void composeNow() {
		source.extractMatrix(matrix);
		Matrix.inverse(temp, matrix, matrix);
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
