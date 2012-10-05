package common.map;

import initial3d.Initial3D;
import initial3d.Texture;
import initial3d.engine.Color;
import initial3d.engine.Material;
import initial3d.engine.Mesh;
import initial3d.engine.MeshLOD;
import initial3d.engine.ReferenceFrame;
import initial3d.engine.Vec3;
import initial3d.engine.MeshContext;

import java.util.Random;

public class Segment {
	public static final int size = 16;
	public static final float vertScale = 8f;
	public static final float horzScale = 2f;
	private static final int terr_tex_size = 32;

	private float[][] heightmap = null;

	public final int xPos;
	public final int zPos;
	public final long id;

	// TODO need to work out how to handel textures better than this... ^^;
	public static Material terr_mtl;
	private MeshContext terr_mc = null; 

	static {
		terr_mtl = new Material(new Color(0.4f, 0.4f, 0.4f), new Color(0.1f, 0.1f, 0.1f), 1f);
		Texture terr_tx = Initial3D.createTexture(terr_tex_size);
		// populate the terrain texture
		for (int u = 0; u < terr_tex_size; u++) {
			for (int v = 0; v < terr_tex_size; v++) {
				terr_tx.setTexel(u, v, 1f, 0.0f, (float) (Math.random() * 0.15 + 0.5), 0.0f);
			}
		}
		terr_tx.composeMipMaps();
		terr_tx.useMipMaps(true);

		terr_mtl = new Material(terr_mtl, terr_tx, null, null);
	}
	
	public static long getID(int posx, int posz){
		return (long)((long)posx<<32) | (((long)(posz)) & 0xFFFFFFFFL);
	}

	public Segment(int _xPos, int _zPos, float[][] heightmap) {
		xPos = _xPos;
		zPos = _zPos;
		id = getID(_xPos, _zPos);
		this.heightmap = heightmap;
	}

	public float[][] getData() {
		return this.heightmap;
	}
	
	public boolean contains(double x, double z){
		return x >= xPos*size*horzScale && x < (xPos+1)*size*horzScale
				&& z >= zPos*size*horzScale && z < (zPos+1)*size*horzScale;
	}
	
	//values go from 0 to size (inclusive)
	private float heightAt(int x, int z){
		return heightmap[(int)x+1][(int)z+1] * vertScale;
	}
	
	//values go from 0 to size (inclusive)
	private Vec3 normalAt(int x, int z){
		float h = heightAt(x, z);
		
		Vec3 d0 = Vec3.create(-2, heightAt(x - 1, z) - h, 0).unit();
		Vec3 d1 = Vec3.create(0, heightAt(x, z + 1) - h, 2).unit();
		Vec3 d2 = Vec3.create(2, heightAt(x + 1, z) - h, 0).unit();
		Vec3 d3 = Vec3.create(0, heightAt(x, z - 1) - h, -2).unit();

		return (d0.cross(d1).add(d1.cross(d2)).add(d2.cross(d3)).add(d3.cross(d0))).unit();
	}
	
	/**
	 * Translates the given relative value x, to a global position
	 * based on the segment position given. Assumes x is a value
	 * between 0 (inclusive) and Segment.size (exclusive).
	 * 
	 * @param x the segment relative value
	 * @param segPos one of Segment.xPos or Segment.zPos
	 * @return global position of that value
	 */
	private double globalPos(double x, int segPos){
		return (x + (segPos * size)) * horzScale;
	}
	
	/**
	 * Translates the global position given into relative position
	 * between 0 (inclusive) and Segment.size (exclusive).
	 * 
	 * @param x The global position 
	 * @return The relative position to the segment 
	 */
	private static double relativePos(double x){
		return (x/horzScale)%size;
	}
	
	/**
	 * Returns the height at the given global positions inside the Segment.
	 * returns 0 if the positions are not contained inside the segment
	 * 
	 * @param x global x position
	 * @param z global z position
	 * @return the height of the segment at the given global position
	 */
	public double getHeight(double x, double z){
		if(contains(x, z)){

			//relative positions
			x = relativePos(x);
			z = relativePos(z);
			
			//relative positions rounded down 
			int relX = (int) x;
			int relZ = (int) z;
			
			double topLeft = heightAt(relX, relZ);
			double topRight = heightAt(relX+1, relZ);
			
			double botLeft = heightAt(relX, relZ+1);
			double botRight = heightAt(relX+1, relZ+1);
			
			double left = (botLeft-topLeft)*(z%1) + topLeft;
			double right = (botRight-topRight)*(z%1) + topRight;

			return (right-left) * (z%1) + left;
			
		}
		return 0;
	}
	
	/**
	 * Returns the normal at the given global positions inside the Segment.
	 * returns a normal that points straight up if position is not contained
	 * inside the segment.
	 * 
	 * @param x global x position
	 * @param z global z position
	 * @return the normal of the segment at the given global position
	 */
	public Vec3 getNormal(double x, double z){
		if(contains(x, z)){

			//relative positions
			x = relativePos(x);
			z = relativePos(z);
			
			//relative positions rounded down 
			int relX = (int) x;
			int relZ = (int) z;
			
			Vec3 topLeft = normalAt(relX, relZ);
			Vec3 topRight = normalAt(relX+1, relZ);
			
			Vec3 botLeft = normalAt(relX, relZ+1);
			Vec3 botRight = normalAt(relX+1, relZ+1);
			
			Vec3 left = (botLeft.sub(topLeft)).scale(z%1).add(topLeft);
			Vec3 right = (botRight.sub(topRight)).scale(z%1).add(topRight);

			return (right.sub(left)).scale((z%1)).add(left);
			
		}
		return Vec3.create(0,1,0);
	}
	

	public MeshContext getMeshContext() {
		if (terr_mc!=null){
			return terr_mc;
		}
		//create the mesh value
		Mesh terr_mesh = new Mesh();
		//add the LOD
		terr_mesh.add(highLOD());
		//set the mesh context and return the value
		return terr_mc = new MeshContext(terr_mesh, terr_mtl, ReferenceFrame.SCENE_ROOT);
	}

	private MeshLOD highLOD() {

		MeshLOD mLOD = new MeshLOD(size * size * 2 * 2, 3, (size + 1) * (size + 1) * 2, 20,
				(size + 1) * (size + 1) * 2, 1);

		int[][] ind = new int[(size + 1)][(size + 1)];
		
		// first create vectors and normals
		for (int z = 0; z <= size; z++) {
			for (int x = 0; x <= size; x++) {
				
				//add vertex and normal
				ind[x][z] = mLOD.addVertex(globalPos(x, xPos), heightAt(x, z), globalPos(z, zPos));
				Vec3 normal = normalAt(x, z);
				mLOD.addNormal(normal.x, normal.y, normal.z);

				// mLOD.addNormal(0, 1, 0);
			}
		}

		Random r = new Random(32);

		int[] tri_vt_1a = new int[] { mLOD.addTexCoord(0, 1), mLOD.addTexCoord(1, 1), mLOD.addTexCoord(0, 0) };
		int[] tri_vt_1b = new int[] { mLOD.addTexCoord(1, 1), mLOD.addTexCoord(1, 0), mLOD.addTexCoord(0, 0) };
		int[] tri_vt_2a = new int[] { mLOD.addTexCoord(0, 1), mLOD.addTexCoord(1, 1), mLOD.addTexCoord(1, 0) };
		int[] tri_vt_2b = new int[] { mLOD.addTexCoord(0, 1), mLOD.addTexCoord(1, 0), mLOD.addTexCoord(0, 0) };

		// add polygons by theorized indexed values ^^;
		for (int z = 0; z < size; z++) {
			for (int x = 0; x < size; x++) {
				int[] tri0, tri1;

				if (r.nextBoolean()) {
					tri0 = new int[] { ind[x + 1][z], ind[x][z], ind[x + 1][z + 1] };
					tri1 = new int[] { ind[x][z], ind[x][z + 1], ind[x + 1][z + 1] };
					mLOD.addPolygon(tri0, tri_vt_1a, tri0, null);
					mLOD.addPolygon(tri1, tri_vt_1b, tri1, null);
				} else {
					tri0 = new int[] { ind[x + 1][z], ind[x][z], ind[x][z + 1] };
					tri1 = new int[] { ind[x + 1][z], ind[x][z + 1], ind[x + 1][z + 1] };
					mLOD.addPolygon(tri0, tri_vt_2a, tri0, null);
					mLOD.addPolygon(tri1, tri_vt_2b, tri1, null);
				}

			}
		}

		return mLOD;
	}

	// lowest LOD is 2 triangles for the whole context
	private MeshLOD lowLOD() {
		MeshLOD mLOD = new MeshLOD(3, 4, 5, 1, 5, 1);

		// first create vectors and normals
		for (int z = 1; z <= size + 1; z += size) {
			for (int x = 1; x <= size + 1; x += size) {
				float h = heightmap[x][z] * vertScale;

				mLOD.addVertex(x * horzScale + (xPos * size * horzScale), h, z * horzScale + (zPos * size * horzScale));

				// try to construct normal from noise pseudo-derivative
				Vec3 d0 = Vec3.create(-2, heightmap[x - 1][z] * vertScale - h, 0).unit();
				Vec3 d1 = Vec3.create(0, heightmap[x][z + 1] * vertScale - h, 2).unit();
				Vec3 d2 = Vec3.create(2, heightmap[x + 1][z] * vertScale - h, 0).unit();
				Vec3 d3 = Vec3.create(0, heightmap[x][z - 1] * vertScale - h, -2).unit();

				Vec3 normal = (d0.cross(d1).add(d1.cross(d2)).add(d2.cross(d3)).add(d3.cross(d0))).unit();

				mLOD.addNormal(normal.x, normal.y, normal.z);
			}
		}

		// add polygons by theorized indexed values ^^;
		int[] tri0 = new int[] { 1, 4, 2 };
		int[] tri1 = new int[] { 1, 3, 4 };

		mLOD.addPolygon(tri0, null, tri0, null);
		mLOD.addPolygon(tri1, null, tri1, null);

		return mLOD;
	}
}
