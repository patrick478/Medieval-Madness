package initial3d.renderer;

import sun.misc.Unsafe;
import initial3d.Texture;

class TextureImpl extends Texture {

	private final Unsafe unsafe = Util.getUnsafe();

	private final int level;
	private final int size;
	private final long pTex;
	private final long pLevel;

	TextureImpl(int size_) {
		int level_ = 0;
		switch (size_) {
		case 1024:
			level_++;
		case 512:
			level_++;
		case 256:
			level_++;
		case 128:
			level_++;
		case 64:
			level_++;
		case 32:
			level_++;
		case 16:
			level_++;
		case 8:
			level_++;
		case 4:
			level_++;
		case 2:
			level_++;
		case 1:
			break;
		default:
			throw new IllegalArgumentException("Illegal size for texture: " + size_);
		}
		level = level_;
		size = size_;
		pTex = unsafe.allocateMemory((long) (8 + size * size * 16 * 1.4d));
		pLevel = pTex + levelOffset(level);
		unsafe.putInt(pTex, level);
	}

	protected void finalize() {
		unsafe.freeMemory(pTex);
	}

	private static int levelOffset(int level) {
		switch (level) {
		case 0:
			return 8;
		case 1:
			return 24;
		case 2:
			return 88;
		case 3:
			return 344;
		case 4:
			return 1368;
		case 5:
			return 5464;
		case 6:
			return 21848;
		case 7:
			return 87384;
		case 8:
			return 349528;
		case 9:
			return 1398104;
		case 10:
			return 5592408;
		default:
			// could possibly put generic formula here
			return 8;
		}
	}

	/* package-private */
	static int getTextureOffset(float u_, float v_, int level) {
		if (level == 0) return 8;
		int u = (int) (u_ * 65536f);
		int v = (int) (v_ * 65536f);
		// this code is set up for 16.16 fixed point u and v values
		// indexing into 16 bytes per element
		switch (level) {
		case 1:
			return 24 + (((v & 32768) >> 10) | ((u & 32768) >> 11));
		case 2:
			return 88 + (((v & 49152) >> 8) | ((u & 49152) >> 10));
		case 3:
			return 344 + (((v & 57344) >> 6) | ((u & 57344) >> 9));
		case 4:
			return 1368 + (((v & 61440) >> 4) | ((u & 61440) >> 8));
		case 5:
			return 5464 + (((v & 63488) >> 2) | ((u & 63488) >> 7));
		case 6:
			return 21848 + ((v & 64512) | ((u & 64512) >> 6));
		case 7:
			return 87384 + (((v & 65024) << 2) | ((u & 65024) >> 5));
		case 8:
			return 349528 + (((v & 65280) << 4) | ((u & 65280) >> 4));
		case 9:
			return 1398104 + (((v & 65408) << 6) | ((u & 65408) >> 3));
		case 10:
			return 5592408 + (((v & 65472) << 8) | ((u & 65472) >> 2));
		default:
			// could possibly put generic formula here
			return 8;
		}
	}

	/* package-private */
	long getTexturePointer() {
		return pTex;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public float getPixel(int u, int v, Channel ch) {
		u &= (size - 1);
		v &= (size - 1);
		return unsafe.getFloat(pLevel + (v * size + u) * 16 + ch.ordinal() * 4);
	}

	@Override
	public void setPixel(int u, int v, float a, float r, float g, float b) {
		u &= (size - 1);
		v &= (size - 1);
		unsafe.putFloat(pLevel + (v * size + u) * 16, a);
		unsafe.putFloat(pLevel + (v * size + u) * 16 + 4, r);
		unsafe.putFloat(pLevel + (v * size + u) * 16 + 8, g);
		unsafe.putFloat(pLevel + (v * size + u) * 16 + 12, b);
	}

	@Override
	public void setPixel(int u, int v, Channel ch, float val) {
		u &= (size - 1);
		v &= (size - 1);
		unsafe.putFloat(pLevel + (v * size + u) * 16 + ch.ordinal() * 4, val);
	}

	@Override
	public void useMipMaps(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public void composeMipMaps() {
		// TODO Auto-generated method stub

	}

}
