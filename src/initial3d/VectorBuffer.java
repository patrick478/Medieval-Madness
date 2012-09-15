package initial3d;

/** Fixed-size 4-vector buffer. Always stores 4-element vectors, but methods can use 1 to 4 elements. */
public abstract class VectorBuffer {

	public abstract int capacity() ;

	public abstract int count();

	public abstract void clear();

	public abstract int put(double[][] v);

	public abstract int put(int index, double[][] v);

	public abstract void get(int index, double[][] v);

}
