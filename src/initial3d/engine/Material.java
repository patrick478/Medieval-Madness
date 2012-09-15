package initial3d.engine;

public class Material {

	public final float[] ka = new float[] { 0.1f, 0.1f, 0.1f };
	public final float[] kd = new float[] { 1f, 1f, 1f };
	public final float[] ks = new float[] { 0f, 0f, 0f };
	public final float[] ke = new float[] { 0f, 0f, 0f };
	public float shininess = 1f, opacity = 1f;

	public Material() {

	}

}
