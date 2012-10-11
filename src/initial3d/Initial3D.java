package initial3d;

import initial3d.renderer.Initial3DFactory;

import java.awt.image.BufferedImage;

/**
 * This class defines the <code><i><b>Initial3D</b></i></code> rendering API.
 * 
 * Colors, when specified as a sequence of floats in the API, are RGBA (although internally are generally ARGB).
 * 
 * THIS IS VERY MUCH A WORK IN PROGRESS.
 * 
 * 
 * @author Ben Allen
 */
public abstract class Initial3D {

	public static final Initial3D createInstance() {
		return Initial3DFactory.createInitial3DInstance();
	}

	public static final VectorBuffer createVectorBuffer(int capacity) {
		return Initial3DFactory.createVectorBuffer(capacity);
	}

	public static final PolygonBuffer createPolygonBuffer(int capacity, int maxvertices) {
		return Initial3DFactory.createPolygonBuffer(capacity, maxvertices);
	}
	
	public static final Texture createTexture(int size) {
		return Initial3DFactory.createTexture(size);
	}

	// shared
	public static final int ZERO = 0;
	public static final int ONE = 1;
	public static final int TRUE = 0xFFFFFFFF;

	// state flags
	public static final long ALPHA_TEST = 0x1L;
	public static final long BLEND = 0x2L;
	public static final long CULL_FACE = 0x4L;
	public static final long DEPTH_TEST = 0x8L;
	public static final long FOG = 0x10L;
	public static final long LIGHTING = 0x20L;
	public static final long TWO_SIDED_LIGHTING = 0x40L;
	// 0x80L;
	// 0x100L;
	// 0x200L;
	// 0x400L;
	// 0x800L;
	// 0x1000L;
	// 0x2000L;
	public static final long MIPMAPS = 0x4000L;
	public static final long STENCIL_TEST = 0x8000L;
	public static final long TEXTURE_2D = 0x10000L;
	public static final long AUTO_ZFLIP = 0x20000L;
	public static final long WRITE_FRAME = 0x40000L;
	public static final long WRITE_COLOR = 0x80000L;
	public static final long WRITE_Z = 0x100000L;
	public static final long WRITE_STENCIL = 0x200000L;
	public static final long WRITE_ID = 0x400000L;
	public static final long LIGHT0 = 0x800000L;
	// LIGHT_MAX must be >= LIGHT0 && < 0x1000000L so lights don't interfere with other flags
	// 0x1000000L;
	
	// alpharef_random (might be interesting combined with changing material opacity)
	// - otherwise load from diffuse material
	public static final long ALPHAREF_RANDOM = 0x2000000L;
	// 0x4000000L;
	// 0x8000000L;
	// sort (polygons, in drawPolys()) front to back
	public static final long SORT_FRONT_TO_BACK = 0x10000000L;

	// shademodels
	public static final int SHADEMODEL_FLAT = 1;
	public static final int SHADEMODEL_GOURARD = 2;
	public static final int SHADEMODEL_PHONG = 3;

	// blend func parameters
	// share ZERO, ONE
	public static final int SRC_COLOR = 2;
	public static final int ONE_MINUS_SRC_COLOR = 3;
	public static final int DST_COLOR = 4;
	public static final int ONE_MINUS_DST_COLOR = 5;
	public static final int SRC_ALPHA = 6;
	public static final int ONE_MINUS_SRC_ALPHA = 7;
	public static final int DST_ALPHA = 8;
	public static final int ONE_MINUS_DST_ALPHA = 9;

	// buffers
	public static final int FRAME_BUFFER_BIT = 0x1;
	public static final int COLOR_BUFFER_BIT = 0x2;
	public static final int Z_BUFFER_BIT = 0x4;
	public static final int STENCIL_BUFFER_BIT = 0x8;
	public static final int ID_BUFFER_BIT = 0x10;

	// comparison functions
	public static final int NEVER = 0;
	public static final int LESS = 1;
	public static final int LEQUAL = 2;
	public static final int GREATER = 3;
	public static final int GEQUAL = 4;
	public static final int EQUAL = 5;
	public static final int NOTEQUAL = 6;
	public static final int ALWAYS = 7;

	// stencil ops
	// stencil buffer as unsigned ints... supposedly
	// share ZERO
	public static final int KEEP = 1;
	public static final int REPLACE = 2;
	public static final int INCR = 3; // clamp to MAX_INT
	public static final int INCR_WRAP = 4; // wrap to 0
	public static final int DECR = 5; // clamp to 0
	public static final int DECR_WRAP = 6; // wrap to MAX_INT
	public static final int INVERT = 7; // bitwise invert

	// face flags
	public static final int FRONT = 1;
	public static final int BACK = 2;
	public static final int FRONT_AND_BACK = 3;

	// light and material
	public static final int AMBIENT = 0;
	public static final int DIFFUSE = 1;
	public static final int SPECULAR = 2;
	public static final int EMISSION = 3;
	// opacity in diffuse alpha, shininess in specular alpha
	// but you can set them seperately of the color
	public static final int SHININESS = 4;
	public static final int OPACITY = 5;
	public static final int POSITION = 6;
	public static final int SPOT_DIRECTION = 7;
	@Deprecated
	public static final int INTENSITY = 8;
	public static final int CONSTANT_ATTENUATION = 9;
	public static final int LINEAR_ATTENUATION = 10;
	public static final int QUADRATIC_ATTENUATION = 11;
	public static final int SPOT_CUTOFF = 12;
	public static final int SPOT_EXPONENT = 13;
	public static final int EFFECT_RADIUS = 14;

	// matrices accessible via matrixmode
	public static final int MODEL = 0;
	public static final int VIEW = 1;
	public static final int PROJ = 2;
	public static final int MODELVIEW = 3;
	public static final int VIEWPROJ = 4;
	public static final int MODELVIEWPROJ = 5;
	public static final int MODEL_INV = 6;
	public static final int VIEW_INV = 7;
	public static final int PROJ_INV = 8;
	public static final int MODELVIEW_INV = 9;
	public static final int VIEWPROJ_INV = 10;
	public static final int MODELVIEWPROJ_INV = 11;

	// projection types
	public static final int ORTHOGRAPHIC = 0;
	public static final int PERSPECTIVE = 1;

	// begin
	// (0 means ended) <-- ???
	public static final int LINE_STRIP = 1;
	public static final int LINE_LOOP = 2;
	public static final int POLYGON = 3;

	// poly modes
	public static final int POLY_FILL = 0;
	public static final int POLY_OUTLINE = 1;
	
	public abstract Profiler getProfiler();
	
	public abstract void initFog(float fog_a, float fog_b);
	
	public abstract void fogColorfv(float[] v);

	public abstract void viewportSize(int w, int h);
	
	public abstract void nearClip(double z);
	
	public abstract void farCull(double z);

	public abstract void begin(int mode);

	public abstract void vertex3d(double vx, double vy, double vz);

	public abstract void normal3d(double nx, double ny, double nz);

	public abstract void color3d(double r, double g, double b);

	public abstract void texCoord2d(double u, double v);

	public abstract void end();

	public abstract void materialfv(int face, int pname, float[] v);

	public abstract void materialf(int face, int pname, float v);
	
	public abstract void texImage2D(int face, Texture map_kd, Texture map_ks, Texture map_ke);

	public abstract void objectID(int id);

	/**
	 * Copy color buffer data to frame buffer (clamping), or blend if enabled. Flip zsign if <code>AUTO_ZFLIP</code>
	 * enabled and <code>WRITE_Z</code> enabled.
	 */
	public abstract void finish();
	
	public abstract void flipZSign();

	public abstract void clear(int bflags);

	public abstract void vertexData(VectorBuffer vbuf);

	public abstract void normalData(VectorBuffer vnbuf);

	public abstract void colorData(VectorBuffer vcbuf);

	public abstract void texCoordData(VectorBuffer vtbuf);

	public abstract void drawPolygons(PolygonBuffer pbuf, int startindex, int count);

	public abstract void blendFunc(int sfactor, int dfactor);

	public abstract void alphaFunc(int func, float ref);

	public abstract void depthFunc(int func);

	public abstract void stencilFunc(int func, int ref, int mask);

	public abstract void stencilOp(int sfail, int dfail, int dpass);
	
	public abstract int maxLights();

	public abstract void lightf(long light, int pname, float v);

	public abstract void lightfv(long light, int pname, float[] v);

	public abstract void lightdv(long light, int pname, double[] v);

	public abstract void sceneAmbientfv(float[] v);

	public abstract void cullFace(int face);

	public abstract void enable(long state);

	public abstract void disable(long state);

	public abstract boolean isEnabled(long state);

	public abstract void projectionMode(int mode);

	public abstract void polygonMode(int face, int mode);

	public abstract void shadeModel(int model);

	// matrices

	public abstract void transformOne(double[][] target, double[][] right);

	public abstract void pushMatrix(); // copy current to new

	public abstract void popMatrix();

	public abstract void matrixMode(int mode);

	public abstract void loadPerspectiveFOV(double near, double far, double fov, double ratio);

	public abstract void loadIdentity();

	public abstract void loadMatrix(double[][] m);

	public abstract void multMatrix(double[][] m); // left multiply by m

	public abstract void transposeMatrix();

	public abstract void extractMatrix(double[][] m);

	// transforms - applied to contents of active matrix

	public abstract void translateX(double d);

	public abstract void translateY(double d);

	public abstract void translateZ(double d);

	public abstract void scale(double f);

	public abstract void rotateX(double r);

	public abstract void rotateY(double r);

	public abstract void rotateZ(double r);

	// image? stuff

	// TODO this method is shit change it
	public abstract void extractBuffer(int bufferbit, BufferedImage bi);

	public abstract int queryBuffer(int bufferbit, int x, int y);
	
	public abstract void useFrameBuffer(int[] framebuffer, int stride);

}
