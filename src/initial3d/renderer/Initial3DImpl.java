package initial3d.renderer;

import static initial3d.renderer.Util.*;
import initial3d.*;
import initial3d.linearmath.*;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
class Initial3DImpl extends Initial3D {

	static final int MEM_SIZE = 268435456;

	protected final Unsafe unsafe;
	protected final long pBase;

	protected final AbstractMatrixStack[] matrixstacks = new AbstractMatrixStack[12];

	private AbstractMatrixStack activeStack;

	protected final VectorBuffer defaultvectorbuf;
	protected final VectorBuffer defaultvectorcolorbuf;
	protected VectorBuffer v_vbo, vt_vbo, vn_vbo, vc_vbo;

	protected final double[][] zerovector = new double[4][1];
	protected final double[][] onevector = new double[][] { { 1d }, { 1d }, { 1d }, { 1d } };

	protected VectorBuffer begin_v_vbo, begin_vt_vbo, begin_vn_vbo, begin_vc_vbo;

	protected int[] protopoly = new int[256];

	protected PolygonPipeline polypipe;
	protected Finisher finisher;

	Initial3DImpl() {
		unsafe = Util.getUnsafe();
		pBase = unsafe.allocateMemory(MEM_SIZE);

		// init zsign
		putInt(0x00000068, 1);

		matrixstacks[MODEL] = new MatrixStack();
		matrixstacks[VIEW] = new MatrixStack();
		matrixstacks[PROJ] = new MatrixStack();
		matrixstacks[MODELVIEW] = new ComposedMatrixStack(matrixstacks[VIEW], matrixstacks[MODEL]);
		matrixstacks[VIEWPROJ] = new ComposedMatrixStack(matrixstacks[PROJ], matrixstacks[VIEW]);
		matrixstacks[MODELVIEWPROJ] = new ComposedMatrixStack(matrixstacks[PROJ], matrixstacks[MODELVIEW]);

		matrixstacks[MODEL_INV] = new InverseMatrixStack(matrixstacks[MODEL]);
		matrixstacks[VIEW_INV] = new InverseMatrixStack(matrixstacks[VIEW]);
		matrixstacks[PROJ_INV] = new InverseMatrixStack(matrixstacks[PROJ]);
		matrixstacks[MODELVIEW_INV] = new ComposedMatrixStack(matrixstacks[MODEL_INV], matrixstacks[VIEW_INV]);
		matrixstacks[VIEWPROJ_INV] = new ComposedMatrixStack(matrixstacks[VIEW_INV], matrixstacks[PROJ_INV]);
		matrixstacks[MODELVIEWPROJ_INV] = new ComposedMatrixStack(matrixstacks[MODEL_INV], matrixstacks[VIEWPROJ_INV]);

		activeStack = matrixstacks[VIEW];

		defaultvectorbuf = createVectorBuffer(1);
		defaultvectorbuf.put(zerovector);
		defaultvectorcolorbuf = createVectorBuffer(1);
		defaultvectorcolorbuf.put(onevector);

		v_vbo = defaultvectorbuf;
		vt_vbo = defaultvectorbuf;
		vn_vbo = defaultvectorbuf;
		vc_vbo = defaultvectorcolorbuf;

		begin_v_vbo = createVectorBuffer(64);
		begin_v_vbo.put(zerovector);
		begin_vt_vbo = createVectorBuffer(64);
		begin_vt_vbo.put(zerovector);
		begin_vn_vbo = createVectorBuffer(64);
		begin_vn_vbo.put(zerovector);
		begin_vc_vbo = createVectorBuffer(64);
		begin_vc_vbo.put(onevector);

		polypipe = new PolygonPipeline(2);
		finisher = new Finisher(2);

		// enable default states
		enable(CULL_FACE | DEPTH_TEST | AUTO_ZFLIP | WRITE_FRAME | WRITE_COLOR | WRITE_Z);
		polygonMode(FRONT_AND_BACK, POLY_FILL);
		shadeModel(SHADEMODEL_FLAT);
		depthFunc(LESS);
		cullFace(BACK);
		materialf(FRONT_AND_BACK, OPACITY, 1f);
		materialf(FRONT_AND_BACK, SHININESS, 1f);
		sceneAmbientfv(new float[] { 0.1f, 0.1f, 0.1f });
	}

	@Override
	protected void finalize() {
		unsafe.freeMemory(pBase);
	}

	protected void putInt(long q, int val) {
		unsafe.putInt(pBase + q, val);
	}

	protected int getInt(long q) {
		return unsafe.getInt(pBase + q);
	}

	protected void putFloat(long q, float val) {
		unsafe.putFloat(pBase + q, val);
	}

	protected float getFloat(long q) {
		return unsafe.getFloat(pBase + q);
	}

	protected void putLong(long q, long val) {
		unsafe.putLong(pBase + q, val);
	}

	protected long getLong(long q) {
		return unsafe.getLong(pBase + q);
	}

	protected void putDouble(long q, double val) {
		unsafe.putDouble(pBase + q, val);
	}

	protected double getDouble(long q) {
		return unsafe.getDouble(pBase + q);
	}

	protected void initPipelineGeneral() {
		// copy matrices over
		long pMatrix = 0x00080900 + pBase;
		double[][] m = Matrix.create(4, 4);
		for (int matrix = MODEL; matrix <= MODELVIEWPROJ_INV; matrix++) {
			matrixstacks[matrix].extractMatrix(m);
			Util.copyMatrixFrom2D_unsafe(unsafe, pMatrix, m, 4, 4);
			pMatrix += 128;
		}

		// compute view space clip equations (depend on matrices)
		computeProjectionClipFunc(unsafe, pBase, pBase + 0x000B0500, 1, 0, 0, 1, 0, 1, 1, 1, 1); // left
		computeProjectionClipFunc(unsafe, pBase, pBase + 0x000B0540, -1, 0, 0, -1, 1, 0, -1, 1, 1); // right
		computeProjectionClipFunc(unsafe, pBase, pBase + 0x000B0580, 0, 1, 0, 1, 1, 0, 1, 1, 1); // top
		computeProjectionClipFunc(unsafe, pBase, pBase + 0x000B05C0, 0, -1, 0, -1, -1, 0, -1, -1, 1); // bottom
	}

	@Override
	public void viewportSize(int w, int h) {
		putInt(0x00000000, w);
		putInt(0x00000004, h);
		// clear all the buffers
		clear(TRUE);
	}

	@Override
	public void materialf(int face, int pname, float v) {
		if (face < FRONT || face > FRONT_AND_BACK) throw new IllegalArgumentException();
		int qMtlFront = 0x00000900;
		int qMtlBack = 0x00040900;
		switch (pname) {
		case SHININESS:
			if ((face & FRONT) != 0) putFloat(qMtlFront + 32, v);
			if ((face & BACK) != 0) putFloat(qMtlBack + 32, v);
			break;
		case OPACITY:
			if ((face & FRONT) != 0) putFloat(qMtlFront + 16, v);
			if ((face & BACK) != 0) putFloat(qMtlBack + 16, v);
			break;
		default:
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void materialfv(int face, int pname, float[] v) {
		if (face < FRONT || face > FRONT_AND_BACK) throw new IllegalArgumentException();
		int qMtlFront = 0x00000900;
		int qMtlBack = 0x00040900;
		switch (pname) {
		case SPECULAR:
			if (v.length == 4) {
				if ((face & FRONT) != 0) putFloat(qMtlFront + 32, v[3]);
				if ((face & BACK) != 0) putFloat(qMtlBack + 32, v[3]);
			}
			if ((face & FRONT) != 0) putFloat(qMtlFront + 36, v[0]);
			if ((face & FRONT) != 0) putFloat(qMtlFront + 40, v[1]);
			if ((face & FRONT) != 0) putFloat(qMtlFront + 44, v[2]);
			if ((face & BACK) != 0) putFloat(qMtlBack + 36, v[0]);
			if ((face & BACK) != 0) putFloat(qMtlBack + 40, v[1]);
			if ((face & BACK) != 0) putFloat(qMtlBack + 44, v[2]);
			break;
		case DIFFUSE:
			if (v.length == 4) {
				if ((face & FRONT) != 0) putFloat(qMtlFront + 16, v[3]);
				if ((face & BACK) != 0) putFloat(qMtlBack + 16, v[3]);
			}
			if ((face & FRONT) != 0) putFloat(qMtlFront + 20, v[0]);
			if ((face & FRONT) != 0) putFloat(qMtlFront + 24, v[1]);
			if ((face & FRONT) != 0) putFloat(qMtlFront + 28, v[2]);
			if ((face & BACK) != 0) putFloat(qMtlBack + 20, v[0]);
			if ((face & BACK) != 0) putFloat(qMtlBack + 24, v[1]);
			if ((face & BACK) != 0) putFloat(qMtlBack + 28, v[2]);
			break;
		case AMBIENT:
			if ((face & FRONT) != 0) putFloat(qMtlFront + 4, v[0]);
			if ((face & FRONT) != 0) putFloat(qMtlFront + 8, v[1]);
			if ((face & FRONT) != 0) putFloat(qMtlFront + 12, v[2]);
			if ((face & BACK) != 0) putFloat(qMtlBack + 4, v[0]);
			if ((face & BACK) != 0) putFloat(qMtlBack + 8, v[1]);
			if ((face & BACK) != 0) putFloat(qMtlBack + 12, v[2]);
			break;
		case EMISSION:
			if ((face & FRONT) != 0) putFloat(qMtlFront + 52, v[0]);
			if ((face & FRONT) != 0) putFloat(qMtlFront + 56, v[1]);
			if ((face & FRONT) != 0) putFloat(qMtlFront + 60, v[2]);
			if ((face & BACK) != 0) putFloat(qMtlBack + 52, v[0]);
			if ((face & BACK) != 0) putFloat(qMtlBack + 56, v[1]);
			if ((face & BACK) != 0) putFloat(qMtlBack + 60, v[2]);
			break;
		default:
			throw new IllegalArgumentException();
		}

	}

	@Override
	public void objectID(int id) {
		putInt(0x00000064, id);
	}

	@Override
	public void flipZSign() {
		putInt(0x00000068, getInt(0x00000068) * -1);
	}

	@Override
	public void clear(int bflags) {
		if ((bflags & FRAME_BUFFER_BIT) > 0) clearBuffer(0x00DC0900, 0x1000000);
		if ((bflags & COLOR_BUFFER_BIT) > 0) clearBuffer(0x01DC0900, 0x4000000);
		if ((bflags & Z_BUFFER_BIT) > 0) clearBuffer(0x05DC0900, 0x1000000);
		if ((bflags & STENCIL_BUFFER_BIT) > 0) clearBuffer(0x06E00900, 0x1000000);
		if ((bflags & ID_BUFFER_BIT) > 0) clearBuffer(0x07E00900, 0x1000000);
	}

	/** Clear a region of memory. Size must be a multiple of 8. */
	protected void clearBuffer(long qBuffer, long size) {
		unsafe.setMemory(pBase + qBuffer, size, (byte) 0);
	}

	@Override
	public void vertexData(VectorBuffer vbuf) {
		if (vbuf == null) {
			v_vbo = defaultvectorbuf;
			return;
		}
		if (vbuf.count() >= 1) {
			v_vbo = vbuf;
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void normalData(VectorBuffer vnbuf) {
		if (vnbuf == null) {
			vn_vbo = defaultvectorbuf;
			return;
		}
		if (vnbuf.count() >= 1) {
			vn_vbo = vnbuf;
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void colorData(VectorBuffer vcbuf) {
		if (vcbuf == null) {
			vc_vbo = defaultvectorcolorbuf;
			return;
		}
		if (vcbuf.count() >= 1) {
			vt_vbo = vcbuf;
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void texCoordData(VectorBuffer vtbuf) {
		if (vtbuf == null) {
			vt_vbo = defaultvectorbuf;
			return;
		}
		if (vtbuf.count() >= 1) {
			vt_vbo = vtbuf;
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void blendFunc(int sfactor, int dfactor) {
		// TODO
	}

	@Override
	public void alphaFunc(int func, float ref) {
		// TODO
	}

	@Override
	public void depthFunc(int func) {
		// TODO
	}

	@Override
	public void stencilFunc(int func, int ref, int mask) {
		// TODO
	}

	@Override
	public void stencilOp(int sfail, int dfail, int dpass) {
		// TODO
	}

	protected final long getLightQPointer(long light) {
		if (light < LIGHT0 || light > LIGHT7) throw new IllegalArgumentException();
		long qLight = 0x00000100;
		if (light >= LIGHT7) qLight += 256;
		if (light >= LIGHT6) qLight += 256;
		if (light >= LIGHT5) qLight += 256;
		if (light >= LIGHT4) qLight += 256;
		if (light >= LIGHT3) qLight += 256;
		if (light >= LIGHT2) qLight += 256;
		if (light >= LIGHT1) qLight += 256;
		return qLight;
	}

	@Override
	public void lightf(long light, int pname, float v) {
		long qLight = getLightQPointer(light);
		switch (pname) {
		case SPOT_CUTOFF:
			putFloat(qLight + 80, v);
			break;
		case INTENSITY:
			putFloat(qLight + 84, v);
			break;
		default:
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void lightfv(long light, int pname, float[] v) {
		long qLight = getLightQPointer(light);
		switch (pname) {
		case AMBIENT:
			putFloat(qLight + 4, v[0]);
			putFloat(qLight + 8, v[1]);
			putFloat(qLight + 12, v[2]);
			break;
		case DIFFUSE:
			putFloat(qLight + 20, v[0]);
			putFloat(qLight + 24, v[1]);
			putFloat(qLight + 28, v[2]);
			break;
		case SPECULAR:
			putFloat(qLight + 36, v[0]);
			putFloat(qLight + 40, v[1]);
			putFloat(qLight + 44, v[2]);
			break;
		default:
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void lightdv(long light, int pname, double[] v) {
		// for position and spot direction
		long qLight = getLightQPointer(light);

		// transform by modelview

		double[][] v0 = new double[][] { { v[0] }, { v[1] }, { v[2] },
				{ pname == SPOT_DIRECTION ? 0 : v.length > 3 ? v[3] : 1 } };
		double[][] v1 = Matrix.create(4, 1);
		matrixstacks[MODELVIEW].transformOne(v1, v0);

		switch (pname) {
		case POSITION:
			// shouldn't be necessary to homogenise, but...
			// I mean, who has a non-affine view transform?
			if (Math.abs(v1[3][0]) > 0.001) {
				Vector4D.homogenise(v1, v1);
			} else {
				Vector3D.normalise(v1, v1);
			}
			putFloat(qLight + 48, (float) v1[0][0]);
			putFloat(qLight + 52, (float) v1[1][0]);
			putFloat(qLight + 56, (float) v1[2][0]);
			putFloat(qLight + 60, (float) v1[3][0]);
			break;
		case SPOT_DIRECTION:
			Vector3D.normalise(v1, v1);
			putFloat(qLight + 64, (float) v1[0][0]);
			putFloat(qLight + 68, (float) v1[1][0]);
			putFloat(qLight + 72, (float) v1[2][0]);
			putFloat(qLight + 76, (float) v1[3][0]);
			break;
		default:
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void sceneAmbientfv(float[] v) {
		if (v.length < 3) throw new IllegalArgumentException();
		if (v.length == 4) putFloat(0x0000006C, v[3]);
		putFloat(0x0000006C + 4, v[0]);
		putFloat(0x0000006C + 8, v[1]);
		putFloat(0x0000006C + 12, v[2]);
	}

	@Override
	public void cullFace(int face) {
		if (face < 0 || face > 3) throw new IllegalArgumentException();
		putInt(0x0000002C, face);
	}

	@Override
	public void enable(long state) {
		state = getLong(0x00000008) | state;
		putLong(0x00000008, state);
	}

	@Override
	public void disable(long state) {
		state = getLong(0x00000008) & ~state;
		putLong(0x00000008, state);
	}

	@Override
	public boolean isEnabled(long state) {
		return (getLong(0x00000008) & state) > 0;
	}

	@Override
	public void projectionMode(int mode) {
		if (mode < ORTHOGRAPHIC || mode > PERSPECTIVE) throw new IllegalArgumentException();
		putInt(0x00000030, mode);
	}

	@Override
	public void polygonMode(int face, int mode) {
		if (face < FRONT || face > FRONT_AND_BACK || mode < POLY_FILL || mode > POLY_OUTLINE)
			throw new IllegalArgumentException();
		if ((face & FRONT) != 0) {
			putInt(0x0000003C, mode);
		}
		if ((face & BACK) != 0) {
			putInt(0x00000040, mode);
		}
	}

	@Override
	public void shadeModel(int model) {
		if (model < SHADEMODEL_FLAT || model > SHADEMODEL_PHONG) throw new IllegalArgumentException();
		putInt(0x00000034, model);
	}

	/*
	 * 
	 * Matrix Operations
	 */

	@Override
	public void transformOne(double[][] target, double[][] right) {
		activeStack.transformOne(target, right);
	}

	@Override
	public void pushMatrix() {
		activeStack.pushMatrix();
	}

	@Override
	public void popMatrix() {
		activeStack.popMatrix();
	}

	@Override
	public void matrixMode(int mode) {
		if (mode >= MODEL && mode <= MODELVIEWPROJ_INV) {
			activeStack = matrixstacks[mode];
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void loadPerspectiveFOV(double near, double far, double fov, double ratio) {
		activeStack.loadMatrix(getMatrixPerspectiveFOV(near, far, fov, ratio));
	}

	@Override
	public void loadIdentity() {
		activeStack.loadIdentity();
	}

	@Override
	public void loadMatrix(double[][] m) {
		activeStack.loadMatrix(m);
	}

	@Override
	public void multMatrix(double[][] m) {
		activeStack.multMatrix(m);
	}

	@Override
	public void transposeMatrix() {
		activeStack.transposeMatrix();
	}

	@Override
	public void extractMatrix(double[][] m) {
		activeStack.extractMatrix(m);
	}

	@Override
	public void translateX(double d) {
		activeStack.translateX(d);
	}

	@Override
	public void translateY(double d) {
		activeStack.translateY(d);
	}

	@Override
	public void translateZ(double d) {
		activeStack.translateZ(d);
	}

	@Override
	public void scale(double f) {
		activeStack.scale(f);
	}

	@Override
	public void rotateX(double r) {
		activeStack.rotateX(r);
	}

	@Override
	public void rotateY(double r) {
		activeStack.rotateY(r);
	}

	@Override
	public void rotateZ(double r) {
		activeStack.rotateZ(r);
	}

	/** Create a perspective projection matrix. */
	public static final double[][] getMatrixPerspectiveFOV(double near, double far, double fov, double ratio) {
		double[][] m = Matrix.create(4, 4);
		double fov_ = Math.cos(fov / 2d) / Math.sin(fov / 2d);
		m[0][0] = fov_ / ratio;
		m[1][1] = fov_;
		m[2][2] = (far + near) / (far - near);
		m[2][3] = (2 * near * far) / (near - far);
		m[3][2] = 1d;
		return m;
	}

	@Override
	public void extractBuffer(int bufferbit, BufferedImage bi) {
		// this method is so full of assumptions it's not funny...
		int[] bidata = ((DataBufferInt) (bi.getRaster().getDataBuffer())).getData();
		// get framebuffer if nothing else
		long pBuffer = 0x00DC0900 + pBase;
		if (bufferbit == Z_BUFFER_BIT) pBuffer = 0x05DC0900 + pBase;
		if (bufferbit == STENCIL_BUFFER_BIT) pBuffer = 0x06E00900 + pBase;
		if (bufferbit == ID_BUFFER_BIT) pBuffer = 0x07E00900 + pBase;

		unsafe.copyMemory(null, pBuffer, bidata, unsafe.arrayBaseOffset(int[].class), bidata.length * 4);

	}

	@Override
	public int queryBuffer(int bufferbit, int x, int y) {
		// TODO
		return 0;
	}

	@Override
	public void begin(int mode) {
		if (mode < LINE_STRIP || mode > POLYGON) throw new IllegalArgumentException();

		// set begin mode
		putInt(0x00000038, mode);

		// clear vector buffers
		// always need valid zero-index vector
		begin_v_vbo.clear();
		begin_v_vbo.put(zerovector);
		begin_vt_vbo.clear();
		begin_vt_vbo.put(zerovector);
		begin_vn_vbo.clear();
		begin_vn_vbo.put(zerovector);
		begin_vc_vbo.clear();
		begin_vc_vbo.put(onevector);

		if (mode == POLYGON) {
			// clear polygon
			protopoly[0] = 0;
		}

		if (mode == LINE_STRIP) {
			// TODO begin line strip
		}

		if (mode == LINE_LOOP) {
			// TODO begin line loop
		}

	}

	@Override
	public void vertex3d(double vx, double vy, double vz) {
		begin_v_vbo.put(new double[][] { { vx }, { vy }, { vz } });

		// bind to previously set normal / texcoord
		if (true /* TODO mode = poly */) {
			int vcount = protopoly[0] + 1;
			protopoly[0] = vcount;
			protopoly[vcount * 4] = begin_v_vbo.count() - 1;
			protopoly[vcount * 4 + 1] = begin_vt_vbo.count() - 1;
			protopoly[vcount * 4 + 2] = begin_vn_vbo.count() - 1;
			protopoly[vcount * 4 + 3] = begin_vc_vbo.count() - 1;
		}
	}

	@Override
	public void normal3d(double nx, double ny, double nz) {
		begin_vn_vbo.put(new double[][] { { nx }, { ny }, { nz } });
	}

	@Override
	public void color3d(double r, double g, double b) {
		// alpha of 1
		begin_vc_vbo.put(new double[][] { { r }, { g }, { b }, { 1d } });
	}

	@Override
	public void texCoord2d(double u, double v) {
		begin_vt_vbo.put(new double[][] { { u }, { v } });
	}

	@Override
	public void end() {
		initPipelineGeneral();
		// transform using modelviewproj
		Util.multiply4VectorBlock_pos_unsafe(unsafe, pBase + 0x000C0900, begin_v_vbo.count(),
				((VectorBufferImpl) begin_v_vbo).getBasePointer(), pBase + 0x00080B80);
		// also transform using modelview (for lighting and stuff)
		Util.multiply4VectorBlock_pos_unsafe(unsafe, pBase + 0x003C0900, begin_v_vbo.count(),
				((VectorBufferImpl) begin_v_vbo).getBasePointer(), pBase + 0x00080A80);
		// use modelview for normals
		Util.multiply4VectorBlock_norm_unsafe(unsafe, pBase + 0x00240900, begin_vn_vbo.count(),
				((VectorBufferImpl) begin_vn_vbo).getBasePointer(), pBase + 0x00080A80);
		// copy texture vertices
		Util.copy4VectorBlock_unsafe(unsafe, pBase + 0x00540900, ((VectorBufferImpl) begin_vt_vbo).getBasePointer(),
				begin_vt_vbo.count());
		// copy vertex colors
		Util.copy4VectorBlock_unsafe(unsafe, pBase + 0x006C0900, ((VectorBufferImpl) begin_vc_vbo).getBasePointer(),
				begin_vc_vbo.count());

		int mode = getInt(0x00000038);

		if (mode == POLYGON) {
			polypipe.processPolygons(unsafe, pBase, protopoly, 0, 256, 1);
		}

		if (mode == LINE_STRIP) {
			// TODO end line strip
		}

		if (mode == LINE_LOOP) {
			// TODO end line loop
		}
	}

	@Override
	public void drawPolygons(PolygonBuffer pbuf, int startindex, int count) {
		if (startindex + count > pbuf.count()) throw new IllegalArgumentException();
		if (count < 1) throw new IllegalArgumentException();
		initPipelineGeneral();
		// transform using modelviewproj
		Util.multiply4VectorBlock_pos_unsafe(unsafe, pBase + 0x000C0900, v_vbo.count(),
				((VectorBufferImpl) v_vbo).getBasePointer(), pBase + 0x00080B80);
		// also transform using modelview (for lighting and stuff)
		Util.multiply4VectorBlock_pos_unsafe(unsafe, pBase + 0x003C0900, v_vbo.count(),
				((VectorBufferImpl) v_vbo).getBasePointer(), pBase + 0x00080A80);
		// use modelview for normals
		Util.multiply4VectorBlock_norm_unsafe(unsafe, pBase + 0x00240900, vn_vbo.count(),
				((VectorBufferImpl) vn_vbo).getBasePointer(), pBase + 0x00080A80);
		// copy texture vertices
		Util.copy4VectorBlock_unsafe(unsafe, pBase + 0x00540900, ((VectorBufferImpl) vt_vbo).getBasePointer(),
				vt_vbo.count());
		// copy vertex colors
		Util.copy4VectorBlock_unsafe(unsafe, pBase + 0x006C0900, ((VectorBufferImpl) vc_vbo).getBasePointer(),
				vc_vbo.count());

		PolygonBufferImpl pbuf_ = (PolygonBufferImpl) pbuf;
		polypipe.processPolygons(unsafe, pBase, pbuf_.polygonData(), pbuf_.stride() * startindex, pbuf_.stride(), count);
	}

	@Override
	public void finish() {
		finisher.finish(unsafe, pBase);
	}
	
	@Override
	public void finish(int[] framebuffer) {
		finisher.finish_array(unsafe, pBase, framebuffer);
	}

}
