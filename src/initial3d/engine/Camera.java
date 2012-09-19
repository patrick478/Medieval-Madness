package initial3d.engine;

import initial3d.Initial3D;

public class Camera {

	private Vec3 pos = Vec3.zero;
	private double yaw, pitch, roll;
	private double fov = Math.PI / 3;

	private final double[][] kvector = Vec3.k.to4ArrayNormal();
	private final double[][] jvector = Vec3.j.to4ArrayNormal();
	private final double[][] worldnormal = new double[4][1];
	private final double[][] worldup = new double[4][1];

	Camera() {

	}

	public synchronized Vec3 getPosition() {
		return pos;
	}
	
	public synchronized void setPosition(Vec3 v) {
		pos = v;
	}
	
	public synchronized void setPosition(double x, double y, double z) {
		pos = Vec3.create(x, y, z);
	}

	public synchronized double getYaw() {
		return yaw;
	}

	public synchronized void setYaw(double yaw) {
		this.yaw = yaw;
	}

	public synchronized double getPitch() {
		return pitch;
	}

	public synchronized void setPitch(double pitch) {
		this.pitch = pitch;
	}

	public synchronized double getRoll() {
		return roll;
	}

	public synchronized void setRoll(double roll) {
		this.roll = roll;
	}
	
	public synchronized double getFOV() {
		return fov;
	}
	
	public synchronized void setFOV(double fov_) {
		fov = fov_;
	}

	public synchronized Vec3 getNormal() {
		return Vec3.create(worldnormal).unit();
	}
	
	public synchronized Vec3 getUpNormal() {
		return Vec3.create(worldup).unit();
	}

	public synchronized void move(double dx, double dy, double dz) {
		pos = pos.add(dx, dy, dz);
	}
	
	public synchronized void move(Vec3 delta) {
		pos = pos.add(delta);
	}

	public synchronized void rotate(double dyaw, double dpitch, double droll) {
		yaw += dyaw;
		pitch += dpitch;
		roll += droll;
	}

	synchronized void loadTransformTo(Initial3D i3d) {
		i3d.matrixMode(Initial3D.VIEW);
		i3d.loadIdentity();
		i3d.translateX(-pos.x);
		i3d.translateY(-pos.y);
		i3d.translateZ(-pos.z);
		i3d.rotateY(-yaw);
		i3d.rotateX(-pitch);
		i3d.rotateZ(-roll);
		i3d.matrixMode(Initial3D.VIEW_INV);
		i3d.transformOne(worldnormal, kvector);
		i3d.transformOne(worldup, jvector);
	}
}
