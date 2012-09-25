package initial3d.engine;

import initial3d.Initial3D;

public class Camera {

	private ReferenceFrame track_frame = ReferenceFrame.SCENE_ROOT;

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
		return Math.PI / 3;
	}

	/* package-private */
	void loadViewTransform(Initial3D i3d) {
		// TODO
	}

}
