package initial3d.renderer;

import initial3d.PolygonBuffer;

final class PolygonBufferImpl extends PolygonBuffer {

	private final int[] pdata;
	private final int capacity, maxv, stride;
	private int count = 0;

	// stride is in ints

	// poly format: [vcount, -, -, -, v0, vt0, vn0, vc0, v..., vt..., vn..., vc...] - indices into VectorBuffer objects

	public PolygonBufferImpl(int capacity_, int maxv_) {
		capacity = capacity_;
		maxv = maxv_;
		stride = maxv * 4 + 4;
		pdata = new int[capacity * stride];
	}

	@Override
	public final int count() {
		return count;
	}

	@Override
	public final int capacity() {
		return capacity;
	}

	@Override
	public final int maxVertices() {
		return maxv;
	}

	@Override
	public final void addPolygon(int[] v, int[] vt, int[] vn, int[] vc) {
		if (count == capacity) throw new IndexOutOfBoundsException();
		if (v == null) throw new IllegalArgumentException();
		if ((vn != null && v.length != vn.length) || (vt != null && v.length != vt.length)
				|| (vc != null && v.length != vc.length)) throw new IllegalArgumentException("Incorrect size for vt/vn/vc.");
		int vcount = v.length;
		if (vcount > maxv) throw new IllegalArgumentException("Too many vertices.");

		// write vertex count
		int i = count * stride;
		pdata[i] = vcount;

		// write vertex data
		i += 4;
		for (int j = 0; j < vcount; j++) {
			pdata[i] = v[j];
			if (vt != null) pdata[i + 1] = vt[j];
			if (vn != null) pdata[i + 2] = vn[j];
			if (vc != null) pdata[1 + 3] = vc[j];
			i += 4;
		}
		count++;
	}

	// package-private
	final int[] polygonData() {
		return pdata;
	}

	// package-private
	final int stride() {
		return stride;
	}

}
