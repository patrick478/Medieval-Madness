package common.map;

import java.util.HashMap;
import java.util.Random;

import common.map.worldgenerator.Biome;

import initial3d.Initial3D;
import initial3d.Texture;
import initial3d.engine.Color;
import initial3d.engine.Material;

public class TerrainTexture {
	
	private static final int terr_tex_size = 32;
	private static final HashMap<Biome, Material> textureCache = new HashMap<Biome, Material>();
	
	public static Material getTexture(Biome b){
		if(b==null){
			return getTexture(b.OCEAN);
		}else if(textureCache.containsKey(b)){
			return textureCache.get(b);
		}
		
		Material terr_mtl = new Material(new Color(0.4f, 0.4f, 0.4f), new Color(0.1f, 0.1f, 0.1f), 1f);
		Texture terr_tx = Initial3D.createTexture(terr_tex_size);
		// populate the terrain texture
		Random r = new Random();
		for (int u = 0; u < terr_tex_size; u++) {
			for (int v = 0; v < terr_tex_size; v++) {
				terr_tx.setTexel(u, v, 1f,  
						(float) ((b.color.getRed() / 255d) - (r.nextDouble() * 0.10 - 0.05)),
						(float) ((b.color.getGreen() / 255d) - (r.nextDouble() * 0.10 - 0.05)), 
						(float) ((b.color.getBlue() / 255d) - (r.nextDouble() * 0.10 - 0.05)));
			}
		}
		terr_tx.composeMipMaps();
		terr_tx.useMipMaps(true);

		terr_mtl = new Material(terr_mtl, terr_tx, null, null);
		
		textureCache.put(b, terr_mtl);
		return terr_mtl;
	}

}
