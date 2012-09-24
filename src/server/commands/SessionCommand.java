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
		if(args.length < 2) {
			this.parentServer.face.getOut().printf("No session command inputted\n");
			return;
		}
		if(args[1].equals("count"))
		{
			this.parentServer.face.getOut().printf("There are currently %d active sessions\n", SessionMngr.getInstance().numSessions());
		}
		else if(args[1].equals("total"))
		{
			this.parentServer.face.getOut().printf("There have been a total of %d sessions\n",  SessionMngr.getInstance().totalSessions());
		}
		else
			this.parentServer.face.getOut().printf("Unrecognised session command\n");
	}

}
