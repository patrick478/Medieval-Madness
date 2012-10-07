package game.modelloader;

import java.io.InputStream;

import initial3d.engine.Mesh;

public abstract class AbstractModelLoader {
	public abstract Mesh Load(String file);
	public abstract Mesh Load(InputStream file);
	
}
