package initial3d.engine;

import initial3d.Initial3D;

public abstract class Light {

	public void trackReferenceFrame(ReferenceFrame rf) {
		// TODO
	}
	
	void loadTo(Initial3D i3d, int light_n) {
		
	}

	public static class DirectionalLight extends Light {

	}

	public static class SphericalPointLight extends Light {

	}

	public static class SpotLight extends Light {

	}

}
