package initial3d;

import java.awt.image.BufferedImage;

/**
 * A square, power-of-two sized texture. Note that operations 'wrap' around the edge of the texture, except for those
 * that explicitly don't. For precision puposes, methods in this class that take <code>u</code> and <code>v</code> as
 * parameters expect integers where an increment of 1 corresponds to one texel. u,v == 0,0 corresponds to the top-left
 * of the texture.
 */
public abstract class Texture {

	public static enum Channel {
		ALPHA, RED, GREEN, BLUE;
	}

	/** Get the size, in texels, of one dimension of the texture. */
	public abstract int size();

	/** Clear the entire texture, including the useMipMaps flag. */
	public abstract void clear();

	public abstract float getTexel(int u, int v, Channel ch);

	public abstract void setTexel(int u, int v, float a, float r, float g, float b);

	public abstract void setTexel(int u, int v, Channel ch, float val);

	public abstract void useMipMaps(boolean b);

	public abstract void composeMipMaps();

	public void setTexelNoWrap(int u, int v, float a, float r, float g, float b) {
		if (u < 0 || u >= size()) return;
		if (v < 0 || v >= size()) return;
		setTexel(u, v, a, r, g, b);
	}

	public void setTexelNoWrap(int u, int v, Channel ch, float val) {
		if (u < 0 || u >= size()) return;
		if (v < 0 || v >= size()) return;
		setTexel(u, v, ch, val);
	}

	public void clear(int u, int v, int usize, int vsize) {
		int umax = u + usize;
		int vmax = v + vsize;
		for (; u < umax; u++) {
			for (; v < vmax; v++) {
				setTexel(u, v, 0f, 0f, 0f, 0f);
			}
		}
	}

	public void clearNoWrap(int u, int v, int usize, int vsize) {
		int umax = u + usize;
		int vmax = v + vsize;
		for (; u < umax; u++) {
			if (u < 0 || u >= size()) continue;
			for (; v < vmax; v++) {
				if (v < 0 || v >= size()) continue;
				setTexel(u, v, 0f, 0f, 0f, 0f);
			}
		}
	}

	/**
	 * Draw an image onto this texture. No scaling takes place.
	 * 
	 * @param u
	 *            The u coordinate to start drawing at
	 * @param v
	 *            The v coordinate to start drawing at
	 * @param imgx
	 *            The x coordinate in the image to start copying from
	 * @param imgy
	 *            The y coordinate in the image to start copying from
	 * @param imgw
	 *            The width, in pixels, of image data to copy
	 * @param imgh
	 *            The height, in pixels, of image data to copy.
	 * @param img
	 *            The BufferedImage to copy from
	 */
	public void drawImage(int u, int v, int imgx, int imgy, int imgw, int imgh, BufferedImage img) {
		float i255 = 1 / 255f;
		int imgxmax = imgx + imgw;
		int imgymax = imgy + imgh;
		for (; imgx < imgxmax; u++, imgx++) {
			for (; imgy < imgymax; v++, imgy++) {
				int rgb = img.getRGB(imgx, imgy);
				setTexel(u, v, Channel.BLUE, (rgb & 0xFF) * i255);
				setTexel(u, v, Channel.GREEN, ((rgb >>>= 8) & 0xFF) * i255);
				setTexel(u, v, Channel.RED, ((rgb >>>= 8) & 0xFF) * i255);
				setTexel(u, v, Channel.ALPHA, ((rgb >>>= 8) & 0xFF) * i255);
			}
		}
	}

	/**
	 * Draw an image onto this texture, without wrapping around the edges. No scaling takes place.
	 * 
	 * @param u
	 *            The u coordinate to start drawing at
	 * @param v
	 *            The v coordinate to start drawing at
	 * @param imgx
	 *            The x coordinate in the image to start copying from
	 * @param imgy
	 *            The y coordinate in the image to start copying from
	 * @param imgw
	 *            The width, in pixels, of image data to copy
	 * @param imgh
	 *            The height, in pixels, of image data to copy.
	 * @param img
	 *            The BufferedImage to copy from
	 */
	public void drawImageNoWrap(int u, int v, int imgx, int imgy, int imgw, int imgh, BufferedImage img) {
		float i255 = 1 / 255f;
		int imgxmax = imgx + imgw;
		int imgymax = imgy + imgh;
		for (; imgx < imgxmax; u++, imgx++) {
			if (u < 0 || u >= size()) continue;
			for (; imgy < imgymax; v++, imgy++) {
				if (v < 0 || v >= size()) continue;
				int rgb = img.getRGB(imgx, imgy);
				setTexel(u, v, Channel.BLUE, (rgb & 0xFF) * i255);
				setTexel(u, v, Channel.GREEN, ((rgb >>>= 8) & 0xFF) * i255);
				setTexel(u, v, Channel.RED, ((rgb >>>= 8) & 0xFF) * i255);
				setTexel(u, v, Channel.ALPHA, ((rgb >>>= 8) & 0xFF) * i255);
			}
		}
	}

	/**
	 * Draw an image onto this texture. Bad scaling takes place.
	 * 
	 * @param u
	 *            The u coordinate to start drawing at
	 * @param v
	 *            The v coordinate to start drawing at
	 * @param usize
	 *            The width in texels to scale the image to
	 * @param vsize
	 *            The height in texels to scale the image to
	 * @param imgx
	 *            The x coordinate in the image to start copying from
	 * @param imgy
	 *            The y coordinate in the image to start copying from
	 * @param imgw
	 *            The width, in pixels, of image data to copy
	 * @param imgh
	 *            The height, in pixels, of image data to copy.
	 * @param img
	 *            The BufferedImage to copy from
	 */
	public void drawImage(int u, int v, int usize, int vsize, int imgx, int imgy, int imgw, int imgh, BufferedImage img) {
		float i255 = 1 / 255f;

		double dimgx = imgw / (double) Math.abs(usize);
		double dimgy = imgh / (double) Math.abs(vsize);
		double imgx_cd = 0;
		double imgy_cd = 0;

		int imgxmax = imgx + imgw;
		int imgymax = imgy + imgh;
		for (; imgx < imgxmax; u += (usize > 0 ? 1 : -1)) {
			for (; imgy < imgymax; v += (vsize > 0 ? 1 : -1)) {
				int rgb = img.getRGB(imgx, imgy);
				setTexel(u, v, Channel.BLUE, (rgb & 0xFF) * i255);
				setTexel(u, v, Channel.GREEN, ((rgb >>>= 8) & 0xFF) * i255);
				setTexel(u, v, Channel.RED, ((rgb >>>= 8) & 0xFF) * i255);
				setTexel(u, v, Channel.ALPHA, ((rgb >>>= 8) & 0xFF) * i255);

				double imgy_new = imgy + imgy_cd + dimgy;
				imgy = (int) (imgy_new);
				imgy_cd = imgy_new - imgy;
			}
			double imgx_new = imgx + imgx_cd + dimgx;
			imgx = (int) (imgx_new);
			imgx_cd = imgx_new - imgx;
		}
	}

	/**
	 * Draw an image onto this texture, without wrapping around the edges. Bad scaling takes place.
	 * 
	 * @param u
	 *            The u coordinate to start drawing at
	 * @param v
	 *            The v coordinate to start drawing at
	 * @param usize
	 *            The width in texels to scale the image to
	 * @param vsize
	 *            The height in texels to scale the image to
	 * @param imgx
	 *            The x coordinate in the image to start copying from
	 * @param imgy
	 *            The y coordinate in the image to start copying from
	 * @param imgw
	 *            The width, in pixels, of image data to copy
	 * @param imgh
	 *            The height, in pixels, of image data to copy.
	 * @param img
	 *            The BufferedImage to copy from
	 */
	public void drawImageNoWrap(int u, int v, int usize, int vsize, int imgx, int imgy, int imgw, int imgh,
			BufferedImage img) {
		float i255 = 1 / 255f;

		double dimgx = imgw / (double) Math.abs(usize);
		double dimgy = imgh / (double) Math.abs(vsize);
		double imgx_cd = 0;
		double imgy_cd = 0;

		int imgxmax = imgx + imgw;
		int imgymax = imgy + imgh;
		for (; imgx < imgxmax; u += (usize > 0 ? 1 : -1)) {
			for (; imgy < imgymax; v += (vsize > 0 ? 1 : -1)) {
				int rgb = img.getRGB(imgx, imgy);
				setTexelNoWrap(u, v, Channel.BLUE, (rgb & 0xFF) * i255);
				setTexelNoWrap(u, v, Channel.GREEN, ((rgb >>>= 8) & 0xFF) * i255);
				setTexelNoWrap(u, v, Channel.RED, ((rgb >>>= 8) & 0xFF) * i255);
				setTexelNoWrap(u, v, Channel.ALPHA, ((rgb >>>= 8) & 0xFF) * i255);

				double imgy_new = imgy + imgy_cd + dimgy;
				imgy = (int) (imgy_new);
				imgy_cd = imgy_new - imgy;
			}
			double imgx_new = imgx + imgx_cd + dimgx;
			imgx = (int) (imgx_new);
			imgx_cd = imgx_new - imgx;
		}
	}

	/** Draw the entirety of an image (badly) scaled to fit this entire texture. */
	public void drawImage(BufferedImage img) {
		drawImage(0, 0, size(), size(), 0, 0, img.getWidth(), img.getHeight(), img);
	}

}
