package initial3d.test;

import initial3d.Profiler;
import initial3d.engine.RenderWindow;
import initial3d.engine.Scene;
import initial3d.engine.SceneManager;

public class Test9000 {

	public static void main(String[] args) throws Throwable {
		
		// This is to test the syncro behaviour when switching scenes

		SceneManager sman = new SceneManager(848, 480);
		sman.getProfiler().setResetOutput(null);

		RenderWindow rwin = RenderWindow.create(848, 480);
		rwin.setLocationRelativeTo(null);
		rwin.setVisible(true);
		sman.setDisplayTarget(rwin);

		Profiler p = new Profiler();
		p.setResetOutput(System.out);

		Thread.sleep(1000);

		Scene s1 = new Scene();
		Scene s2 = new Scene();

		p.startSection("attach_scene_1");

		sman.attachToScene(s1);

		p.endSection("attach_scene_1");
		
		Thread.sleep(1000);

		p.startSection("attach_scene_2");

		sman.attachToScene(s2);

		p.endSection("attach_scene_2");

		p.reset();

	}

}
