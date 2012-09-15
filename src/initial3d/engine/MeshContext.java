package initial3d.engine;

import initial3d.Initial3D;
import initial3d.linearmath.Matrix;

public class MeshContext {

	private final Mesh mesh;
	private final double[][] xform = Matrix.createIdentity(4);
	private final Material mtl;
	// texture coord transform?
	
	public MeshContext(Mesh mesh_, Material mtl_) {
		mesh = mesh_;
		mtl = mtl_;
	}
	
	public Mesh getMesh() {
		return mesh;
	}
	
	public Material getMaterial() {
		return mtl;
	}
	
	public synchronized void setTransform(double[][] m) {
		Matrix.copy(xform, m);
	}
	
	public synchronized void extractTransform(double[][] m) {
		Matrix.copy(m, xform);
	}
	
	synchronized void loadTransformTo(Initial3D i3d) {
		i3d.matrixMode(Initial3D.MODEL);
		i3d.loadMatrix(xform);
	}
	
}
