package initial3d.renderer;

import java.util.HashSet;
import java.util.Set;

abstract class AbstractMatrixStack {

	private Set<AbstractDerivedMatrixStack> listeners = new HashSet<AbstractDerivedMatrixStack>();
	
	public final void addDeriver(AbstractDerivedMatrixStack dms) {
		listeners.add(dms);
		dms.composeNow();
	}

	protected final void notifyDerived() {
		for(AbstractDerivedMatrixStack dms : listeners) {
			dms.composeNow();
		}
	}
	
	public abstract void pushMatrix();

	public abstract void popMatrix();

	public abstract void loadIdentity();

	public abstract void loadMatrix(double[][] m);

	public abstract void multMatrix(double[][] m);

	public abstract void transposeMatrix();

	public abstract void extractMatrix(double[][] m);
	
	public abstract void transformOne(double[][] target, double[][] right);

	public abstract void translateX(double d);

	public abstract void translateY(double d);

	public abstract void translateZ(double d);

	public abstract void scale(double f);

	public abstract void rotateX(double r);

	public abstract void rotateY(double r);

	public abstract void rotateZ(double r);

}