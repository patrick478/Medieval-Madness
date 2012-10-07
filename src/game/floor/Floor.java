package game.floor;

import java.util.ArrayList;
import java.util.List;

import game.entity.WallEntity;
import initial3d.engine.Vec3;

public class Floor {

	private Vec3 startPos;
	private Vec3 endPos;
	private Vec3 size;
	
	private List<WallEntity> walls;
	
	public Floor(int _size, List<WallEntity> _walls){
		walls = new ArrayList<WallEntity>(_walls);
		size = Vec3.create(_size, 0, _size);
	}
	
	private List<WallEntity> getWalls(){
		return walls;
	}
}
