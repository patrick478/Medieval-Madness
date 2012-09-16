package server.commands;

import server.Command;
import server.Server;
import server.SessionMngr;

public class SessionCommand extends Command {

	public SessionCommand(Server server) {
		super(server);
	}

	@Override
	public void execute(String[] args) {
		if(args[1].equals("count"))
		{
			System.out.printf("There are currently %d active sessions\n", SessionMngr.getInstance().numSessions());
		}
		else
			System.out.printf("Unrecognised session command\n");
	}

}
