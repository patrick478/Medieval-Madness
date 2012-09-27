package initial3d.engine;

import initial3d.Initial3D;
import initial3d.linearmath.Matrix;
import static initial3d.Initial3D.*;

public abstract class Light {

	private ReferenceFrame rf = ReferenceFrame.SCENE_ROOT;

	protected double[] lightpos = new double[4];
	protected double[] spotdir = new double[4];
	protected Color color_d = Color.WHITE, color_s = Color.BLACK, color_a = Color.BLACK;
	protected float spot_cutoff = (float) Math.PI;
	protected float[] coltemp = new float[3];

	protected float atten_const = 1f;
	protected float atten_lin = 0f;
	protected float atten_quad = 0f;

	private final double[][] xformtemp = Matrix.create(4, 4);

	public Light(ReferenceFrame rf_) {
		if (rf_ == null) throw new IllegalArgumentException("Null not permitted.");
		rf = rf_;
	}

	public void trackReferenceFrame(ReferenceFrame rf_) {
		if (rf_ == null) rf_ = ReferenceFrame.SCENE_ROOT;
		rf = rf_;
	}

	void loadTo(Initial3D i3d, long lightid) {
		poke();
		i3d.matrixMode(VIEW);
		i3d.pushMatrix();
		i3d.loadIdentity();
		// load the transforms back to the scene root (world space)
		for (ReferenceFrame r = rf; r != ReferenceFrame.SCENE_ROOT; r = r.getParent()) {
			r.getOrientation().toOrientationMatrix(xformtemp);
			i3d.multMatrix(xformtemp);
			r.getPosition().toTranslationMatrix(xformtemp);
			i3d.multMatrix(xformtemp);
		}

		i3d.lightdv(lightid, POSITION, lightpos);
		i3d.lightdv(lightid, SPOT_DIRECTION, spotdir);

		i3d.popMatrix();

		i3d.lightf(lightid, SPOT_CUTOFF, spot_cutoff);
		i3d.lightfv(lightid, DIFFUSE, color_d.toArray(coltemp));
		i3d.lightfv(lightid, SPECULAR, color_s.toArray(coltemp));
		i3d.lightfv(lightid, AMBIENT, color_a.toArray(coltemp));

		// TODO proper attenuation
		i3d.lightf(lightid, INTENSITY, atten_const);

	}

	protected void poke() {
		// override as necessary
	}

	public static class DirectionalLight extends Light {

		private volatile Vec3 n = Vec3.j;

		public DirectionalLight(ReferenceFrame rf_) {
			super(rf_);
			lightpos[3] = 0;
		}

		public void setNormal(Vec3 n_) {
			n = n_;
		}

		protected void poke() {
			lightpos[0] = n.x;
			lightpos[1] = n.y;
			lightpos[2] = n.z;
		}

	}

	public static class SphericalPointLight extends Light {

		public SphericalPointLight(ReferenceFrame rf_) {
			super(rf_);
		}

	}

	public static class SpotLight extends Light {

		public SpotLight(ReferenceFrame rf_) {
			super(rf_);
		}

	}

	public static class SunLight extends DirectionalLight {

		public SunLight(ReferenceFrame rf_) {
			super(rf_);
		}

	}

}
