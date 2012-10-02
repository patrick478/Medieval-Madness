package client.factories;

import java.io.FileInputStream;

import initial3d.engine.Vec3;
import common.entity.Player;
import comp261.modelview.MeshLoader;

public class EntityFactory {
	
	public static Player createPlayer(Vec3 radius, long id){
		//create player base
		Player p = new Player(radius, id);
		FileInputStream fis;
		try {
			fis = new FileInputStream("ball.txt");
			p.setMeshContexts(MeshLoader.loadComp261(fis));
			fis.close();
		} catch (Exception e) {e.printStackTrace();}
		
		return p;
	}
}
