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
	public boolean intersects(BoundingBox _b){
		double dis_sqr = 0;//distance squared between sphere and box 
		double[][] sph_cen =  this.position.to3Array();
		double[][] box_min =  _b.getNegExtreme().to3Array();
		double[][] box_max =  _b.getPosExtreme().to3Array();
		for(int i=0; i<3; i++){
			if(sph_cen[i][0] < box_min[i][0]){
				dis_sqr += Math.pow(sph_cen[i][0] - box_min[i][0], 2);
			}else if(sph_cen[i][0] > box_max[i][0]){
				dis_sqr += Math.pow(sph_cen[i][0] - box_max[i][0], 2);
			}
		}
		return Math.pow(this.radius, 2) > dis_sqr ;
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
