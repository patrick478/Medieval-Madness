package common.map;

public class SegmentGenerator
{	
	private final long seed;
	private Perlin perlin;
	
	public static final double frequency = 3;
	
	public SegmentGenerator(long seed)
	{
		this.seed = seed;
		this.perlin = new Perlin(seed);
	}
	
	public Segment getSegment(long posx, long posz)
	{
		float[][] hm = new float[Segment.size + 3][Segment.size + 3];
		for(int z = -1; z <= Segment.size + 1; z++)
		{
			for(int x = -1; x <= Segment.size + 1; x++)
			{
				hm[x+1][z+1] = (float) perlin.getNoise((x/(double)Segment.size + posx)/frequency, (z/(double)Segment.size + posz)/frequency, 0, 8 );
			}
		}
		
		Segment s = new Segment(posx, posz, hm);
		
		return s;
	}
}