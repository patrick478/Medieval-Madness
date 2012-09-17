package initial3d.renderer;

import initial3d.Initial3D;
import initial3d.PolygonBuffer;
import initial3d.Texture;
import initial3d.VectorBuffer;

public class Initial3DFactory {

	private Initial3DFactory() {
		throw new AssertionError();
	}

	public static final Initial3D createInitial3DInstance() {
		return new Initial3DImpl();
	}

	// vector and polygon buffer objects

	public static final VectorBuffer createVectorBuffer(int capacity) {
		return new VectorBufferImpl(capacity);
	}

	public static final PolygonBuffer createPolygonBuffer(int capacity, int maxvertices) {
		return new PolygonBufferImpl(capacity, maxvertices);
	}
	
	public static final Texture createTexture(int size) {
		return new TextureImpl(size);
	}

}
