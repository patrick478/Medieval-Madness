package common.map.worldgenerator;

import initial3d.engine.Vec3;

public class Triangle {
	
	private final Vec3[] vertices = new Vec3[3];
	
	public Triangle(Point[] points, double[] heights){
		if(points.length!=3 || heights.length!=3){
			throw new IllegalArgumentException();
		}
		
		for(int i = 0; i<3; i++){
			vertices[i] = Vec3.create(points[i].x, heights[i], points[i].y);
		}
		
		
		
	}
	
	public boolean inside(Point p){
		return false;
	}

	
	public double height(Point p){
		return 0;
	}
}