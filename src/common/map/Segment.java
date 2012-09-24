package common.map;

import initial3d.Initial3D;
import initial3d.Texture;
import initial3d.engine.Color;
import initial3d.engine.Material;
import initial3d.engine.Mesh;
import initial3d.engine.MeshContext;
import initial3d.engine.MeshLOD;
import initial3d.engine.Vec3;

import java.util.Random;

public class Segment {
	public static final int size = 16;
	public static final int vertScale = 8;
	public static final int horzScale = 2;
	private static final int terr_tex_size = 256;
	
	private float[][] heightmap = null;
	
	private final long xPos;
	private final long zPos;
	
	public Segment(long _xPos, long _zPos, float[][] heightmap)
	{
		xPos = _xPos;
		zPos = _zPos;
		
		this.heightmap = heightmap;
	}
	
	public float[][] getData()
	{
		return this.heightmap;
	}
	
	public MeshContext getMeshContext(){
		Mesh terr_mesh = new Mesh();
		
		terr_mesh.add(highLOD());
		
		
		Material terr_mtl = new Material(new Color(0.4f, 0.4f, 0.4f), new Color(0.1f, 0.1f, 0.1f), 1f);
		Texture terr_tx = Initial3D.createTexture(terr_tex_size);
		//populate the terrain texture
		for (int u = 0; u < terr_tex_size; u++) {
			for (int v = 0; v < terr_tex_size; v++) {
				terr_tx.setPixel(u, v, 1f, 0.3f, (float) (Math.random() * 0.4 + 0.3), 0.3f);
			}
		}
		terr_tx.composeMipMaps();
		terr_tx.useMipMaps(true);
		
		terr_mtl = new Material(terr_mtl, terr_tx, null, null);
		
		MeshContext terr_mc = new MeshContext(terr_mesh, terr_mtl);
		
		return terr_mc;
	}
	
	private MeshLOD highLOD(){
		
		MeshLOD mLOD = new MeshLOD(size * size * 2*2, 3, (size+1) * (size+1)*2, 20, (size+1) * (size+1)*2, 1);

		int[][] ind = new int[(size + 2)][(size + 2)];
		
		//first create vectors and normals
		for (int z = 1; z <= size + 1; z++) {
			for (int x = 1; x <= size + 1; x++) {
				float h = heightmap[x][z];
				
				ind[x-1][z-1] = mLOD.addVertex(x * horzScale + (xPos * size * horzScale), h * vertScale, z * horzScale + (zPos * size * horzScale));

				// try to construct normal from noise pseudo-derivative
				Vec3 d0 = Vec3.create(-1, heightmap[x-1][z]- h, 0).unit();
				Vec3 d1 = Vec3.create(0, heightmap[x][z-1]- h, -1).unit();
				Vec3 d2 = Vec3.create(1, heightmap[x+1][z] - h, 0).unit();
				Vec3 d3 = Vec3.create(0, heightmap[x][z+1] - h, 1).unit();

				Vec3 normal = (d0.cross(d1).add(d1.cross(d2)).add(d2.cross(d3)).add(d3.cross(d0))).neg().unit();

				mLOD.addNormal(normal.x, normal.y, normal.z);
				
//				mLOD.addNormal(0, 1, 0);
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

		Mesh mesh = new Mesh();
		mesh.add(mLOD);

		return mLOD;
	}
}
