package initial3d;

public abstract class Texture {

	public abstract int getSize();
	
	public abstract int getWidth();
	
	public abstract int getHeight();
	
	public abstract int getPixel(int u, int v);
	
	public abstract int setPixel(int u, int v, int col);
	
	public abstract void composeMipMaps();
	
}
