package comp261.modelview;

import initial3d.engine.Mesh;
import initial3d.engine.MeshLOD;
import initial3d.engine.Vec3;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MeshLoader {

	private MeshLoader() {
		throw new AssertionError();
	}

	public static Mesh loadComp261(InputStream is) {
		Scanner scan = new Scanner(is);

		Vec3 light = Vec3.create(scan);
		light = light.scale(5);

		List<Triangle> trilist = new ArrayList<Triangle>();

		// read tris in

		while (scan.hasNext()) {
			trilist.add(new Triangle(Vec3.create(scan), Vec3.create(scan), Vec3.create(scan), scan.nextInt(), scan
					.nextInt(), scan.nextInt()));
		}

		// attempt to calculate vertex normals...

		for (Triangle t0 : trilist) {
			// for each tri
			for (int i = 0; i < 3; i++) {
				// for each vertex
				Vec3 v0 = t0.vertices[i];

				for (Triangle t : trilist) {
					if (t != t0) {
						// for every other tri
						for (Vec3 v : t.vertices) {
							// for every vertex

							if (v0.add(v.neg()).mag() < 0.001) {
								// assume shared vertex
								// add face normals together
								t0.normals[i] = t0.normals[i].add(t.trinorm);

							}

						}

					}
				}
			}
		}

		// compute translation / scaling values

		double largest = 0;
		double xavg = 0, yavg = 0, zavg = 0;

		for (Triangle t : trilist) {
			for (Vec3 v : t.vertices) {
				xavg += v.x;
				yavg += v.y;
				zavg += v.z;
				largest = v.x > largest ? v.x : largest;
				largest = v.y > largest ? v.y : largest;
				largest = v.z > largest ? v.z : largest;
			}
		}

		xavg /= (trilist.size() * 3);
		yavg /= (trilist.size() * 3);
		zavg /= (trilist.size() * 3);
		double scale = 2 / largest;

		// assemble mesh lod 0

		MeshLOD mlod0 = new MeshLOD(2000, 8, 6000, 6000, 6000, 1);

		for (Triangle t : trilist) {

			int iv1 = mlod0.addVertex((t.vertices[0].x - xavg) * scale, (t.vertices[0].y - yavg) * -scale,
					(t.vertices[0].z - zavg) * scale);
			int iv2 = mlod0.addVertex((t.vertices[1].x - xavg) * scale, (t.vertices[1].y - yavg) * -scale,
					(t.vertices[1].z - zavg) * scale);
			int iv3 = mlod0.addVertex((t.vertices[2].x - xavg) * scale, (t.vertices[2].y - yavg) * -scale,
					(t.vertices[2].z - zavg) * scale);

			int ivn1 = mlod0.addNormal(t.normals[0].x * scale, t.normals[0].y * -scale, t.normals[0].z * scale);
			int ivn2 = mlod0.addNormal(t.normals[1].x * scale, t.normals[1].y * -scale, t.normals[1].z * scale);
			int ivn3 = mlod0.addNormal(t.normals[2].x * scale, t.normals[2].y * -scale, t.normals[2].z * scale);

			int[] vlist = new int[] { iv3, iv2, iv1 };
			int[] nlist = new int[] { ivn3, ivn2, ivn1 };

			mlod0.addPolygon(vlist, null, nlist, null);

		}

		Mesh mesh = new Mesh();
		mesh.add(mlod0);

		return mesh;
	}

}
