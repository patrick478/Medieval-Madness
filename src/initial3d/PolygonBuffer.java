package initial3d;

/** Fixed-size buffer for polygons. */
public abstract class PolygonBuffer {

	public abstract int count();

	public abstract int capacity();

	public abstract int maxVertices();

	public abstract void addPolygon(int[] v, int[] vt, int[] vn, int[] vc);

}