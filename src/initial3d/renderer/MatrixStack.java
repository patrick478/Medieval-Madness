package initial3d.renderer;

import java.util.Stack;

import initial3d.linearmath.*;

class MatrixStack extends AbstractMatrixStack {

	private Stack<double[][]> stack = new Stack<double[][]>();

	private double[][] xtemp = Matrix.create(4, 4);
	private double[][] mtemp = Matrix.create(4, 4);

	public MatrixStack() {
		stack.push(Matrix.createIdentity(4));
	}

	@Override
	public void pushMatrix() {
		// copy current to new
		double[][] m = stack.peek();
		double[][] n = Matrix.create(4, 4);
		Matrix.copy(n, m);
		stack.push(n);
		notifyDerived();
	}

	@Override
	public void popMatrix() {
		if (stack.size() > 1) {
			stack.pop();
		}
		notifyDerived();
	}

	// everything else - pop, modify, push (read, modify, write)

	@Override
	public void loadIdentity() {
		double[][] st = stack.pop();
		Matrix.identity(st);
		stack.push(st);
		notifyDerived();
	}

	@Override
	public void loadMatrix(double[][] m) {
		double[][] st = stack.pop();
		Matrix.copy(st, m);
		stack.push(st);
		notifyDerived();
	}

	@Override
	public void multMatrix(double[][] m) {
		// left-multiply by m
		double[][] st = stack.pop();
		double[][] n = Matrix.create(4, 4);
		Matrix.multiply(n, st, m);
		stack.push(n);
		notifyDerived();
	}

	@Override
	public void transposeMatrix() {
		double[][] st = stack.pop();
		Matrix.transpose(st, st);
		stack.push(st);
		notifyDerived();
	}

	@Override
	public void extractMatrix(double[][] m) {
		double[][] st = stack.peek();
		Matrix.copy(m, st);
	}

	// transforms - applied to contents of active matrix
	// may need some temp matrices

	@Override
	public void translateX(double d) {
		double[][] st = stack.pop();
		// x = x + w * d
		TransformationMatrix4D.shear(xtemp, 0, 3, d);
		// Matrix.multiply(mtemp, st, xtemp);
		Matrix.multiply(mtemp, xtemp, st);
		Matrix.copy(st, mtemp);
		stack.push(st);
		notifyDerived();
	}

	@Override
	public void translateY(double d) {
		double[][] st = stack.pop();
		// y = y + w * d
		TransformationMatrix4D.shear(xtemp, 1, 3, d);
		// Matrix.multiply(mtemp, st, xtemp);
		Matrix.multiply(mtemp, xtemp, st);
		Matrix.copy(st, mtemp);
		stack.push(st);
		notifyDerived();
	}

	@Override
	public void translateZ(double d) {
		double[][] st = stack.pop();
		// z = z + w * d
		TransformationMatrix4D.shear(xtemp, 2, 3, d);
		// Matrix.multiply(mtemp, st, xtemp);
		Matrix.multiply(mtemp, xtemp, st);
		Matrix.copy(st, mtemp);
		stack.push(st);
		notifyDerived();
	}

	@Override
	public void scale(double f) {
		double[][] st = stack.pop();
		TransformationMatrix4D.scale(xtemp, f, f, f, 1d);
		// Matrix.multiply(mtemp, st, xtemp);
		Matrix.multiply(mtemp, xtemp, st);
		Matrix.copy(st, mtemp);
		stack.push(st);
		notifyDerived();
	}

	@Override
	public void rotateX(double r) {
		double[][] st = stack.pop();
		TransformationMatrix4D.rotateX(xtemp, r);
		// Matrix.multiply(mtemp, st, xtemp);
		Matrix.multiply(mtemp, xtemp, st);
		Matrix.copy(st, mtemp);
		stack.push(st);
		notifyDerived();
	}

	@Override
	public void rotateY(double r) {
		double[][] st = stack.pop();
		TransformationMatrix4D.rotateY(xtemp, r);
		// Matrix.multiply(mtemp, st, xtemp);
		Matrix.multiply(mtemp, xtemp, st);
		Matrix.copy(st, mtemp);
		stack.push(st);
		notifyDerived();
	}

	@Override
	public void rotateZ(double r) {
		double[][] st = stack.pop();
		TransformationMatrix4D.rotateZ(xtemp, r);
		// Matrix.multiply(mtemp, st, xtemp);
		Matrix.multiply(mtemp, xtemp, st);
		Matrix.copy(st, mtemp);
		stack.push(st);
		notifyDerived();
	}

	@Override
	public void transformOne(double[][] target, double[][] right) {
		Matrix.multiply(target, right, stack.peek());
	}

}
