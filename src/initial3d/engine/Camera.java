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
		track_frame = rf;
	}
	
	/* package-private */
	void loadViewTransform(Initial3D i3d) {
		
	}

}
