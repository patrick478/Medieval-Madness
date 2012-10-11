package game.modelloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class ImageLoader extends AbstractContentLoader {

	@Override
	public Object Load(String filename)
	{
		return new File(filename);
	}

	@Override
	public Object Load(InputStream file) {
		throw new UnsupportedOperationException();
	}

}
