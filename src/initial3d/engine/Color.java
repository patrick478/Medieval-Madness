package initial3d.engine;

import static initial3d.renderer.Util.*;

/** Represents a Color with floats for r, g and b ranged [0-1]. */
public class Color {
	
	public static final Color BLACK = new Color(0f, 0f, 0f);
	public static final Color GRAY = new Color(0.5f, 0.5f, 0.5f);
	public static final Color WHITE = new Color(1f, 1f, 1f);
	public static final Color RED = new Color(1f, 0f, 0f);
	public static final Color GREEN = new Color(0f, 1f, 0f);
	public static final Color BLUE = new Color(0f, 0f, 1f);
	public static final Color YELLOW = new Color(1f, 1f, 0f);
	public static final Color ORANGE = new Color(1f, 0.58f, 0f);
	public static final Color DARK_RED = new Color(1f, 0.50f, 0.50f);

	public final float r, g, b;
	public final int rgb24;

	/** Construct a Color from three floats [0-1]. */
	public Color(float r_, float g_, float b_) {
		r = clamp(r_, 0f, 1f);
		g = clamp(g_, 0f, 1f);
		b = clamp(b_, 0f, 1f);
		rgb24 = ((int)(r * 255f) << 16) | ((int)(g * 255f) << 8) | (int)(b * 255f);
	}
	
	/** Construct a Color from three ints [0-255]. */
	public Color(int r_, int g_, int b_) {
		r = clamp(r_ / 255f, 0f, 1f);
		g = clamp(g_ / 255f, 0f, 1f);
		b = clamp(b_ / 255f, 0f, 1f);
		rgb24 = ((int)(r * 255f) << 16) | ((int)(g * 255f) << 8) | (int)(b * 255f);
	}

	/** Construct a Color from a packed RGB24 int. */
	public Color(int rgb) {
		rgb24 = rgb & 0x00FFFFFF;
		b = (rgb & 0xFF) / 255f;
		rgb >>>= 8;
		g = (rgb & 0xFF) / 255f;
		rgb >>>= 8;
		r = (rgb & 0xFF) / 255f;
	}
	
	public float[] toArray(float[] c) {
		c[0] = r;
		c[1] = g;
		c[2] = b;
		return c;
	}
	
	public float[] toArray() {
		return toArray(new float[3]);
	}

}
