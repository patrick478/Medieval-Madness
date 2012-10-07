package game.GameStates;

import initial3d.engine.MovableReferenceFrame;
import initial3d.engine.Quat;
import initial3d.engine.Vec3;
import game.Entity;
import game.Game;
import game.GameState;
import game.WallEntity;

/***
 * The game!
 * @author Ben
 *
 */
public class PlayState extends GameState {	
	public PlayState(Game parent) {
		super(parent);
	}
	

	@Override
	public void initalise() {
		Entity e = new WallEntity(Vec3.zero, Vec3.one, 1, 1);
		
		e.addToScene(scene);
		
		MovableReferenceFrame cameraRf = new MovableReferenceFrame(e);
		scene.getCamera().trackReferenceFrame(cameraRf);
		cameraRf.setPosition(Vec3.create(-300, -250, -400));
		cameraRf.setOrientation(Quat.create(Math.PI / 8, Vec3.i));
		cameraRf.setOrientation(cameraRf.getOrientation().mul(Quat.create(Math.PI / 4, Vec3.j)));
	}

	@Override
	public void update(double delta) {
	}

	@Override
	public void destroy() {
	}	
}
