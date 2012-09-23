package comp261.modelview;

import static initial3d.Initial3D.*;
import initial3d.renderer.Util;
import initial3d.Initial3D;
import initial3d.PolygonBuffer;
import initial3d.VectorBuffer;
import initial3d.engine.RenderWindow;
import initial3d.engine.Vec3;
import initial3d.linearmath.Matrix;
import initial3d.linearmath.TransformationMatrix4D;
import initial3d.linearmath.Vector4D;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws Exception {

		FileInputStream ifs = new FileInputStream(args[0]);
		Scanner scan = new Scanner(ifs);

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

		System.out.printf("%.4f, %.4f, %.4f, %.4f\n", xavg, yavg, zavg, scale);

		Initial3D i3d = Initial3D.createInstance();

		// assemble polygon and vector buffers
		PolygonBuffer pbuf = i3d.createPolygonBuffer(10000, 8);
		VectorBuffer vbuf = i3d.createVectorBuffer(10000);
		VectorBuffer nbuf = i3d.createVectorBuffer(10000);

		for (Triangle t : trilist) {

			double[][] v1 = Vector4D.create(t.vertices[0].x, t.vertices[0].y, t.vertices[0].z, 1d);
			double[][] v2 = Vector4D.create(t.vertices[1].x, t.vertices[1].y, t.vertices[1].z, 1d);
			double[][] v3 = Vector4D.create(t.vertices[2].x, t.vertices[2].y, t.vertices[2].z, 1d);

			double[][] n1 = Vector4D.create(t.normals[0].x, t.normals[0].y, t.normals[0].z, 0d);
			double[][] n2 = Vector4D.create(t.normals[1].x, t.normals[1].y, t.normals[1].z, 0d);
			double[][] n3 = Vector4D.create(t.normals[2].x, t.normals[2].y, t.normals[2].z, 0d);

			int[] vlist = new int[] { vbuf.put(v3), vbuf.put(v2), vbuf.put(v1) };
			int[] nlist = new int[] { nbuf.put(n3), nbuf.put(n2), nbuf.put(n1) };

			pbuf.addPolygon(vlist, null, nlist, null);

		}

		/*
		 * 
		 * Run 3D cool stuff
		 */

		final int WIDTH = 848;
		final int HEIGHT = 480;

		RenderWindow rwin = RenderWindow.create(WIDTH, HEIGHT);
		rwin.setLocationRelativeTo(null);
		rwin.setVisible(true);
		BufferedImage bi = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

		// RenderFrame rwin2 = RenderFrame.create(WIDTH, HEIGHT);
		// rwin2.setLocationRelativeTo(null);
		// rwin2.setVisible(true);
		// BufferedImage bi2 = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

		i3d.viewportSize(WIDTH, HEIGHT);

		i3d.matrixMode(MODEL);
		i3d.translateX(-xavg);
		i3d.translateY(-yavg);
		i3d.translateZ(-zavg);

		// custom scaling matrix
		double[][] mscale = Matrix.create(4, 4);
		TransformationMatrix4D.scale(mscale, scale, -scale, scale, 1);
		i3d.multMatrix(mscale);

		i3d.pushMatrix();

		i3d.matrixMode(PROJ);
		i3d.loadPerspectiveFOV(0.5, 10, Math.PI / 6, WIDTH / (double) HEIGHT);
		i3d.projectionMode(PERSPECTIVE);

		// i3d.matrixMode(VIEW);
		// i3d.rotateX(-Math.PI / 6);
		// i3d.translateZ(6);

		i3d.lightfv(LIGHT0, DIFFUSE, new float[] { 1f, 1f, 1f });
		i3d.lightfv(LIGHT0, SPECULAR, new float[] { 1f, 1f, 1f });
		i3d.lightfv(LIGHT0, AMBIENT, new float[] { 0.1f, 0.1f, 0.1f });
		i3d.lightf(LIGHT0, INTENSITY, 30f);
		i3d.enable(LIGHT0);

		i3d.lightfv(LIGHT1, DIFFUSE, new float[] { 1f, 0f, 0f });
		i3d.lightfv(LIGHT1, SPECULAR, new float[] { 1f, 0f, 0f });
		i3d.lightfv(LIGHT1, AMBIENT, new float[] { 0.1f, 0.1f, 0.1f });
		i3d.lightf(LIGHT1, INTENSITY, 30f);
		i3d.enable(LIGHT1);

		i3d.lightfv(LIGHT2, DIFFUSE, new float[] { 0f, 1f, 0f });
		i3d.lightfv(LIGHT2, SPECULAR, new float[] { 0f, 1f, 0f });
		i3d.lightfv(LIGHT2, AMBIENT, new float[] { 0.1f, 0.1f, 0.1f });
		i3d.lightf(LIGHT2, INTENSITY, 500f);
		i3d.enable(LIGHT2);

		i3d.lightfv(LIGHT3, DIFFUSE, new float[] { 0f, 0f, 1f });
		i3d.lightfv(LIGHT3, SPECULAR, new float[] { 0f, 0f, 1f });
		i3d.lightfv(LIGHT3, AMBIENT, new float[] { 0.1f, 0.1f, 0.1f });
		i3d.lightf(LIGHT3, INTENSITY, 30f);
		i3d.enable(LIGHT3);

		i3d.cullFace(BACK);
		i3d.polygonMode(FRONT_AND_BACK, POLY_FILL);

		i3d.shadeModel(SHADEMODEL_FLAT);

		double[] light0p = new double[] { light.x, light.y, light.z };
		double[] light1p = new double[] { -10, 0, 0 };
		double[] light2p = new double[] { 0, 50, 0 };
		double[] light3p = new double[] { 10, 0, 0 };

		float[] mtl_floor_d = new float[] { 0.6f, 0.6f, 0.6f };
		float[] mtl_floor_s = new float[] { 0.2f, 0.2f, 0.2f };

		System.out.println(light);

		i3d.materialfv(FRONT, AMBIENT, new float[] { 0.1f, 0.1f, 0.1f });

		double camera_x = 0;
		double camera_y = 0;
		double camera_z = -5;
		double[][] kvector = new double[][] { { 0d }, { 0d }, { 1d }, { 0d } };
		double[][] movevector = new double[4][1];
		double camera_yaw = 0;
		double camera_pitch = 0;
		double camera_roll = 0;

		while (true) {

			// i3d.clear(STENCIL_BUFFER_BIT);

			i3d.matrixMode(PROJ);
			i3d.loadPerspectiveFOV(1, 200, Math.PI / 6, rwin.getRenderWidth() / (double) rwin.getRenderHeight());

			i3d.matrixMode(VIEW);
			i3d.loadIdentity();
			i3d.translateX(-camera_x);
			i3d.translateY(-camera_y);
			i3d.translateZ(-camera_z);
			i3d.rotateY(camera_yaw);
			i3d.rotateX(camera_pitch);
			i3d.rotateZ(camera_roll);
			i3d.matrixMode(VIEW_INV);
			i3d.transformOne(movevector, kvector);

			i3d.lightdv(LIGHT0, POSITION, light0p);
			i3d.lightdv(LIGHT1, POSITION, light1p);
			i3d.lightdv(LIGHT2, POSITION, light2p);
			i3d.lightdv(LIGHT3, POSITION, light3p);

			// draw ground plane
			i3d.disable(LIGHTING);
			// i3d.materialfv(FRONT, DIFFUSE, mtl_floor_d);
			// i3d.materialfv(FRONT, SPECULAR, mtl_floor_s);
			// i3d.materialf(FRONT, SHININESS, 1f);
			i3d.matrixMode(MODEL);
			i3d.pushMatrix();
			i3d.loadIdentity();
			i3d.begin(POLYGON);
			i3d.normal3d(0, 1, 0);
			i3d.color3d(0.1, 0.5, 0.1);
			i3d.vertex3d(-5, -1, -5);
			i3d.vertex3d(-5, -1, 5);
			i3d.vertex3d(5, -1, 5);
			i3d.vertex3d(5, -1, -5);
			i3d.end();
			i3d.popMatrix();

			// draw skybox
			i3d.disable(LIGHTING);
			i3d.matrixMode(MODEL);
			i3d.pushMatrix();
			i3d.loadIdentity();
			i3d.begin(POLYGON);
			i3d.normal3d(0, 0, -1);
			i3d.color3d(0.4, 0.4, 1);

			i3d.vertex3d(100, -100, 100);
			i3d.vertex3d(-100, -100, 100);
			i3d.vertex3d(-100, 100, 100);
			i3d.vertex3d(100, 100, 100);

			i3d.end();

			i3d.popMatrix();
			// end skybox

			i3d.enable(LIGHTING);
			// i3d.disable(LIGHTING);
			i3d.materialfv(FRONT_AND_BACK, DIFFUSE, trilist.get(0).diffuse);
			i3d.materialfv(FRONT_AND_BACK, SPECULAR, trilist.get(0).specular);
			i3d.materialf(FRONT_AND_BACK, SHININESS, 10f);

			i3d.vertexData(vbuf);
			i3d.normalData(nbuf);
			i3d.drawPolygons(pbuf, 0, pbuf.count());

			i3d.finish();

			i3d.extractBuffer(FRAME_BUFFER_BIT, bi);
			rwin.display(bi);

			// i3d.extractBuffer(STENCIL_BUFFER_BIT, bi2);
			// rwin2.display(bi2);

			if (rwin.pollKey(KeyEvent.VK_1)) {
				i3d.shadeModel(SHADEMODEL_FLAT);
			}
			if (rwin.pollKey(KeyEvent.VK_2)) {
				i3d.shadeModel(SHADEMODEL_GOURARD);
			}
			if (rwin.pollKey(KeyEvent.VK_3)) {
				i3d.shadeModel(SHADEMODEL_PHONG);
			}

			if (rwin.pollKey(KeyEvent.VK_F5)) {
				rwin.setCursorVisible(!rwin.isCursorVisible());
			}
			if (rwin.pollKey(KeyEvent.VK_F6)) {
				rwin.setMouseCapture(!rwin.isMouseCaptured());
			}
			if (rwin.pollKey(KeyEvent.VK_F11)) {
				rwin.setFullscreen(!rwin.isFullscreen());
			}

			if (rwin.getKey(KeyEvent.VK_W)) {
				camera_x += movevector[0][0] * 0.05;
				camera_z += movevector[2][0] * 0.05;
			}
			if (rwin.getKey(KeyEvent.VK_S)) {
				camera_x -= movevector[0][0] * 0.05;
				camera_z -= movevector[2][0] * 0.05;
			}
			if (rwin.getKey(KeyEvent.VK_A)) {
				camera_x += movevector[2][0] * 0.05;
				camera_z -= movevector[0][0] * 0.05;
			}
			if (rwin.getKey(KeyEvent.VK_D)) {
				camera_x -= movevector[2][0] * 0.05;
				camera_z += movevector[0][0] * 0.05;
			}

			int mx = rwin.pollMouseTravelX(100);
			int my = rwin.pollMouseTravelY(100);
			if (rwin.getKey(KeyEvent.VK_SHIFT)) {
				camera_roll = Util.clamp(camera_roll + Math.PI / -2d * mx / 1000d, -Math.PI / 4, Math.PI / 4);
				camera_pitch = Util.clamp(camera_pitch + Math.PI / 2d * my / 500d, -Math.PI / 2, Math.PI / 2);
			} else {
				camera_yaw = camera_yaw + Math.PI / 2d * mx / 500d;
				camera_pitch = Util.clamp(camera_pitch + Math.PI / -2d * my / 500d, -Math.PI / 2, Math.PI / 2);
			}

			// Util.pause(100);
			// break;
		}

	}

}
