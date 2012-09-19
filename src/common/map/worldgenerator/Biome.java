package common.map.worldgenerator;

import java.awt.Color;

public enum Biome {

	OCEAN(0x44447a), 
	MARSH(0x339933), 
	ICE(0x99ffff), 
	LAKE(0x336699), 
	BEACH(0xa09077), 
	SNOW(0xffffff), 
	TUNDRA(0xbbbbaa), 
	BARE(0x888888), 
	SCORCHED(0x555555), 
	TAIGA(0x99aa77), 
	SHRUBLAND(0x889977), 
	TEMPERATE_DESERT(0xc9d29b),
	TEMPERATE_RAIN_FOREST(0x448855), 
	TEMPERATE_DECIDUOUS_FOREST(0x679459), 
	TROPICAL_RAIN_FOREST(0x337755), 
	TROPICAL_SEASONAL_FOREST(0x559944), 
	SUBTROPICAL_DESERT(0xd2b98b),
	GRASSLAND(0x88aa55);

	Color color;
	
	Biome(int rgb) {
		color = new Color(rgb);
	}

}
