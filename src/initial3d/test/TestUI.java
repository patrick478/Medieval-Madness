package initial3d.test;

import initial3d.engine.*;

public class TestUI {

	public static void main(String[] args) {
		
		RenderWindow rwin = RenderWindow.create(848, 480);
		rwin.setLocationRelativeTo(null);
		rwin.setVisible(true);
		
		SceneManager sman = new SceneManager(848, 480);
		sman.setDisplayTarget(rwin);
		rwin.addKeyListener(sman);
		
		Scene scene = new Scene();
		sman.attachToScene(scene);
		
		
		
	}
	
}
