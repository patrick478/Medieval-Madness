package game.item;

import game.ItemType;
import game.modelloader.Content;

import initial3d.engine.Color;
import initial3d.engine.Material;
import initial3d.engine.Mesh;
import initial3d.engine.MeshContext;
import initial3d.engine.Quat;
import initial3d.engine.Vec3;

import java.awt.image.BufferedImage;

public class Key extends Item{

	public Key(Vec3 _pos) {
		this(System.nanoTime(), _pos);
	}
	
	public Key(long id, Vec3 _pos) {
		super(id, Content.<BufferedImage> loadContent("resources/inventory/key.png"), "A key used to unlock doors", ItemType.Key, _pos);
		Material mat = new Material(Color.GRAY, new Color(0.081f, 0.064f, 0.036f), new Color(0.81f, 0.72f, 0.54f), new Color(
				0.2f, 0.2f, 0f), 1f, 1f);
		Mesh m = Content.loadContent("resources/models/key/key.obj");
		MeshContext mc = new MeshContext(m, mat, this.getItemEntity());
		mc.setHint(MeshContext.HINT_SMOOTH_SHADING);
		mc.setScale(3);
		this.getItemEntity().addMeshContext(mc);
		this.getItemEntity().setOrientation(Quat.create(Vec3.create(-1, 0, 0)));
	}

}
