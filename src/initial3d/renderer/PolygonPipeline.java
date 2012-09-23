package initial3d.renderer;

import initial3d.Profiler;
import sun.misc.Unsafe;
import static initial3d.renderer.Util.*;

@SuppressWarnings("restriction")
final class PolygonPipeline {

	// really a part of Initial3D
	// collection of static methods for actually processing polys
	// all vectors must be transformed before calling to this class

	private final RasteriserWorker[] workers;
	private final Profiler profiler;

	PolygonPipeline(int threads, Profiler profiler_) {
		profiler = profiler_;
		workers = new RasteriserWorker[threads];
		for (int i = 0; i < threads; i++) {
			RasteriserWorker rw = new RasteriserWorker();
			rw.start();
			workers[i] = rw;
		}
	}

	/** Entry point. Stride is in ints. */
	final void processPolygons(Unsafe unsafe, long pBase, int[] pdata, int poffset, int pstride, int pcount) {

		profiler.startSection("I3D_polypipe");

		final long flags = unsafe.getLong(pBase + 0x00000008);
		final int shademodel = unsafe.getInt(pBase + 0x00000034);
		final boolean lighting = ((flags & 0x20L) != 0);

		// triangulate each poly and rasterise
		// restricted to strictly convex polys (each vertex must 'point' out)
		// ccw winding visible -> front face

		// [ (int) tri_norm_zsign, (int) flags, {56} | (long) pv0, (long) pvt0,
		// (long) pvn0, (long) pvv0, {16},
		// (float4) vlight0 | <v*1> | <v*2> ]

		final long pClipLeft = 0x000B0500 + pBase;
		final long pClipRight = 0x000B0540 + pBase;
		final long pClipTop = 0x000B0580 + pBase;
		final long pClipBottom = 0x000B05C0 + pBase;

		final long pTri0 = 0x009C0900 + pBase;
		final long pPoly0 = 0x000B0910 + pBase;
		long pTri = pTri0;

		// init pEx
		unsafe.putLong(pBase + 0x00000084, pBase + 0x00840900);

		profiler.startSection("I3D_polypipe_cull-clip-light-triangulate");

		for (int i = poffset; i < (pcount * pstride); i += pstride) {
			// fewer than 3 vertices => not really a polygon (this shouldn't happen)
			if (pdata[i] < 3) {
				continue;
			}
			// extract the poly data
			// TODO use the polyvert struct in PolygonBuffer so this is not needed
			long pPoly = pPoly0;
			unpackPolygon(unsafe, pBase, pPoly, pdata, i);
			final int vcount = unsafe.getInt(pPoly + 32);

			// far cull (experimental)
			long pEnd = pPoly + vcount * 64;
			int far_cull_count = 0;
			for (long pPolyVert = pPoly; pPolyVert < pEnd; pPolyVert += 64) {
				if (unsafe.getDouble(unsafe.getLong(pPolyVert) + 16) > 1d) far_cull_count++;
			}
			if (far_cull_count >= vcount) {
				// all vertices over projection far plane
				continue;
			}

			// plane culling (clip optimisation)
			int vclip_near = testClipPolygon(unsafe, pBase, pPoly, 0, 0, 1, 0.2);
			if (vclip_near == 0) continue;
			int vclip_left = testClipPolygon(unsafe, pBase, pPoly, pClipLeft);
			if (vclip_left == 0) continue;
			int vclip_right = testClipPolygon(unsafe, pBase, pPoly, pClipRight);
			if (vclip_right == 0) continue;
			int vclip_top = testClipPolygon(unsafe, pBase, pPoly, pClipTop);
			if (vclip_top == 0) continue;
			int vclip_bottom = testClipPolygon(unsafe, pBase, pPoly, pClipBottom);
			if (vclip_bottom == 0) continue;

			int vcount_clipped = vcount;

			// face culling in view space - take dot prod of normal and position vector
			// if dot prod < 0 front face
			// this is experimental as a means of doing lighting before any clipping
			// TODO eval normal at multiple vertices so colinear edges work
			double vv1x = unsafe.getDouble(unsafe.getLong(pPoly + 64 + 24))
					- unsafe.getDouble(unsafe.getLong(pPoly + 24));
			double vv1y = unsafe.getDouble(unsafe.getLong(pPoly + 64 + 24) + 8)
					- unsafe.getDouble(unsafe.getLong(pPoly + 24) + 8);
			double vv1z = unsafe.getDouble(unsafe.getLong(pPoly + 64 + 24) + 16)
					- unsafe.getDouble(unsafe.getLong(pPoly + 24) + 16);
			double vv2x = unsafe.getDouble(unsafe.getLong(pPoly + 128 + 24))
					- unsafe.getDouble(unsafe.getLong(pPoly + 64 + 24));
			double vv2y = unsafe.getDouble(unsafe.getLong(pPoly + 128 + 24) + 8)
					- unsafe.getDouble(unsafe.getLong(pPoly + 64 + 24) + 8);
			double vv2z = unsafe.getDouble(unsafe.getLong(pPoly + 128 + 24) + 16)
					- unsafe.getDouble(unsafe.getLong(pPoly + 64 + 24) + 16);
			double Nx = vv1y * vv2z - vv1z * vv2y;
			double Ny = vv1z * vv2x - vv1x * vv2z;
			double Nz = vv1x * vv2y - vv1y * vv2x;

			double normdot = Nx * unsafe.getDouble(unsafe.getLong(pPoly + 24)) + Ny
					* unsafe.getDouble(unsafe.getLong(pPoly + 24) + 8) + Nz
					* unsafe.getDouble(unsafe.getLong(pPoly + 24) + 16);

			if (normdot >= 0 && (unsafe.getInt(pBase + 0x0000002C) & 2) != 0 && (flags & 0x4L) != 0) {
				// cull backface
				continue;
			}
			if (normdot <= 0 && (unsafe.getInt(pBase + 0x0000002C) & 1) != 0 && (flags & 0x4L) != 0) {
				// cull frontface
				continue;
			}

			// lighting
			// profiler.startSection("I3D_polypipe-light");
			final long pMtl = pBase + ((normdot < 0) ? 0x00000900 : 0x00040900);

			if (lighting) {
				float opacity = unsafe.getFloat(pMtl + 16);
				if (shademodel == 1) {
					unsafe.putFloat(pPoly + 48, opacity);
					calculatePolygonLight(unsafe, pBase, pMtl, pPoly, pPoly + 48 + 4);
					// copy poly lighting for each vertex
					pEnd = pPoly + vcount_clipped * 64;
					for (long pPolyVert = pPoly + 64; pPolyVert < pEnd; pPolyVert += 64) {
						unsafe.copyMemory(pPoly + 48, pPolyVert + 48, 16);
					}
				} else if (shademodel == 2) {
					// do lighting for each vertex
					pEnd = pPoly + vcount_clipped * 64;
					for (long pPolyVert = pPoly; pPolyVert < pEnd; pPolyVert += 64) {
						unsafe.putFloat(pPolyVert + 48, opacity);
						calculateVertexLight(unsafe, pBase, pMtl, pPolyVert, pPolyVert + 52);
					}
				}
			}
			// profiler.endSection("I3D_polypipe-light");

			// near clip, but only if necessary
			if (vclip_near < vcount) {
				pPoly = clipPolygon(unsafe, pBase, pPoly, 0, 0, 1, 0.2);
				// note that even if no vertices are emitted from the clipper,
				// vcount can still be read and will be zero
				vcount_clipped = unsafe.getInt(pPoly + 32);
				if (vcount_clipped < 3) {
					// drop degenerate poly
					continue;
				}
			}

			// old proj space face culling
			// face culling, eval z component of proj space normal
			// double v1x = unsafe.getDouble(unsafe.getLong(pPoly + 64)) - unsafe.getDouble(unsafe.getLong(pPoly));
			// double v1y = unsafe.getDouble(unsafe.getLong(pPoly + 64) + 8) - unsafe.getDouble(unsafe.getLong(pPoly) +
			// 8);
			// double v2x = unsafe.getDouble(unsafe.getLong(pPoly + 128)) - unsafe.getDouble(unsafe.getLong(pPoly +
			// 64));
			// double v2y = unsafe.getDouble(unsafe.getLong(pPoly + 128) + 8)
			// - unsafe.getDouble(unsafe.getLong(pPoly + 64) + 8);
			// double cross_z = v1x * v2y - v1y * v2x;
			// if (cross_z >= 0 && (unsafe.getInt(pBase + 0x0000002C) & 2) != 0 && (flags & 0x4L) != 0) {
			// // cull backface
			// continue;
			// }
			// if (cross_z <= 0 && (unsafe.getInt(pBase + 0x0000002C) & 1) != 0 && (flags & 0x4L) != 0) {
			// // cull frontface
			// continue;
			// }

			// clip other planes
			if (vclip_left < vcount) pPoly = clipPolygon(unsafe, pBase, pPoly, pClipLeft);
			if (vclip_right < vcount) pPoly = clipPolygon(unsafe, pBase, pPoly, pClipRight);
			if (vclip_top < vcount) pPoly = clipPolygon(unsafe, pBase, pPoly, pClipTop);
			if (vclip_bottom < vcount) pPoly = clipPolygon(unsafe, pBase, pPoly, pClipBottom);

			vcount_clipped = unsafe.getInt(pPoly + 32);

			if (vcount_clipped < 3) {
				// drop degenerate poly
				continue;
			}

			// triangulation
			// profiler.startSection("I3D_polypipe-triangulate");
			long pPolyVert1 = pPoly + 64;
			long pPolyVert2 = pPoly + 128;
			pEnd = pPoly + vcount_clipped * 64;

			for (; pPolyVert2 < pEnd; pPolyVert1 += 64, pPolyVert2 += 64, pTri += 256) {
				unsafe.putInt(pTri, normdot < 0 ? -1 : normdot > 0 ? 1 : 0);
				unsafe.putInt(pTri + 4, 0);
				unsafe.copyMemory(pPoly, pTri + 64, 64);
				unsafe.copyMemory(pPolyVert1, pTri + 128, 64);
				unsafe.copyMemory(pPolyVert2, pTri + 192, 64);
				// TrianglePerspectiveRasteriser.verifyTriangle(unsafe, pBase, pTri);
			}
			// profiler.endSection("I3D_polypipe-triangulate");

		}

		profiler.endSection("I3D_polypipe_cull-clip-light-triangulate");

		profiler.startSection("I3D_polypipe_rasterise");

		// rasterise, this parallelises nicely
		int totallines = unsafe.getInt(pBase + 0x00000004);
		// lines per thread, as a multiple of 8
		int lines = (totallines / workers.length) & ~7;
		int Yi = 0;
		for (int i = 0; i < workers.length - 1; i++, Yi += lines) {
			workers[i].beginRasteriseTriangleBuffer(unsafe, pBase, pTri0, pTri, Yi, Yi + lines);
		}
		workers[workers.length - 1].beginRasteriseTriangleBuffer(unsafe, pBase, pTri0, pTri, Yi, totallines);

		// wait for completion
		for (RasteriserWorker rw : workers) {
			rw.waitUntilDone();
		}

		profiler.endSection("I3D_polypipe_rasterise");

		profiler.endSection("I3D_polypipe");

	}

	private static final void unpackPolygon(Unsafe unsafe, long pBase, long pPolyOut, int[] pdata, int i) {
		long pV0 = 0x000C0900 + pBase;
		long pVt0 = 0x00540900 + pBase;
		long pVn0 = 0x00240900 + pBase;
		long pVV0 = 0x003C0900 + pBase;
		long pVc0 = 0x006C0900 + pBase;
		// 64B per vertex : [ pv | pvt | pvn | pvv | ... | {12} | color ]
		// i initially points to poly, so move to first vertex
		int iend = i + pdata[i] * 4 + 4;
		// write out vertex count
		unsafe.putInt(pPolyOut + 32, pdata[i]);
		for (i += 4; i < iend; i += 4) {
			unsafe.putLong(pPolyOut, pV0 + pdata[i] * 32);
			unsafe.putLong(pPolyOut + 8, pVt0 + pdata[i + 1] * 32);
			unsafe.putLong(pPolyOut + 16, pVn0 + pdata[i + 2] * 32);
			unsafe.putLong(pPolyOut + 24, pVV0 + pdata[i] * 32);
			long pvc = pVc0 + pdata[i + 3] * 32;
			unsafe.putFloat(pPolyOut + 48, (float) unsafe.getDouble(pvc + 24));
			unsafe.putFloat(pPolyOut + 52, (float) unsafe.getDouble(pvc));
			unsafe.putFloat(pPolyOut + 56, (float) unsafe.getDouble(pvc + 8));
			unsafe.putFloat(pPolyOut + 60, (float) unsafe.getDouble(pvc + 16));
			pPolyOut += 64;
		}
	}

	private static int testClipPolygon(Unsafe unsafe, long pBase, long pPoly, long pClip) {
		// helper
		return testClipPolygon(unsafe, pBase, pPoly, unsafe.getDouble(pClip), unsafe.getDouble(pClip + 8),
				unsafe.getDouble(pClip + 16), unsafe.getDouble(pClip + 24));
	}

	private static int testClipPolygon(Unsafe unsafe, long pBase, long pPoly, double cx, double cy, double cz,
			double cutoff) {
		// read vertex count
		final long pPolyEnd = pPoly + unsafe.getInt(pPoly + 32) * 64;

		// number of vertices that pass clip func
		int vpasscount = 0;

		// loop through vertices
		for (; pPoly < pPolyEnd; pPoly += 64) {
			if (vectorDot(unsafe, unsafe.getLong(pPoly + 24), cx, cy, cz) > cutoff) {
				// vertex passes clip func
				vpasscount++;
			}
		}

		return vpasscount;
	}

	private static final long clipPolygon(Unsafe unsafe, long pBase, long pPolyIn, long pClip) {
		// helper
		return clipPolygon(unsafe, pBase, pPolyIn, unsafe.getDouble(pClip), unsafe.getDouble(pClip + 8),
				unsafe.getDouble(pClip + 16), unsafe.getDouble(pClip + 24));
	}

	private static final long clipPolygon(Unsafe unsafe, long pBase, long pPolyIn, double cx, double cy, double cz,
			double cutoff) {
		// read vertex count
		final long pPolyOut = pPolyIn + unsafe.getInt(pPolyIn + 32) * 64;

		long pNext = pPolyOut;

		// at this point:
		// pPolyIn points to the series of vertex structs to clip
		// pPolyOut points to free space to put the clipped polygon
		// pNext tracks creation of new vertex structs

		long pPolyVertA = pPolyIn;
		long pPolyVertB = pPolyIn + 64;

		// skip altogether if input isn't at least a triangle
		if (pPolyOut - pPolyIn >= 192) {
			// if first vertex inside, emit it
			if (vectorDot(unsafe, unsafe.getLong(pPolyVertA + 24), cx, cy, cz) > cutoff) {
				unsafe.copyMemory(pPolyVertA, pNext, 64);
				pNext += 64;
				// System.out.println("adding point a");
			}
			// loop through edges
			for (; pPolyVertA < pPolyOut; pPolyVertA += 64, pPolyVertB += 64) {
				// redirect B back to first vertex for last edge
				if (pPolyVertB == pPolyOut) pPolyVertB = pPolyIn;
				double za = vectorDot(unsafe, unsafe.getLong(pPolyVertA + 24), cx, cy, cz);
				double zb = vectorDot(unsafe, unsafe.getLong(pPolyVertB + 24), cx, cy, cz);
				if (za > cutoff) {
					// P_i inside clip region
					if (zb > cutoff) {
						// P_i+1 inside clip region, add P_i+1
						unsafe.copyMemory(pPolyVertB, pNext, 64);
						pNext += 64;
						// System.out.println("adding point b");
					} else {
						// intersection of P_i, P_i+1 on the near plane
						doVertexIntersection(unsafe, pBase, pNext, pPolyVertA, (cutoff - zb) / (za - zb), pPolyVertB,
								(za - cutoff) / (za - zb));
						pNext += 64;
						// System.out.println("adding new point (b out)");
					}
				} else {
					if (zb > cutoff) {
						// do intersection
						doVertexIntersection(unsafe, pBase, pNext, pPolyVertA, (zb - cutoff) / (zb - za), pPolyVertB,
								(cutoff - za) / (zb - za));
						pNext += 64;
						// copy as well
						unsafe.copyMemory(pPolyVertB, pNext, 64);
						pNext += 64;
						// System.out.println("adding new point (a out) and point b");
					} else {
						// System.out.println("skipping points a and b");
					}
				}
			}
		}

		// put resulting number of vertices in first struct
		// note this should work even if no vertices are output
		unsafe.putInt(pPolyOut + 32, ((int) (pNext - pPolyOut)) >> 6);
		return pPolyOut;
	}

	private static final void doVertexIntersection(Unsafe unsafe, long pBase, long pOut, long pA, double cA, long pB,
			double cB) {
		// needs to create 4 new vectors @32B == 128B at pEx, and a new vertex
		// struct at pOut
		long pEx = unsafe.getLong(pBase + 0x00000084);
		// update pEx
		unsafe.putLong(pBase + 0x00000084, pEx + 128);
		// pvt
		interpolateVectors(unsafe, pEx + 32, unsafe.getLong(pA + 8), cA, unsafe.getLong(pB + 8), cB);
		unsafe.putLong(pOut + 8, pEx + 32);
		// pvn
		interpolateVectors(unsafe, pEx + 64, unsafe.getLong(pA + 16), cA, unsafe.getLong(pB + 16), cB);
		normalise4VectorBlock_unsafe(unsafe, pEx + 64, pEx + 64, 1);
		unsafe.putLong(pOut + 16, pEx + 64);
		// pvv
		interpolateVectors(unsafe, pEx + 96, unsafe.getLong(pA + 24), cA, unsafe.getLong(pB + 24), cB);
		unsafe.putLong(pOut + 24, pEx + 96);
		// retransform pv instead of interpolate (use proj matrix)
		multiply4VectorBlock_pos_unsafe(unsafe, pEx, 1, pEx + 96, pBase + 0x00080A00);
		unsafe.putLong(pOut, pEx);
		// color
		interpolateVectors_float(unsafe, pOut + 48, pA + 48, (float) cA, pB + 48, (float) cB);
	}

	private static final void interpolateVectors(Unsafe unsafe, long pTarget, long pA, double cA, long pB, double cB) {
		for (int q = 0; q < 32; q += 8) {
			unsafe.putDouble(pTarget + q, unsafe.getDouble(pA + q) * cA + unsafe.getDouble(pB + q) * cB);
		}
	}

	private static final void interpolateVectors_float(Unsafe unsafe, long pTarget, long pA, float cA, long pB, float cB) {
		for (int q = 0; q < 16; q += 4) {
			unsafe.putFloat(pTarget + q, unsafe.getFloat(pA + q) * cA + unsafe.getFloat(pB + q) * cB);
		}
	}

	private static final void calculatePolygonLight(Unsafe unsafe, long pBase, long pMtl, long pPoly, long pOutput) {
		// use poly normal and 'representative vertex' to run lighting equation
		// surface normal (phong N)
		double vv1x = unsafe.getDouble(unsafe.getLong(pPoly + 64 + 24)) - unsafe.getDouble(unsafe.getLong(pPoly + 24));
		double vv1y = unsafe.getDouble(unsafe.getLong(pPoly + 64 + 24) + 8)
				- unsafe.getDouble(unsafe.getLong(pPoly + 24) + 8);
		double vv1z = unsafe.getDouble(unsafe.getLong(pPoly + 64 + 24) + 16)
				- unsafe.getDouble(unsafe.getLong(pPoly + 24) + 16);
		double vv2x = unsafe.getDouble(unsafe.getLong(pPoly + 128 + 24))
				- unsafe.getDouble(unsafe.getLong(pPoly + 64 + 24));
		double vv2y = unsafe.getDouble(unsafe.getLong(pPoly + 128 + 24) + 8)
				- unsafe.getDouble(unsafe.getLong(pPoly + 64 + 24) + 8);
		double vv2z = unsafe.getDouble(unsafe.getLong(pPoly + 128 + 24) + 16)
				- unsafe.getDouble(unsafe.getLong(pPoly + 64 + 24) + 16);
		float Nx = (float) (vv1y * vv2z - vv1z * vv2y);
		float Ny = (float) (vv1z * vv2x - vv1x * vv2z);
		float Nz = (float) (vv1x * vv2y - vv1y * vv2x);
		// normalise N
		float imN = Util.fastInverseSqrt(Nx * Nx + Ny * Ny + Nz * Nz);
		Nx *= imN;
		Ny *= imN;
		Nz *= imN;

		// view normal (phong V)
		float Vx = (float) -unsafe.getDouble(unsafe.getLong(pPoly + 24));
		float Vy = (float) -unsafe.getDouble(unsafe.getLong(pPoly + 24) + 8);
		float Vz = (float) -unsafe.getDouble(unsafe.getLong(pPoly + 24) + 16);
		// prepare to normalise V
		float imV = Util.fastInverseSqrt(Vx * Vx + Vy * Vy + Vz * Vz);

		LightingEquations.runPhong2(unsafe, pBase, pOutput, pMtl, Nx, Ny, Nz, Vx * imV, Vy * imV, Vz * imV, Vx, Vy, Vz);
	}

	private static final void calculateVertexLight(Unsafe unsafe, long pBase, long pMtl, long pPolyVert, long pOutput) {
		// vertex (surface) normal (phong N)
		float Nx = (float) unsafe.getDouble(unsafe.getLong(pPolyVert + 16));
		float Ny = (float) unsafe.getDouble(unsafe.getLong(pPolyVert + 16) + 8);
		float Nz = (float) unsafe.getDouble(unsafe.getLong(pPolyVert + 16) + 16);
		// shouldn't need to normalise N, because normal transformation and clip interpolation both do
		// float imN = Util.fastInverseSqrt(Nx * Nx + Ny * Ny + Nz * Nz);
		// Nx *= imN;
		// Ny *= imN;
		// Nz *= imN;

		// view normal (phong V)
		float Vx = (float) -unsafe.getDouble(unsafe.getLong(pPolyVert + 24));
		float Vy = (float) -unsafe.getDouble(unsafe.getLong(pPolyVert + 24) + 8);
		float Vz = (float) -unsafe.getDouble(unsafe.getLong(pPolyVert + 24) + 16);
		// prepare to normalise V
		float imV = Util.fastInverseSqrt(Vx * Vx + Vy * Vy + Vz * Vz);

		LightingEquations.runPhong2(unsafe, pBase, pOutput, pMtl, Nx, Ny, Nz, Vx * imV, Vy * imV, Vz * imV, Vx, Vy, Vz);
	}

	private static final void rasteriseTriangleBuffer(Unsafe unsafe, long pBase, long pTri, long pTriEnd, int Yi, int Yf) {
		// this method needs to be able to be invoked multiple times in parallel
		// System.out.printf("Rasterising lines [%d,%d)\n", Yi, Yf);
		final long flags = unsafe.getInt(pBase + 0x00000008);
		final boolean frontface = unsafe.getInt(pTri) < 0;
		final int fmode = unsafe.getInt(pBase + 0x0000003C);
		final int bmode = unsafe.getInt(pBase + 0x00000040);
		final int shademodel = unsafe.getInt(pBase + 0x00000034);
		final int projtype = unsafe.getInt(pBase + 0x00000030);
		final boolean tex2d = (flags & 0x10000L) != 0;
		final boolean depth_test = (flags & 0x8L) != 0;
		final boolean colorwrite = (flags & 0x80000L) != 0;

		// TODO for inside ifs
		for (; pTri < pTriEnd; pTri += 256) {
			if (projtype == 1) {
				// persepective projection

				if ((frontface ? fmode : bmode) == 0) {
					// fill polys
					if (tex2d) {
						if (shademodel == 2 && colorwrite) {
							// interpolate iZ + color + uv
							// shouldn't need to interpolate color if not writing to color buffer
							TrianglePerspectiveRasteriser.rasteriseTriangle_z_color_uv(unsafe, pBase, pTri, Yi, Yf);

						} else {
							// interpolate iZ + uv
							TrianglePerspectiveRasteriser.rasteriseTriangle_z_uv(unsafe, pBase, pTri, Yi, Yf);

						}

					} else {
						if (shademodel == 2 && colorwrite) {
							// interpolate iZ + color
							// shouldn't need to interpolate color if not writing to color buffer
							TrianglePerspectiveRasteriser.rasteriseTriangle_z_color(unsafe, pBase, pTri, Yi, Yf);

						} else if (depth_test) {
							// interpolate iZ
							TrianglePerspectiveRasteriser.rasteriseTriangle_z(unsafe, pBase, pTri, Yi, Yf);

						} else {
							// interpolate... nothing!
							// stencil-only passes should end up here
							TrianglePerspectiveRasteriser.rasteriseTriangle(unsafe, pBase, pTri, Yi, Yf);
						}

					}

				} else if ((frontface ? fmode : bmode) == 1) {
					// outline polys

				}

			} else if (projtype == 0) {
				// orthographic projection

			}

		}
	}

	private static class RasteriserWorker extends Thread {

		private final Object wait_begin = new Object();
		private final Object wait_done = new Object();

		// rasterisation params
		private volatile Unsafe unsafe;
		private volatile long pBase;
		private volatile long pTri;
		private volatile long pTriEnd;
		private volatile int Yi, Yf;

		private volatile boolean dorasterise = false;

		public RasteriserWorker() {
			setDaemon(true);
		}

		public void beginRasteriseTriangleBuffer(Unsafe unsafe, long pBase, long pTri, long pTriEnd, int Yi, int Yf) {
			this.unsafe = unsafe;
			this.pBase = pBase;
			this.pTri = pTri;
			this.pTriEnd = pTriEnd;
			this.Yi = Yi;
			this.Yf = Yf;
			synchronized (wait_begin) {
				dorasterise = true;
				wait_begin.notify();
			}
		}

		public void waitUntilDone() {
			synchronized (wait_done) {
				while (dorasterise) {
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
						while (!dorasterise) {
							wait_begin.wait();
						}
					}
					// execute parallel method
					rasteriseTriangleBuffer(unsafe, pBase, pTri, pTriEnd, Yi, Yf);
					synchronized (wait_done) {
						dorasterise = false;
						wait_done.notify();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
