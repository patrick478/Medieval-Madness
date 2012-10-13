package game.modelloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class ImageLoader extends AbstractContentLoader {

	@Override
	public Object Load(String filename)
	{
		try {
			return ImageIO.read(new File(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Object Load(InputStream file) {
		throw new UnsupportedOperationException();
	}
}
