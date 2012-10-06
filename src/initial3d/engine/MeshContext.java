package initial3d.engine;

import static initial3d.Initial3D.*;
import initial3d.Initial3D;
import initial3d.linearmath.Matrix;

public class MeshContext extends Drawable {

	private final Mesh mesh;
	private Material mtl;
	private ReferenceFrame rf;
	private double scale = 1;

	private final double[][] xformtemp = Matrix.create(4, 4);
	private final float[] coltemp = new float[3];

	public MeshContext(Mesh mesh_, Material mtl_, ReferenceFrame rf_) {
		if (mesh_ == null || mtl_ == null) throw new IllegalArgumentException("Null mesh or material not permitted.");
		mesh = mesh_;
		mtl = mtl_;
		trackReferenceFrame(rf_);
	}

	public final Mesh getMesh() {
		return mesh;
	}

	public final Material getMaterial() {
		return mtl;
	}

	public final void setMaterial(Material mtl_) {
		if (mtl_ == null) throw new IllegalArgumentException("Null material not permitted.");
		mtl = mtl_;
	}

	public final double getScale() {
		return scale;
	}

	public final void setScale(double scale_) {
		scale = scale_;
	}

	public final void trackReferenceFrame(ReferenceFrame rf_) {
		rf = rf_ == null ? ReferenceFrame.SCENE_ROOT : rf_;
	}

	public final ReferenceFrame getReferenceFrame() {
		return rf;
	}

	@Override
	protected final void draw(Initial3D i3d, int framewidth, int frameheight) {

		i3d.matrixMode(MODEL);
		i3d.pushMatrix();
		i3d.loadIdentity();

		loadTransform(i3d);

		i3d.materialfv(FRONT, AMBIENT, mtl.ka.toArray(coltemp));
		i3d.materialfv(FRONT, DIFFUSE, mtl.kd.toArray(coltemp));
		i3d.materialfv(FRONT, SPECULAR, mtl.ks.toArray(coltemp));
		i3d.materialfv(FRONT, EMISSION, mtl.ke.toArray(coltemp));
		i3d.materialf(FRONT, OPACITY, mtl.opacity);
		i3d.materialf(FRONT, SHININESS, mtl.shininess);

		if (mtl.map_kd != null) {
			i3d.enable(TEXTURE_2D);
			i3d.texImage2D(FRONT, mtl.map_kd, null, null);
		}

		// TODO select mesh lod
		MeshLOD mlod = mesh.get(0);

		i3d.vertexData(mlod.vertices);
		i3d.texCoordData(mlod.texcoords);
		i3d.normalData(mlod.normals);

		i3d.objectID(getDrawIDStart());

		i3d.drawPolygons(mlod.polys, 0, mlod.polys.count());

		i3d.disable(TEXTURE_2D);
		i3d.popMatrix();

	}

	protected void loadTransform(Initial3D i3d) {

		// load the transforms back to the scene root (world space)
		for (ReferenceFrame r = rf; r != ReferenceFrame.SCENE_ROOT; r = r.getParent()) {
			r.getOrientation().toOrientationMatrix(xformtemp);
			i3d.multMatrix(xformtemp);
			r.getPosition().toTranslationMatrix(xformtemp);
			i3d.multMatrix(xformtemp);
		}

		// scale mesh (about mesh origin)
		i3d.scale(scale);

	}

}
