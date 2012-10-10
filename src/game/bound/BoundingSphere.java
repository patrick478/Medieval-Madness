package game.bound;

import initial3d.engine.Vec3;

public class BoundingSphere extends Bound {
	private final Vec3 position;
	private final double radius;

	public BoundingSphere(Vec3 _position, double _radius) {
		position = _position;
		radius = _radius;
	}

	@Override
	public Vec3 getPosition() {
		return position;
	}

	@Override
	public Bound setPosition(Vec3 v) {
		return new BoundingSphere(v, radius);
	}

	@Override
	public boolean contains(Vec3 v) {
		double distance = position.sub(v).mag();
		return distance < radius;
	}

	@Override
	public boolean intersects(BoundingBox b){
//		double dis;
//		Vec3 neg_ext = b.getNegExtreme().sub(position);
//		Vec3 pos_ext = b.getPosExtreme().sub(position);
//		Vec3 closestPoint = Vec3.create(
//				Math.pow(Math.min(Math.abs(neg_ext.x), Math.abs(pos_ext.x)), 2), 
//				Math.pow(Math.min(Math.abs(neg_ext.y), Math.abs(pos_ext.y)), 2), 
//				Math.pow(Math.min(Math.abs(neg_ext.z), Math.abs(pos_ext.z)), 2));
//		
//		return closestPoint.mag() < radius;
		return false;
	}

	@Override
	public boolean intersects(BoundingSphere b) {
		double distance = position.sub(b.position).mag();
		return distance < radius + b.radius;
	}

	public double getRadius() {
		return radius;
	}
}
