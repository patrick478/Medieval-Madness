package common.map.worldgenerator;
import java.util.List;



public class Center {
    public int index;
  
    public Point point;  // location
    public boolean water;  // lake or ocean
    public boolean ocean;  // ocean
    public boolean coast;  // land polygon touching an ocean
    public boolean border;  // at the edge of the map
    public Biome biome;  // biome type (see article)
    public double elevation;  // 0.0-1.0
    public double moisture;  // 0.0-1.0

    //TODO?
    public List<Center> neighbors;
    public List<Edge> borders;
    public List<Corner> corners;
}
