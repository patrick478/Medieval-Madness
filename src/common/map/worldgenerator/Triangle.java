package common.map.worldgenerator;

import initial3d.engine.Vec3;

public class Triangle {
	
	private final Vec3[] ver = new Vec3[3];
	
	public Triangle(Point[] points, double[] heights){
		if(points.length!=3 || heights.length!=3){
			throw new IllegalArgumentException();
		}
		
		for(int i = 0; i<3; i++){
			ver[i] = Vec3.create(points[i].x, heights[i], points[i].y);
		}
		
		Vec3 normal = Vec3.createPlaneNorm(ver[0], ver[1], ver[2]);
		
		if (normal.y < 0) {
			Vec3 temp = ver[0];
			ver[0] = ver[1];
			ver[1] = temp; 
		}
	}
	
	/**
	 * A method that returns true if the given point in inside or on the 
	 * triangle using some halfspace function gibberish.
	 */
	public boolean contains(Point p){
		return ((ver[0].x - ver[1].x) * (p.y - ver[0].z) - (ver[0].z - ver[1].z) * (p.x - ver[0].x) >= 0 &&
	            (ver[1].x - ver[2].x) * (p.y - ver[1].z) - (ver[1].z - ver[2].z) * (p.x - ver[1].x) >= 0 &&
	            (ver[2].x - ver[0].x) * (p.y - ver[2].z) - (ver[2].z - ver[0].z) * (p.x - ver[2].x) >= 0);
	}
	
	/**
	 * A method that returns true if the given positions are in inside or on the 
	 * triangle using some halfspace function gibberish.
	 */
	public boolean contains(double x, double z){
		return ((ver[0].x - ver[1].x) * (z - ver[0].z) - (ver[0].z - ver[1].z) * (x - ver[0].x) >= 0 &&
	            (ver[1].x - ver[2].x) * (z - ver[1].z) - (ver[1].z - ver[2].z) * (x - ver[1].x) >= 0 &&
	            (ver[2].x - ver[0].x) * (z - ver[2].z) - (ver[2].z - ver[0].z) * (x - ver[2].x) >= 0);
	}

	/**
	 * Returns the interpolated value for the given point (assuming it's
	 * inside the triangle) using barycentric gibberish.
	 */
	public double height(Point p){
		double dT = (ver[1].z - ver[2].z) * (ver[0].x - ver[2].x) + (ver[2].x - ver[1].x) * (ver[0].z - ver[2].z);
		
		double a1 = ((ver[1].z - ver[2].z) * (p.x - ver[2].x) + (ver[2].x - ver[1].x) * (p.y - ver[2].z)) / dT;
		double a2 = ((ver[2].z - ver[0].z) * (p.x - ver[2].x) + (ver[0].x - ver[2].x) * (p.y - ver[2].z)) / dT;
		double a3 = 1-a1-a2;
		
		return a1 * ver[0].y + a2 * ver[1].y + a3 * ver[2].y ;
	}
	
	/**
	 * Returns the interpolated value for the given positions (assuming it's
	 * inside the triangle) using barycentric gibberish.
	 */
	public double height(double x, double z){
		double dT = (ver[1].z - ver[2].z) * (ver[0].x - ver[2].x) + (ver[2].x - ver[1].x) * (ver[0].z - ver[2].z);
		
		double a1 = ((ver[1].z - ver[2].z) * (x - ver[2].x) + (ver[2].x - ver[1].x) * (z - ver[2].z)) / dT;
		double a2 = ((ver[2].z - ver[0].z) * (x - ver[2].x) + (ver[0].x - ver[2].x) * (z - ver[2].z)) / dT;
		double a3 = 1-a1-a2;
		
		return a1 * ver[0].y + a2 * ver[1].y + a3 * ver[2].y ;
	}
	
	public double getMinX(){
		return Math.min(Math.min(ver[0].x, ver[1].x), ver[2].x);
	}
	
	public double getMinZ(){
		return Math.min(Math.min(ver[0].z, ver[1].z), ver[2].z);
	}
	
	public double getMaxX(){
		return Math.max(Math.max(ver[0].x, ver[1].x), ver[2].x);
	}

	public double getMaxZ(){
		return Math.max(Math.max(ver[0].z, ver[1].z), ver[2].z);
	}
	
	public String toString(){
		String s = "Triangle :: ";
		for(Vec3 v : ver){
			s += v.toString();
		}
		return s;
	}
	
	public static void main(String[] args){
		//small testing methods
		Triangle t = new Triangle(
				new Point[]{new Point(1, 1),new Point(10, 1), new Point(10, 10)},
				new double[]{0.5, .25, .50});
		System.out.println(t.height(new Point(5, 2)));
	}
}
















