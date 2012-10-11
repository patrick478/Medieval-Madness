package initial3d.renderer;

import initial3d.Profiler;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
class Finisher {

	private final FinisherWorker[] workers;
	private final Profiler profiler;

	public Finisher(int threads, Profiler profiler_) {
		profiler = profiler_;
		workers = new FinisherWorker[threads];
		for (int i = 0; i < threads; i++) {
			workers[i] = new FinisherWorker();
			workers[i].start();
		}
	}

	/** Entry point. */
	final void finish(Unsafe unsafe, long pBase, Object framebuffer, long qFrame) {

		profiler.startSection("I3D_finish");

		final long flags = unsafe.getLong(pBase + 0x00000008);

		// parallel finishing
		int totallines = unsafe.getInt(pBase + 0x00000004);
		// lines per thread, as a multiple of 8
		int lines = (totallines / workers.length) & ~7;
		int Yi = 0;
		for (int i = 0; i < workers.length - 1; i++, Yi += lines) {
			workers[i].beginFinishRegion(unsafe, pBase, framebuffer, qFrame, Yi, Yi + lines);
		}
		workers[workers.length - 1].beginFinishRegion(unsafe, pBase, framebuffer, qFrame, Yi, totallines);

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

	@SuppressWarnings("unused")
	private static final void finishRegion(Unsafe unsafe, long pBase, Object framebuffer, long qFrame, int Yi, int Yf) {
		final long flags = unsafe.getLong(pBase + 0x00000008);
		final int width = unsafe.getInt(pBase + 0x00000000);
		final int height = unsafe.getInt(pBase + 0x00000004);
		final int zsign = unsafe.getInt(pBase + 0x00000068);

		long pColor = pBase + 0x01DC0900 + Yi * width * 16; // float4, ARGB
		long pZ = pBase + 0x05DC0900 + Yi * width * 4; // float
		long pFC = pBase + 0x08E00900 + Yi * width * 16; // float4

		long frameoffset = qFrame + Yi * width * 4;
		long frameend = frameoffset + width * (Yf - Yi) * 4;

		float fog_r = unsafe.getFloat(pBase + 0x0000008C + 4);
		float fog_g = unsafe.getFloat(pBase + 0x0000008C + 8);
		float fog_b = unsafe.getFloat(pBase + 0x0000008C + 12);
		int fog_col = 0xFF000000 | ((int) (fog_r * 255f) << 16) | ((int) (fog_g * 255f) << 8) | (int) (fog_b * 255f);

		final boolean zwrite = ((flags & 0x100000L) != 0);
		final boolean depth_test = (flags & 0x8L) != 0;
		final boolean fill_bg = zwrite && depth_test;
		// if frame write enabled, fog enabled, and depth test enabled
		// fog can't be done if depth doesn't make sense
		if ((flags & 0x40000L) != 0 && (flags & 0x10L) != 0 && depth_test) {
			while (frameoffset < frameend) {
				float iZ = unsafe.getFloat(pZ);
				iZ *= zsign;
				if (iZ < 0 && fill_bg) {
					// nothing written this frame
					// only if zwrite enabled and depth test enabled
					unsafe.putFloat(pZ, zsign);
					unsafe.putInt(framebuffer, frameoffset, fog_col);
					pZ += 4;
					frameoffset += 4;
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
				float c_mul = 255f - fogmul;
				int ir = Math.min((int) (r * c_mul + fog_r * fogmul), 255);
				int ig = Math.min((int) (g * c_mul + fog_g * fogmul), 255);
				int ib = Math.min((int) (b * c_mul + fog_b * fogmul), 255);

				// pack and write to frame buffer
				unsafe.putInt(framebuffer, frameoffset, 0xFF000000 | (ir << 16) | (ig << 8) | ib);

				frameoffset += 4;
				pZ += 4;
			}
		} else {
			while (frameoffset < frameend) {
				float iZ = unsafe.getFloat(pZ);
				iZ *= zsign;
				if (iZ < 0 && fill_bg) {
					// nothing written this frame
					// only if zwrite enabled and depth test enabled
					unsafe.putFloat(pZ, zsign);
					unsafe.putInt(framebuffer, frameoffset, 0xFF000000);
					pZ += 4;
					frameoffset += 4;
					pColor += 16;
					pFC += 16;
					continue;
				}

				float a = unsafe.getFloat(pColor);
				float r = unsafe.getFloat(pColor += 4);
				float g = unsafe.getFloat(pColor += 4);
				float b = unsafe.getFloat(pColor += 4);
				pColor += 4;

				// convert to int and clamp down (shouldn't have to clamp up to zero...)
				float c_mul = 255f;
				int ir = Math.min((int) (r * c_mul), 255);
				int ig = Math.min((int) (g * c_mul), 255);
				int ib = Math.min((int) (b * c_mul), 255);

				// pack and write to frame buffer
				unsafe.putInt(framebuffer, frameoffset, 0xFF000000 | (ir << 16) | (ig << 8) | ib);

				frameoffset += 4;
				pZ += 4;
			}
		}
		
		// TODO proper blending ?

	}

	private static class FinisherWorker extends Thread {

		private final Object wait_begin = new Object();
		private final Object wait_done = new Object();

		// rasterisation params
		private volatile Unsafe unsafe;
		private volatile Object framebuffer;
		private volatile long qFrame;
		private volatile long pBase;
		private volatile int Yi, Yf;

		private volatile boolean dofinish = false;

		public FinisherWorker() {
			setDaemon(true);
		}

		public void beginFinishRegion(Unsafe unsafe, long pBase, Object framebuffer, long qFrame, int Yi, int Yf) {
			this.unsafe = unsafe;
			this.pBase = pBase;
			this.framebuffer = framebuffer;
			this.qFrame = qFrame;
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
					finishRegion(unsafe, pBase, framebuffer, qFrame, Yi, Yf);
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
