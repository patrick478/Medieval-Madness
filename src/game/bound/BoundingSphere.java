package game.bound;

import initial3d.engine.Vec3;

public class BoundingSphere extends Bound{
	private final Vec3 position; 
	private final double radius;
	
	public BoundingSphere(Vec3 _position, double _radius){
		position = _position;
		radius = _radius;
	}
	
	public Vec3 getPosition(){
		return position;
	}
	
	public double getRadius(){
		return radius;
	}
	
	@Override
	public boolean contains(Vec3 v) {
		double distance = position.sub(v).mag();
		return distance < radius;
	}
	
	@Override
	public boolean intersects(BoundingBox b){
		//TODO implement a proper method
		return false;
	}
	
	@Override
	public boolean intersects(BoundingSphere b){
		double distance = position.sub(b.position).mag();
		return distance < radius + b.radius;
	}
}
