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
		if(args.length < 2) System.out.printf("No session command inputted\n");
		if(args[1].equals("count"))
		{
			System.out.printf("There are currently %d active sessions\n", SessionMngr.getInstance().numSessions());
		}
		else if(args[1].equals("total"))
		{
			System.out.printf("There have been a total of %d sessions\n",  SessionMngr.getInstance().totalSessions());
		}
		else
			System.out.printf("Unrecognised session command\n");
	}

}
