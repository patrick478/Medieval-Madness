package initial3d.renderer;

abstract class AbstractDerivedMatrixStack extends AbstractMatrixStack {

	public AbstractDerivedMatrixStack() {
		
	}
	
	public abstract void composeNow();
	
	protected static final void NOPE() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void pushMatrix() {
		NOPE();
	}

	@Override
	public void popMatrix() {
		NOPE();
	}

	@Override
	public void loadIdentity() {
		NOPE();
	}

	@Override
	public void loadMatrix(double[][] m) {
		NOPE();
	}

	@Override
	public void multMatrix(double[][] m) {
		NOPE();
	}

	@Override
	public void transposeMatrix() {
		NOPE();
	}

	@Override
	public void translateX(double d) {
		NOPE();
	}

	@Override
	public void translateY(double d) {
		NOPE();
	}

	@Override
	public void translateZ(double d) {
		NOPE();
	}

	@Override
	public void scale(double f) {
		NOPE();
	}

	@Override
	public void rotateX(double r) {
		NOPE();
	}

	@Override
	public void rotateY(double r) {
		NOPE();
	}

	@Override
	public void rotateZ(double r) {
		NOPE();
	}

}
