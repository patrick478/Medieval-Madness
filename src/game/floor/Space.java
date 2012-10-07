package game.floor;

class Space {
	static final int WALL = 1;
	static final int EMPTY = 0;
	
	final int x, z;
	int type;
	Space(int _x, int _z, int _type){
		x = _x; z = _z; type = _type;
	}
}
