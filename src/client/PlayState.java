package client;

import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import common.Command;
import common.entity.MovableEntity;
import common.entity.Player;
import comp261.modelview.MeshLoader;

import initial3d.engine.Drawable;
import initial3d.engine.MovableReferenceFrame;
import initial3d.engine.Quat;
import initial3d.engine.RenderWindow;
import initial3d.engine.Scene;
import initial3d.engine.Vec3;

public class PlayState extends AbstractState {
	private Vec3 velocity = Vec3.zero;
	private Vec3 location = Vec3.zero;
	private float speed = 0.006f;
	
	private Scene scene = new Scene();
	
	private Map<Command, Boolean> commandsActive = new HashMap<Command, Boolean>(); 
	
	public PlayState() {
		this.gameWorld.getInstance().loadScene(scene);
		
		commandsActive.put(Command.Forward,  false);
		commandsActive.put(Command.Backward,  false);
		commandsActive.put(Command.Left,  false);
		commandsActive.put(Command.Right,  false);
	}

	@Override
	public Scene getScene(){
		return scene;
	}
	
	@Override
	public void update(long updateTime, RenderWindow window) {
		doKeyCommand(window, KeyEvent.VK_UP, Command.Forward);
//		doKeyCommand(window, KeyEvent.VK_W, Command.Forward);
		
		doKeyCommand(window, KeyEvent.VK_DOWN, Command.Backward);
//		doKeyCommand(window, KeyEvent.VK_S, Command.Backward);
		
		doKeyCommand(window, KeyEvent.VK_LEFT, Command.Left);
//		doKeyCommand(window, KeyEvent.VK_A, Command.Left);
		
		doKeyCommand(window, KeyEvent.VK_RIGHT, Command.Right);
//		doKeyCommand(window, KeyEvent.VK_D, Command.Right);
	}
	
	private void doKeyCommand(RenderWindow window, int key, Command command)
	{
//		if(!commandsActive.containsKey(command)) commandsActive.put(command,  false);
		
		if(window.getKey(key) && !commandsActive.get(command))			
		{
			this.client.net.sendCommandStart(command);
			commandsActive.put(command, true);
		}
		
		if(!window.getKey(key) && commandsActive.get(command))
		{
			this.client.net.sendCommandEnd(command);
			commandsActive.put(command, false);
		}
	}
}
