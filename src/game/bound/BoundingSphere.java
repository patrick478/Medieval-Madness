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
	public Vec3 intersects(BoundingBox _b){
		double[][] result = new double[3][1];
		
		double[][] sph_cen =  this.position.to3Array();
		double[][] box_min =  _b.getNegExtreme().to3Array();
		double[][] box_max =  _b.getPosExtreme().to3Array();
		
		//for each plane record the 1-d vector from the 
		//edges to the sphere on that plane.
		for(int i=0; i<3; i++){
			if(sph_cen[i][0] < box_min[i][0]){
				result[i][0] =   box_min[i][0] - sph_cen[i][0]; //TODO not sure if right way round
			}else if(sph_cen[i][0] > box_max[i][0]){
				result[i][0] = box_max[i][0] - sph_cen[i][0];
			}
		}
		//compile the vector together and check the distance
		Vec3 collisionNorm = Vec3.create(result);
		if(this.radius > collisionNorm.mag()){
			return collisionNorm.unit();
		}
		return null;
	}

	@Override
	public Vec3 intersects(BoundingSphere b) {
		Vec3 collisionNorm = b.position.sub(position);
		if(radius + b.radius > collisionNorm.mag()){
			return collisionNorm.unit();
		}
		return null;
	}

	public double getRadius() {
		return radius;
	}
}
