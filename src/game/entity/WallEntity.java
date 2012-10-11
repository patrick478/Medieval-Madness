package game.entity;

import game.bound.Bound;
import game.bound.BoundingBox;
import game.bound.BoundingSphere;
import game.modelloader.Content;
import game.modelloader.WavefrontLoader;
import initial3d.engine.Color;
import initial3d.engine.Material;
import initial3d.engine.Mesh;
import initial3d.engine.MeshContext;
import initial3d.engine.Scene;
import initial3d.engine.Vec3;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import common.Timer;
import comp261.modelview.MeshLoader;

public class WallEntity extends Entity {
	
	private static final Vec3 wallRadius = Vec3.create(0.5, 0.5, 0.5);//TODO even need this?
	private final Bound bound; 
	
	public WallEntity(long _id, Vec3 _pos){
		super(_id);
		position = _pos;
		bound = new BoundingBox(_pos, wallRadius);
//		bound = new BoundingSphere(_pos, 0.5);
	}
	
	@Override
	public void poke() {}

	@Override
	protected Bound getBound(Vec3 position) {
		return bound;
	}

	@Override
	public boolean isSolid() {
		return true;
	}
}
