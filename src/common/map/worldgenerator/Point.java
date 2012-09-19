package common.map.worldgenerator;

public class Point {
	public double x;
	public double y;

	public Point() {
	}
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void setPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (x);
		result = prime * result + (int) (y);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		
		//if the points are lass than 0.01 away there are considered the same spot
		if (Math.hypot(x-other.x, y-other.y)>0.0001)
			return false;
		return true;
	}
	
	
}
