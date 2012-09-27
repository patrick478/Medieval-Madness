package server.commands;

import server.Command;
import server.Server;

public class GameEngineCommands extends Command {

	public GameEngineCommands(Server server) {
		super(server);
	}

	@Override
	public void execute(String[] args) {
		if(args[1].equals("sfq"))
		{
			if(args[2].equals("threadcount"))
			{
				this.parentServer.face.getOut().printf("There are currently %d threads in the pool", this.parentServer.game.segQueue.getCurrentThreadCount());
			}
		}
	}
}
