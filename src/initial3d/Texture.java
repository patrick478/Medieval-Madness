package initial3d;

/** A square, power-of-two sized texture. */
public abstract class Texture {

	public static enum Channel {
		ALPHA, RED, GREEN, BLUE;
	}

	public abstract int size();
	
	public abstract void clear();

	public abstract float getPixel(int u, int v, Channel ch);

	public abstract void setPixel(int u, int v, float a, float r, float g, float b);

	public abstract void setPixel(int u, int v, Channel ch, float val);

	public abstract void useMipMaps(boolean b);

	public abstract void composeMipMaps();

}
