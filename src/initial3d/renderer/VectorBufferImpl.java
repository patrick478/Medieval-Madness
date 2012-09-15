package initial3d.renderer;

import initial3d.VectorBuffer;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
class VectorBufferImpl extends VectorBuffer {

	private Unsafe unsafe = Util.getUnsafe();
	private final long pBase;
	private int count = 0;
	private int capacity;

	public VectorBufferImpl(int capacity_) {
		capacity = capacity_;
		pBase = unsafe.allocateMemory(capacity_ * 32);
	}
	
	@Override
	public final int capacity() {
		return capacity;
	}

	@Override
	protected void finalize() {
		unsafe.freeMemory(pBase);
	}

	@Override
	public int count() {
		return count;
	}

	@Override
	public void clear() {
		count = 0;
	}

	@Override
	public int put(double[][] v) {
		return put(count, v);
	}

	@Override
	public int put(int index, double[][] v) {
		// TODO throw a better exception type here
		if (count == capacity) throw new IndexOutOfBoundsException();
		if (index < 0 || index > count) throw new IndexOutOfBoundsException();
		for (int i = 0; i < Math.min(4, v.length); i++) {
			unsafe.putDouble(pBase + (index * 4 + i) * 8, v[i][0]);
		}
		if (index == count) count++;
		return index;
	}

	@Override
	public void get(int index, double[][] v) {
		if (index < 0 || index >= count) throw new IndexOutOfBoundsException();
		for (int i = 0; i < Math.min(4, v.length); i++) {
			v[i][0] = unsafe.getDouble(pBase + (index * 4 + i) * 8);
		}
	}
	
	// package-private
	long getBasePointer() {
		return pBase;
	}

}