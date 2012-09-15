package initial3d.linearmath;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		double[][] v3 = Vector3D.create(12, 3245, 69);
		
		double[][] v4 = Vector4D.create(32, 78, 0.23453476, 9001);
		
		System.out.println(Vector3D.toString(v3, 4, 3));
		
		System.out.println(Vector4D.toString(v4, 4, 3));

	}

}
