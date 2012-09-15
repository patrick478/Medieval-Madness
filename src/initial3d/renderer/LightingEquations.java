package initial3d.renderer;

import static initial3d.renderer.Util.*;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
final class LightingEquations {

	private LightingEquations() {
		throw new AssertionError("You're doing it wrong.");
	}

	/**
	 * Run the phong equation. pOutput is a pointer to RGB float3 for output purposes. N and V must be normalised
	 * already; however the raw version of V must be supplied as well.
	 */
	static final void runPhong(Unsafe unsafe, long pBase, long pOutput, float ka_r, float ka_g, float ka_b, float kd_r,
			float kd_g, float kd_b, float ks_r, float ks_g, float ks_b, float shininess, float Nx, float Ny, float Nz,
			float Vx, float Vy, float Vz, float Vx_raw, float Vy_raw, float Vz_raw) {

		// TODO lights 'at infinity', viewer 'at infinity'

		float out_r = unsafe.getFloat(pBase + 0x0000006C + 4) * ka_r;
		float out_g = unsafe.getFloat(pBase + 0x0000006C + 8) * ka_g;
		float out_b = unsafe.getFloat(pBase + 0x0000006C + 12) * ka_b;

		// all lights, accounting for light enabling
		long pLight = pBase + 0x00000100;
		long pLightEnd = pBase + 0x00000900;
		long lightflag = 0x40L;
		long flags = unsafe.getLong(pBase + 0x00000008);

		while (pLight < pLightEnd) {
			// light not enabled => skip
			if ((lightflag & flags) == 0) {
				pLight += 0x100;
				continue;
			}

			boolean directional = Math.abs(unsafe.getFloat(pLight + 60)) < 0.001f;

			// vector to light
			float Lx = unsafe.getFloat(pLight + 48);
			float Ly = unsafe.getFloat(pLight + 52);
			float Lz = unsafe.getFloat(pLight + 56);
			float imL = 1f;
			if (!directional) {
				Lx += Vx_raw;
				Ly += Vy_raw;
				Lz += Vz_raw;
				// normalise L
				imL = fastInverseSqrt(Lx * Lx + Ly * Ly + Lz * Lz);
				Lx *= imL;
				Ly *= imL;
				Lz *= imL;
			}

			// add ambient
			out_r += ka_r * unsafe.getFloat(pLight + 4);
			out_g += ka_g * unsafe.getFloat(pLight + 8);
			out_b += ka_b * unsafe.getFloat(pLight + 12);

			// do complicated part of equation

			// light falloff with distance and initial intensity
			float falloff = imL * imL * unsafe.getFloat(pLight + 84);

			// lambert term
			float LdotN = Lx * Nx + Ly * Ny + Lz * Nz;
			
			if (LdotN > 0) {

				float LdotN_falloff = LdotN * falloff;
				out_r += unsafe.getFloat(pLight + 20) * kd_r * LdotN_falloff;
				out_g += unsafe.getFloat(pLight + 24) * kd_g * LdotN_falloff;
				out_b += unsafe.getFloat(pLight + 28) * kd_b * LdotN_falloff;

				// calculate and normalise R (reflection vector)
				float LdotNx2 = LdotN * 2;
				float Rx = LdotNx2 * Nx - Lx;
				float Ry = LdotNx2 * Ny - Ly;
				float Rz = LdotNx2 * Nz - Lz;
				float imR = fastInverseSqrt(Rx * Rx + Ry * Ry + Rz * Rz);
				Rx *= imR;
				Ry *= imR;
				Rz *= imR;

				// this uses an approximation for the spec pow term (from wikipedia)

				// use gamma = 4
				float beta = shininess * 0.25f;

				// compute lambda
				float Sx = Rx - Vx;
				float Sy = Ry - Vy;
				float Sz = Rz - Vz;
				float lambda = 0.5f * (Sx * Sx + Sy * Sy + Sz * Sz);

				// approximate spec term
				float spec = Math.max(0f, 1f - beta * lambda);

				// pow(spec, gamma)
				spec *= spec;
				spec *= spec;

				float spec_falloff = spec * falloff;
				out_r += unsafe.getFloat(pLight + 36) * ks_r * spec_falloff;
				out_g += unsafe.getFloat(pLight + 40) * ks_g * spec_falloff;
				out_b += unsafe.getFloat(pLight + 44) * ks_b * spec_falloff;

			}

			pLight += 0x100;
			lightflag <<= 1;
		}

		unsafe.putFloat(pOutput, out_r);
		unsafe.putFloat(pOutput + 4, out_g);
		unsafe.putFloat(pOutput + 8, out_b);

	}

}
