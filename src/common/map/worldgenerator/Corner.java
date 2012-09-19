package common.map.worldgenerator;
import java.util.List;


public class Corner {
    public int index;
  
    public Point point;  // location
    public boolean ocean;  // ocean
    public boolean water;  // lake or ocean
    public boolean coast;  // touches ocean and land polygons
    public boolean border;  // at the edge of the map
    public double elevation;  // 0.0-1.0
    public double moisture;  // 0.0-1.0

    public List<Center> touches;
    public List<Edge> protrudes;
    public List<Corner> adjacent;
  
    public int river;  // 0 if no river, or volume of water in river
    public Corner downslope;  // pointer to adjacent corner most down hill
    public Corner watershed;  // pointer to coastal corner, or null
    public int watershed_size;
}
