package initial3d.engine;

import initial3d.Initial3D;
import initial3d.linearmath.Matrix;
import static initial3d.Initial3D.*;

public class Camera {

	private volatile ReferenceFrame track_frame = ReferenceFrame.SCENE_ROOT;
	private volatile double fov = Math.PI / 3;

	private final double[][] xformtemp = Matrix.create(4, 4);
	
	private final double[][] kvector = Vec3.k.to4ArrayNormal();
	private final double[][] jvector = Vec3.j.to4ArrayNormal();
	private final double[][] worldnormal = new double[4][1];
	private final double[][] worldup = new double[4][1];

	public Camera() {

	}

	public ReferenceFrame getTrackedReferenceFrame() {
		return track_frame;
	}

	public void trackReferenceFrame(ReferenceFrame rf) {
		if (rf == null) {
			track_frame = ReferenceFrame.SCENE_ROOT;
		} else {
			track_frame = rf;
		}
	}

	public double getFOV() {
		return fov;
	}

	public void setFOV(double fov_) {
		fov = fov_;
	}
	
	public synchronized Vec3 getNormal() {
		return Vec3.create(worldnormal).unit();
	}

	public synchronized Vec3 getUpNormal() {
		return Vec3.create(worldup).unit();
	}

	/* package-private */
	void loadViewTransform(Initial3D i3d) {

		i3d.matrixMode(VIEW);
		i3d.loadIdentity();

		loadViewTransform_rec(i3d, track_frame);
		
		i3d.matrixMode(VIEW_INV);
		i3d.transformOne(worldnormal, kvector);
		i3d.transformOne(worldup, jvector);

	}

	private void loadViewTransform_rec(Initial3D i3d, ReferenceFrame r) {

		if (r.getParent() != ReferenceFrame.SCENE_ROOT) {
			loadViewTransform_rec(i3d, r.getParent());
		}

		r.getPosition().neg().toTranslationMatrix(xformtemp);
		i3d.multMatrix(xformtemp);
		r.getOrientation().conj().toOrientationMatrix(xformtemp);
		i3d.multMatrix(xformtemp);

	}

}
