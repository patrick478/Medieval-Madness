package initial3d.renderer;

import initial3d.Profiler;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
class Finisher {

	private final FinisherWorker[] workers;
	private final FinisherArrayWorker[] arrayworkers;
	private final Profiler profiler;

	public Finisher(int threads, Profiler profiler_) {
		profiler = profiler_;
		workers = new FinisherWorker[threads];
		arrayworkers = new FinisherArrayWorker[threads];
		for (int i = 0; i < threads; i++) {
			workers[i] = new FinisherWorker();
			workers[i].start();
			arrayworkers[i] = new FinisherArrayWorker();
			arrayworkers[i].start();
		}
	}

	/** Entry point. */
	final void finish(Unsafe unsafe, long pBase) {

		profiler.startSection("I3D_finish");

		final long flags = unsafe.getLong(pBase + 0x00000008);

		// parallel finishing
		int totallines = unsafe.getInt(pBase + 0x00000004);
		// lines per thread, as a multiple of 8
		int lines = (totallines / workers.length) & ~7;
		int Yi = 0;
		for (int i = 0; i < workers.length - 1; i++, Yi += lines) {
			workers[i].beginFinishRegion(unsafe, pBase, Yi, Yi + lines);
		}
		workers[workers.length - 1].beginFinishRegion(unsafe, pBase, Yi, totallines);

		// wait for completion
		for (FinisherWorker fw : workers) {
			fw.waitUntilDone();
		}

		// autozflip
		if ((flags & 0x20000L) != 0) {
			unsafe.putInt(pBase + 0x00000068, -unsafe.getInt(pBase + 0x00000068));
		}

		profiler.endSection("I3D_finish");
	}

	private static final void finishRegion(Unsafe unsafe, long pBase, int Yi, int Yf) {
		final long flags = unsafe.getLong(pBase + 0x00000008);
		final int width = unsafe.getInt(pBase + 0x00000000);
		final int height = unsafe.getInt(pBase + 0x00000004);
		final float zsign = unsafe.getInt(pBase + 0x00000068);

		long pFrame = pBase + 0x00DC0900 + Yi * width * 4; // int, packed ARGB
		long pColor = pBase + 0x01DC0900 + Yi * width * 16; // float4, ARGB
		long pZ = pBase + 0x05DC0900 + Yi * width * 4; // float
		long pFrameEnd = pFrame + width * (Yf - Yi) * 4;

		boolean zwrite = ((flags & 0x100000L) != 0);
		// if frame write enabled
		if ((flags & 0x40000L) != 0) {
			while (pFrame < pFrameEnd) {
				float invz = unsafe.getFloat(pZ);
				if (invz * zsign < 0 && zwrite) {
					// nothing written this frame
					// only if zwrite enabled
					unsafe.putFloat(pZ, zsign);
					unsafe.putInt(pFrame, 0x00000000);
					pZ += 4;
					pFrame += 4;
					pColor += 16;
					continue;
				}

				float a = unsafe.getFloat(pColor);
				float r = unsafe.getFloat(pColor += 4);
				float g = unsafe.getFloat(pColor += 4);
				float b = unsafe.getFloat(pColor += 4);
				pColor += 4;

				// convert to int and clamp down (shouldn't have to clamp up to zero...)
				int ia = a >= 1f ? 255 : (int) (a * 255f);
				int ir = r >= 1f ? 255 : (int) (r * 255f);
				int ig = g >= 1f ? 255 : (int) (g * 255f);
				int ib = b >= 1f ? 255 : (int) (b * 255f);

				// pack and write to frame buffer
				unsafe.putInt(pFrame, (ia << 24) | (ir << 16) | (ig << 8) | ib);

				pFrame += 4;
				pZ += 4;
			}
		}
	}

	/** Entry point for using array as framebuffer. */
	final void finish_array(Unsafe unsafe, long pBase, int[] framebuffer) {

		profiler.startSection("I3D_finish-array");

		final long flags = unsafe.getLong(pBase + 0x00000008);

		// parallel finishing
		int totallines = unsafe.getInt(pBase + 0x00000004);
		// lines per thread, as a multiple of 8
		int lines = (totallines / arrayworkers.length) & ~7;
		int Yi = 0;
		for (int i = 0; i < arrayworkers.length - 1; i++, Yi += lines) {
			arrayworkers[i].beginFinishRegion_array(unsafe, pBase, framebuffer, Yi, Yi + lines);
		}
		arrayworkers[workers.length - 1].beginFinishRegion_array(unsafe, pBase, framebuffer, Yi, totallines);

		// wait for completion
		for (FinisherArrayWorker fw : arrayworkers) {
			fw.waitUntilDone();
		}

		// autozflip
		if ((flags & 0x20000L) != 0) {
			unsafe.putInt(pBase + 0x00000068, -unsafe.getInt(pBase + 0x00000068));
		}

		profiler.endSection("I3D_finish-array");
	}

	private static final void finishRegion_array(Unsafe unsafe, long pBase, int[] framebuffer, int Yi, int Yf) {
		final long flags = unsafe.getLong(pBase + 0x00000008);
		final int width = unsafe.getInt(pBase + 0x00000000);
		final int height = unsafe.getInt(pBase + 0x00000004);
		final float zsign = unsafe.getInt(pBase + 0x00000068);

		long pColor = pBase + 0x01DC0900 + Yi * width * 16; // float4, ARGB
		long pZ = pBase + 0x05DC0900 + Yi * width * 4; // float
		long pFC = pBase + 0x08E00900 + Yi * width * 16; // float4

		long arrayoffset = unsafe.arrayBaseOffset(int[].class) + Yi * width * 4;
		long arrayend = arrayoffset + width * (Yf - Yi) * 4;

		float fog_r = 0.8f;
		float fog_g = 0.3f;
		float fog_b = 0.3f;
		int fog_col = 0xFF000000 | ((int) (fog_r * 255f) << 16) | ((int) (fog_g * 255f) << 8) | (int) (fog_b * 255f);

		boolean zwrite = ((flags & 0x100000L) != 0);
		// if frame write enabled
		if ((flags & 0x40000L) != 0) {
			while (arrayoffset < arrayend) {
				float iZ = unsafe.getFloat(pZ);
				iZ *= zsign;
				if (iZ < 0 && zwrite) {
					// nothing written this frame
					// only if zwrite enabled
					unsafe.putFloat(pZ, zsign);
					// unsafe.putInt(pFrame, 0x00000000);
					unsafe.putInt(framebuffer, arrayoffset, fog_col);
					pZ += 4;
					arrayoffset += 4;
					pColor += 16;
					pFC += 16;
					continue;
				}

				// read fog correction factors
				float f_a = unsafe.getFloat(pFC + 4);
				float f_b = unsafe.getFloat(pFC + 8);
				pFC += 16;

				// 'fog constants' a, b
				float fogmul = Math.min(Math.max(f_a - f_b * iZ, 0), 255f);

				float a = unsafe.getFloat(pColor);
				float r = unsafe.getFloat(pColor += 4);
				float g = unsafe.getFloat(pColor += 4);
				float b = unsafe.getFloat(pColor += 4);
				pColor += 4;

				// convert to int and clamp down (shouldn't have to clamp up to zero...)
				// int ia = a >= 1f ? 255 : (int) (a * 255f);
				float c_mul = 255f - fogmul;
				int ir = Math.min((int) (r * c_mul + fog_r * fogmul), 255);
				int ig = Math.min((int) (g * c_mul + fog_g * fogmul), 255);
				int ib = Math.min((int) (b * c_mul + fog_b * fogmul), 255);

				// int col = unsafe.getInt(pColor);
				// pColor += 16;
				//
				// int ib = col & 0xFF;
				// col >>>= 8;
				// int ig = col & 0xFF;
				// col >>>= 8;
				// int ir = col & 0xFF;
				// col >>>= 8;
				// int ia = col;

				// pack and write to frame buffer
				// unsafe.putInt(pFrame, (ia << 24) | (ir << 16) | (ig << 8) | ib);
				unsafe.putInt(framebuffer, arrayoffset, 0xFF000000 | (ir << 16) | (ig << 8) | ib);

				arrayoffset += 4;
				pZ += 4;
			}
		}

	}

	private static class FinisherWorker extends Thread {

		private final Object wait_begin = new Object();
		private final Object wait_done = new Object();

		// rasterisation params
		private volatile Unsafe unsafe;
		private volatile long pBase;
		private volatile int Yi, Yf;

		private volatile boolean dofinish = false;

		public FinisherWorker() {
			setDaemon(true);
		}

		public void beginFinishRegion(Unsafe unsafe, long pBase, int Yi, int Yf) {
			this.unsafe = unsafe;
			this.pBase = pBase;
			this.Yi = Yi;
			this.Yf = Yf;
			synchronized (wait_begin) {
				dofinish = true;
				wait_begin.notify();
			}
		}

		public void waitUntilDone() {
			synchronized (wait_done) {
				while (dofinish) {
					try {
						wait_done.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		public void run() {
			while (true) {
				try {
					synchronized (wait_begin) {
						while (!dofinish) {
							wait_begin.wait();
						}
					}
					// execute parallel method
					finishRegion(unsafe, pBase, Yi, Yf);
					synchronized (wait_done) {
						dofinish = false;
						wait_done.notify();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static class FinisherArrayWorker extends Thread {

		private final Object wait_begin = new Object();
		private final Object wait_done = new Object();

		// rasterisation params
		private volatile Unsafe unsafe;
		private volatile int[] framebuffer;
		private volatile long pBase;
		private volatile int Yi, Yf;

		private volatile boolean dofinish = false;

		public FinisherArrayWorker() {
			setDaemon(true);
		}

		public void beginFinishRegion_array(Unsafe unsafe, long pBase, int[] framebuffer, int Yi, int Yf) {
			this.unsafe = unsafe;
			this.pBase = pBase;
			this.framebuffer = framebuffer;
			this.Yi = Yi;
			this.Yf = Yf;
			synchronized (wait_begin) {
				dofinish = true;
				wait_begin.notify();
			}
		}

		public void waitUntilDone() {
			synchronized (wait_done) {
				while (dofinish) {
					try {
						wait_done.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		@Override
		public void run() {
			while (true) {
				try {
					synchronized (wait_begin) {
						while (!dofinish) {
							wait_begin.wait();
						}
					}
					// execute parallel method
					finishRegion_array(unsafe, pBase, framebuffer, Yi, Yf);
					synchronized (wait_done) {
						dofinish = false;
						wait_done.notify();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
