package game;

import initial3d.engine.Vec3;

public class BoundingBox extends Bound{

	private final Vec3 posex;// positive extreme
	private final Vec3 position; //lowest co-ordinates of bounding box, negative extreme
	private final Vec3 size; //always positive values
	private final double volume;
	
	public BoundingBox(Vec3 position, Vec3 size){
		this.position = Vec3.negativeExtremes(position, position.add(size));
		this.posex = Vec3.positiveExtremes(position, position.add(size));
		this.size = posex.sub(this.position);
		volume = size.x * size.y * size.z;
	}
	

	public boolean contains(BoundingBox b){
		if(b == null) return false;
		if (this.position.x <= b.position.x &&
			this.position.y <= b.position.y &&
			this.position.z <= b.position.z &&
			this.position.add(this.size).x >= b.position.add(b.size).x &&
			this.position.add(this.size).y >= b.position.add(b.size).y &&
			this.position.add(this.size).z >= b.position.add(b.size).z){
			return true;
		}
		return false;
	}
	
	//Obsolete method
	public boolean contains(Vec3 v){
		if(v == null) return false;
		if (this.position.x <= v.x &&
			this.position.y <= v.y &&
			this.position.z <= v.z &&
			this.position.add(this.size).x > v.add(this.size).x &&
			this.position.add(this.size).y > v.add(this.size).y &&
			this.position.add(this.size).z > v.add(this.size).z){
			return true;
		}
		return false;
	}
	
	public boolean intersects(BoundingBox b){
		if (b == null) return false;
		b = getIntersection(b);
		if (b!=null && b.volume>0){
			return true;
		}
		return false;
	}
	
	
	public BoundingBox getIntersection(BoundingBox b){

		Vec3 interNegex = Vec3.positiveExtremes(this.position, b.position);
		Vec3 interPosex = Vec3.negativeExtremes(this.posex, b.posex);
		
		if (interPosex.x>=interNegex.x &&
			interPosex.y>=interNegex.y &&
			interPosex.z>=interNegex.z){
			return new BoundingBox(interNegex, interPosex.sub(interNegex));
		}
		return null;

	}
	
	public double getVolume(){
		return volume;
	}
	
	public Vec3 getPosition(){
		return position;
	}
	public Vec3 getSize(){
		return size;
	}
	
	@Override
	public String toString(){
		return String.format("%.3f,%.3f,%.3f",position.x, position.y, position.z);
	}
}
