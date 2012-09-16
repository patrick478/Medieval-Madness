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

	private static Mesh convertTrilist(List<Triangle> trilist, double xavg, double yavg, double zavg, double scale) {

		// assemble mesh lod 0

		MeshLOD mlod0 = new MeshLOD(10100, 3, 30100, 30100, 30100, 1);

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

	public static List<Mesh> loadComp261(InputStream is) {

		Scanner scan = new Scanner(is);

		Vec3 light = Vec3.create(scan);
		light = light.scale(5);

		List<Mesh> meshlist = new ArrayList<Mesh>();

		List<Triangle> main_trilist = new ArrayList<Triangle>();

		// read tris in

		while (scan.hasNext()) {
			main_trilist.add(new Triangle(Vec3.create(scan), Vec3.create(scan), Vec3.create(scan), scan.nextInt(), scan
					.nextInt(), scan.nextInt()));
		}

		// attempt to calculate vertex normals...

		for (Triangle t0 : main_trilist) {
			// for each tri
			for (int i = 0; i < 3; i++) {
				// for each vertex
				Vec3 v0 = t0.vertices[i];

				for (Triangle t : main_trilist) {
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

		for (Triangle t : main_trilist) {
			for (Vec3 v : t.vertices) {
				xavg += v.x;
				yavg += v.y;
				zavg += v.z;
				largest = v.x > largest ? v.x : largest;
				largest = v.y > largest ? v.y : largest;
				largest = v.z > largest ? v.z : largest;
			}
		}

		xavg /= (main_trilist.size() * 3);
		yavg /= (main_trilist.size() * 3);
		zavg /= (main_trilist.size() * 3);
		double scale = 2 / largest;

		List<Triangle> convert_trilist = new ArrayList<Triangle>();
		for (int i = main_trilist.size(); i-- > 0;) {
			convert_trilist.add(main_trilist.remove(i));
			if (convert_trilist.size() >= 10000) {
				meshlist.add(convertTrilist(convert_trilist, xavg, yavg, zavg, scale));
				System.out.println("COMP261 MeshLoader : 10k tri mesh loaded");
				convert_trilist.clear();
			}

		}
		if (convert_trilist.size() >= 1) {
			meshlist.add(convertTrilist(convert_trilist, xavg, yavg, zavg, scale));
			System.out.println("COMP261 MeshLoader : " + convert_trilist.size() + " tri mesh loaded");
			convert_trilist.clear();
		}

		return meshlist;
	}

}
