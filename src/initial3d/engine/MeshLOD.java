package initial3d.engine;

import initial3d.*;
import initial3d.linearmath.Vector4D;

public class MeshLOD {

	final PolygonBuffer polys;
	final VectorBuffer vertices;
	final VectorBuffer normals;
	final VectorBuffer texcoords;
	final VectorBuffer vcolors;

	private static final double[][] zerovector = new double[4][1];
	private static final double[][] onevector = Vector4D.create(1, 1, 1, 1);

	public MeshLOD(int maxpolys, int maxpolyvertices, int maxvertices, int maxtexcoords, int maxnormals, int maxvcolors) {
		if (maxvertices < 1 || maxtexcoords < 1 || maxnormals < 1 || maxvcolors < 1)
			throw new IllegalArgumentException(
					"Minimum size for vector buffers is 1 (the first element is reserved as it must always be valid).");
		this.polys = Initial3D.createPolygonBuffer(maxpolys, maxpolyvertices);
		this.vertices = Initial3D.createVectorBuffer(maxvertices);
		this.texcoords = Initial3D.createVectorBuffer(maxtexcoords);
		this.normals = Initial3D.createVectorBuffer(maxnormals);
		this.vcolors = Initial3D.createVectorBuffer(maxtexcoords);
		vertices.put(zerovector);
		texcoords.put(zerovector);
		normals.put(zerovector);
		vcolors.put(onevector);
	}

	public int addVertex(double x, double y, double z) {
		return vertices.put(Vector4D.create(x, y, z, 1));
	}

	public int addNormal(double nx, double ny, double nz) {
		return normals.put(Vector4D.create(nx, ny, nz, 0));
	}

	public int addTexCoord(double u, double v) {
		return texcoords.put(Vector4D.create(u, v, 1, 0));
	}

	public int addVertexColor(double r, double g, double b) {
		return vcolors.put(Vector4D.create(r, g, b, 1));
	}

	public void addPolygon(int[] v, int[] vt, int[] vn, int[] vc) {
		polys.addPolygon(v, vt, vn, vc);
	}

}
