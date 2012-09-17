package initial3d.engine;

import initial3d.Texture;

public class Material {

	public static final Color DEFAULT_KA = new Color(0.1f, 0.1f, 0.1f);
	public static final Color DEFAULT_KD = Color.WHITE;
	public static final Color DEFAULT_KS = Color.BLACK;
	public static final Color DEFAULT_KE = Color.BLACK;
	public static final float DEFAULT_SHININESS = 1f;
	public static final float DEFAULT_OPACITY = 1f;

	public static final Material DEFAULT = new Material(DEFAULT_KA, DEFAULT_KD, DEFAULT_KS, DEFAULT_KE,
			DEFAULT_SHININESS, DEFAULT_OPACITY);

	public final Color ka, kd, ks, ke;
	public final float shininess, opacity;

	// theoretically, opacity in diffuse alpha, shininess in spec alpha
	public final Texture map_kd, map_ks, map_ke;

	public Material(Color ka_, Color kd_, Color ks_, Color ke_, float shininess_, float opacity_) {
		ka = ka_;
		kd = kd_;
		ks = ks_;
		ke = ke_;
		shininess = shininess_;
		opacity = opacity_;
		map_kd = null;
		map_ks = null;
		map_ke = null;
	}

	public Material(Color kd_, Color ks_, float shininess_) {
		this(DEFAULT_KA, kd_, ks_, DEFAULT_KE, shininess_, DEFAULT_OPACITY);
	}

	public Material(Material basemtl, Texture map_kd_, Texture map_ks_, Texture map_ke_) {
		ka = basemtl.ka;
		kd = basemtl.kd;
		ks = basemtl.ks;
		ke = basemtl.ke;
		shininess = basemtl.shininess;
		opacity = basemtl.opacity;

		if (map_kd_ == null) throw new IllegalArgumentException();
		
		map_kd = map_kd_;
		map_ks = map_ks_;
		map_ke = map_ke_;

	}

}
