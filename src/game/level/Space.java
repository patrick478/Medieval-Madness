package game.level;

public class Space {
	public static final int WALL = 0;
	public static final int EMPTY = 1;
	
	public final int x, z;
	public int type;
	public Space(int _x, int _z, int _type){
		x = _x; z = _z; type = _type;
	}
}
