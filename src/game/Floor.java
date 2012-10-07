package game;

import java.util.ArrayList;
import java.util.List;

import initial3d.engine.Vec3;

public class Floor {

	private Vec3 startPos;
	private Vec3 endPos;
	
	private List<WallEntity> walls;
	
	public Floor(List<WallEntity> _walls){
		walls = new ArrayList<WallEntity>(_walls);
	}
	
	private List<WallEntity> getWalls(){
		return walls;
	}
}
