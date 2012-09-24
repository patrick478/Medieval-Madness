package initial3d.engine;

public interface ReferenceFrame {

	public static final ReferenceFrame SCENE_ROOT = new ReferenceFrame() {

		@Override
		public Vec3 getPosition() {
			return Vec3.zero;
		}

		@Override
		public Quat getOrientation() {
			return Quat.one;
		}

	};
	
	public Vec3 getPosition();

	public Quat getOrientation();

}
