package game.item;

import initial3d.Initial3D;
import initial3d.Texture;
import initial3d.engine.Color;
import initial3d.engine.Material;
import initial3d.engine.Mesh;
import initial3d.engine.MeshContext;
import initial3d.engine.Vec3;

import java.awt.image.BufferedImage;

import game.ItemType;
import game.modelloader.Content;

public class Battery extends Item {	
	public Battery(Vec3 _pos) {
		this(System.nanoTime(), _pos);
	}
	
	public Battery(long id, Vec3 _pos) {
		super(id, Content.<BufferedImage> loadContent("resources/inventory/battery.png"), "Used to recharge the players cannon", ItemType.Battery, _pos);
		
		Material mat = new Material(Color.GRAY, Color.GRAY, Color.GRAY, Color.BLACK, 20f, 1f);
		Texture tex_kd = Initial3D.createTexture(Content
				.<BufferedImage> loadContent("resources/models/battery/battery_kd.png"));
		Texture tex_ke = Initial3D.createTexture(Content
				.<BufferedImage> loadContent("resources/models/battery/battery_ke.png"));
		mat = new Material(mat, tex_kd, null, tex_ke);

		Mesh m = Content.loadContent("resources/models/battery/battery.obj");
		MeshContext mc = new MeshContext(m, mat, this.getItemEntity());
		mc.setScale(4);
		mc.setHint(MeshContext.HINT_SMOOTH_SHADING);
		this.getItemEntity().addMeshContext(mc);
	}
}
