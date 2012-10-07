package game.modelloader;

import java.io.InputStream;

public abstract class AbstractContentLoader {
	public abstract Object Load(String file);
	public abstract Object Load(InputStream file);
	
}
