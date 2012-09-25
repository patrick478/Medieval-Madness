package initial3d.engine;

import java.awt.image.BufferedImage;

public interface DisplayTarget {

	public void display(BufferedImage bi);
	
	public int getDisplayWidth();
	
	public int getDisplayHeight();

}
