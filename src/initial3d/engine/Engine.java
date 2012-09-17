package initial3d.engine;

import static initial3d.Initial3D.*;
import static initial3d.renderer.Util.*;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import initial3d.*;
import initial3d.renderer.Util;

public class Engine extends Thread {

	private final BlockingQueue<MeshContext> toadd = new LinkedBlockingQueue<MeshContext>();
	private final BlockingQueue<MeshContext> toremove = new LinkedBlockingQueue<MeshContext>();
	private final Set<MeshContext> meshcontexts = new HashSet<MeshContext>();
	private final RenderFrame frame;
	private final int width, height;
	private final Camera cam = new Camera();
	private final double sky_z = 47;

	private Initial3D i3d;
	private int shademodel = 1;

	private volatile boolean handlecamera;

	public Engine(int width_, int height_, boolean handlecamera_) {
		width = width_;
		height = height_;
		frame = RenderFrame.create(width_, height_);
		handlecamera = handlecamera_;
	}

	public Camera getCamera() {
		return cam;
	}

	public void addMeshContext(MeshContext mc) {
		toadd.add(mc);
	}

	public void removeMeshContext(MeshContext mc) {
		toremove.add(mc);
	}

	public void setHandleCamera(boolean b) {
		handlecamera = b;
	}

	private void processMeshContextChanges() {
		meshcontexts.remove(toremove.poll());
		MeshContext mc = toadd.poll();
		if (mc != null) meshcontexts.add(mc);
	}

	public void run() {
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setCrosshairVisible(true);

		i3d = Initial3D.createInstance();

		i3d.projectionMode(PERSPECTIVE);

		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		int[] bidata = ((DataBufferInt) (bi.getRaster().getDataBuffer())).getData();
		
		i3d.useFrameBuffer(bidata, width);

		i3d.viewportSize(width, height);

		i3d.lightfv(LIGHT0, DIFFUSE, new float[] { 1f, 1f, 1f });
		i3d.lightfv(LIGHT0, SPECULAR, new float[] { 1f, 1f, 1f });
		i3d.lightfv(LIGHT0, AMBIENT, new float[] { 0.01f, 0.01f, 0.01f });
		i3d.lightf(LIGHT0, INTENSITY, 0.6f);
		i3d.enable(LIGHT0);

		i3d.lightfv(LIGHT1, DIFFUSE, new float[] { 1f, 1f, 0.7f });
		i3d.lightfv(LIGHT1, SPECULAR, new float[] { 1f, 1f, 0.7f });
		i3d.lightfv(LIGHT1, AMBIENT, new float[] { 0f, 0f, 0f });
		i3d.lightf(LIGHT1, INTENSITY, 20f);
		i3d.enable(LIGHT1);

		i3d.cullFace(BACK);
		i3d.polygonMode(FRONT_AND_BACK, POLY_FILL);
		i3d.shadeModel(SHADEMODEL_FLAT);

		double[] light0p = new double[] { 0, 1, 0.75, 0 };
		double[] light1p = new double[] { 0, 0, 0, 1 };

		i3d.matrixMode(MODEL);
		i3d.loadIdentity();
		i3d.matrixMode(VIEW);
		i3d.loadIdentity();
		i3d.lightdv(LIGHT1, POSITION, light1p);

		double dt = 0;
		long t = time();

		Profiler profiler = i3d.getProfiler();
		profiler.setAutoResetEnabled(true);
		profiler.setResetOutput(System.out);

		i3d.matrixMode(PROJ);
		i3d.loadPerspectiveFOV(0.1, sky_z + 1, cam.getFOV(), frame.getRenderWidth() / (double) frame.getRenderHeight());
		i3d.initFog();
		
		float[] coltemp = new float[3];

		while (true) {

			processMeshContextChanges();

			dt = (time() - t) * 0.001;
			t = time();

			if (handlecamera) processCameraControl(dt);

			cam.loadTransformTo(i3d);

			// draw meshes, with lighting
			i3d.enable(LIGHTING);

			i3d.matrixMode(MODEL);
			i3d.pushMatrix();
			i3d.loadIdentity();
			i3d.lightdv(LIGHT0, POSITION, light0p);
			i3d.popMatrix();

			profiler.startSection(shademodel == 2 ? "I3D-engine_draw-gourard" : "I3D-engine_draw-flat");

			for (MeshContext mc : meshcontexts) {
				if (mc == null) continue;

				mc.loadTransformTo(i3d);

				MeshLOD mlod = mc.getMesh().get(0);
				Material mtl = mc.getMaterial();

				i3d.materialfv(FRONT, AMBIENT, mtl.ka.toArray(coltemp));
				i3d.materialfv(FRONT, DIFFUSE, mtl.kd.toArray(coltemp));
				i3d.materialfv(FRONT, SPECULAR, mtl.ks.toArray(coltemp));
				i3d.materialfv(FRONT, EMISSION, mtl.ke.toArray(coltemp));
				i3d.materialf(FRONT, OPACITY, mtl.opacity);
				i3d.materialf(FRONT, SHININESS, mtl.shininess);

				i3d.vertexData(mlod.vertices);
				i3d.texCoordData(mlod.texcoords);
				i3d.normalData(mlod.normals);

				i3d.drawPolygons(mlod.polys, 0, mlod.polys.count());

			}

			// draw sky
//			i3d.disable(LIGHTING);
//			i3d.matrixMode(MODEL);
//			i3d.pushMatrix();
//			i3d.loadIdentity();
//			i3d.matrixMode(VIEW);
//			i3d.pushMatrix();
//			i3d.loadIdentity();
//			i3d.begin(POLYGON);
//			i3d.normal3d(0, 0, -1);
//			i3d.color3d(0.4, 0.4, 0.1);
//			i3d.vertex3d(9000, -100, sky_z);
//			i3d.vertex3d(-9000, -100, sky_z);
//			i3d.vertex3d(-9000, 100, sky_z);
//			i3d.vertex3d(9000, 100, sky_z);
//			i3d.end();
//			i3d.begin(POLYGON);
//			i3d.normal3d(0, -1, 0);
//			i3d.color3d(0.4, 0.4, 0.1);
//			i3d.vertex3d(9000, 100, -100);
//			i3d.vertex3d(9000, 100, sky_z);
//			i3d.vertex3d(-9000, 100, sky_z);
//			i3d.vertex3d(-9000, 100, -100);
//			i3d.end();
//			i3d.popMatrix();
//			i3d.matrixMode(MODEL);
//			i3d.popMatrix();
			// end sky

			i3d.finish();

			profiler.endSection(shademodel == 2 ? "I3D-engine_draw-gourard" : "I3D-engine_draw-flat");

			profiler.startSection("I3D-engine_display");

			// i3d.extractBuffer(FRAME_BUFFER_BIT, bi);
			frame.display(bi);

			// System.out.printf("%.4f | %.4f\n", (t1 - t0) / (double)(t2 - t0), (t2 - t1) / (double)(t2 - t0));

			profiler.endSection("I3D-engine_display");

		}

	}

	private void processCameraControl(double dt) {
		if (frame.pollKey(KeyEvent.VK_1)) {
			i3d.shadeModel(SHADEMODEL_FLAT);
			shademodel = 1;
		}
		if (frame.pollKey(KeyEvent.VK_2)) {
			i3d.shadeModel(SHADEMODEL_GOURARD);
			shademodel = 2;
		}
		if (frame.pollKey(KeyEvent.VK_3)) {
			if (i3d.isEnabled(CULL_FACE)) {
				i3d.disable(CULL_FACE);
			} else {
				i3d.enable(CULL_FACE);
			}
		}
		if (frame.pollKey(KeyEvent.VK_4)) {
			if (i3d.isEnabled(FOG)) {
				i3d.disable(FOG);
			} else {
				i3d.enable(FOG);
			}
		}

		if (frame.pollKey(KeyEvent.VK_F5)) {
			frame.setCursorVisible(!frame.isCursorVisible());
		}
		if (frame.pollKey(KeyEvent.VK_F6)) {
			frame.setMouseCapture(!frame.isMouseCaptured());
		}
		if (frame.pollKey(KeyEvent.VK_F11)) {
			frame.setFullscreen(!frame.isFullscreen());
		}

		if (frame.pollKey(KeyEvent.VK_F7)) {
			i3d.matrixMode(PROJ);
			i3d.loadPerspectiveFOV(0.1, sky_z + 1, cam.getFOV(),
					frame.getRenderWidth() / (double) frame.getRenderHeight());
			i3d.initFog();
		}

		Vec3 cnorm = cam.getNormal().flattenY().unit();
		Vec3 cup = Vec3.j;
		Vec3 cside = Vec3.j.cross(cnorm);

		if (frame.getKey(KeyEvent.VK_W)) {
			cam.move(cnorm.scale(dt * 3));
		}
		if (frame.getKey(KeyEvent.VK_S)) {
			cam.move(cnorm.neg().scale(dt * 3));
		}
		if (frame.getKey(KeyEvent.VK_A)) {
			cam.move(cside.scale(dt * 3));
		}
		if (frame.getKey(KeyEvent.VK_D)) {
			cam.move(cside.neg().scale(dt * 3));
		}
		if (frame.getKey(KeyEvent.VK_SHIFT)) {
			cam.move(cup.neg().scale(dt * 3));
		}
		if (frame.getKey(KeyEvent.VK_SPACE)) {
			cam.move(cup.scale(dt * 3));
		}

		int mx = frame.pollMouseTravelX();
		int my = frame.pollMouseTravelY();

		// 200px == pi / 4 ??

		double rotx = mx / 800d * Math.PI;
		double roty = my / 800d * Math.PI;

		double rotmax = 4 * Math.PI * dt;

		// max rotation speed, and clamp actual pitch
		cam.setYaw(cam.getYaw() + Util.clamp(-rotx, -rotmax, rotmax));
		cam.setPitch(Util.clamp(cam.getPitch() + Util.clamp(roty, -rotmax, rotmax), -Math.PI * 0.499, Math.PI * 0.499));

	}
}
