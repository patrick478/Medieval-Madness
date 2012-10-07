package initial3d.engine;

import initial3d.*;
import initial3d.linearmath.Vector4D;

public class MeshLOD {

	/* package-private */
	final PolygonBuffer polys;
	final VectorBuffer vertices;
	final VectorBuffer normals;
	final VectorBuffer texcoords;
	final VectorBuffer vcolors;

	private static final double[][] zerovector = new double[4][1];
	private static final double[][] onevector = Vector4D.create(1, 1, 1, 1);

	/**
	 * Construct a MeshLOD with the given parameters.
	 * 
	 * @param maxpolys
	 *            The maximum number of polygons able to be held.
	 * @param maxpolyvertices
	 *            The maximum number of vertices allowed per polygon.
	 * @param maxvertices
	 *            The maximum number of total vertices able to be held.
	 * @param maxtexcoords
	 *            The maximum number of total texture coordinates able to be held.
	 * @param maxnormals
	 *            The maximum number of total normal vectors able to be held.
	 * @param maxvcolors
	 *            The maximum number of total vertex colors able to be held.
	 */
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

	/**
	 * Add a vertex described by the given vector. Returns the index to reference it with when using it in a polygon.
	 * Returned indices start at 1 and increment by 1 with each call. Returned indices are independent of any other
	 * method calls.
	 */
	public int addVertex(double x, double y, double z) {
		return vertices.put(Vector4D.create(x, y, z, 1));
	}

	/**
	 * Add a normal vector described by the given vector. Returns the index to reference it with when using it in a
	 * polygon. Returned indices start at 1 and increment by 1 with each call. Returned indices are independent of any
	 * other method calls.
	 */
	public int addNormal(double nx, double ny, double nz) {
		return normals.put(Vector4D.create(nx, ny, nz, 0));
	}

	/**
	 * Add a texture coordinate described by the given vector. Returns the index to reference it with when using it in a
	 * polygon. Returned indices start at 1 and increment by 1 with each call. Returned indices are independent of any
	 * other method calls.
	 */
	public int addTexCoord(double u, double v) {
		return texcoords.put(Vector4D.create(u, v, 1, 0));
	}

	/**
	 * Add a vertex color described by the given vector. Returns the index to reference it with when using it in a
	 * polygon. Returned indices start at 1 and increment by 1 with each call. Returned indices are independent of any
	 * other method calls.
	 */
	public int addVertexColor(double r, double g, double b) {
		return vcolors.put(Vector4D.create(r, g, b, 1));
	}

	/**
	 * Add a polygon described by the given arrays. Each array index corresponds to one vertex of the polygon. All
	 * arrays, if not null, must be the same length. The values in the arrays correspond to the indices returned by the
	 * other add methods.
	 * 
	 * @param v
	 *            An array of vertex indices describing the vertices that make up this poly. Must be non-null.
	 * @param vt
	 *            An array of texture coordinate indices describing the texture coordinates to apply to the
	 *            corresponding vertices. Can be null.
	 * @param vn
	 *            An array of vertex normal indices describing the normal vectors to apply to the corresponding
	 *            vertices. Can be null.
	 * @param vc
	 *            An array of vertex color indices describing the colors to apply to the corresponding vertices. Can be
	 *            null. This is for precomputed lighting (just leave it null guys).
	 */
	public void addPolygon(int[] v, int[] vt, int[] vn, int[] vc) {
		polys.addPolygon(v, vt, vn, vc);
	}

	@Deprecated
	public PolygonBuffer getPolys() {
		return polys;
	}

	@Deprecated
	public VectorBuffer getVertices() {
		return vertices;
	}

	@Deprecated
	public VectorBuffer getNormals() {
		return normals;
	}

	@Deprecated
	public VectorBuffer getTexcoords() {
		return texcoords;
	}

}
