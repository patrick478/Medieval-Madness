package server.commands;

import server.Command;
import server.Server;

public class DbCommand extends Command {

	public DbCommand(Server server) {
		super(server);
	}

	@Override
	public void execute(String[] args) {
		if(args[1].equals("create"))
		{
			if(args[2].equals("account"))
			{
				
			}
		}
	}

}
