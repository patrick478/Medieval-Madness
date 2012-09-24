package server.commands;

import server.Command;
import server.Server;

public class PrintCommand extends Command {
	public PrintCommand(Server server) {
		super(server);
	}

	@Override
	public void execute(String[] args) {
		if(args.length == 2)
			this.PrintVariable(args[1]);
		else
		{
			String options = "";
			for(String str : this.parentServer.serverData.keySet())
			{
				if(options.length() > 0)
					options += " | ";
				options += str;
			}
			this.parentServer.face.getOut().printf("You must specify one variable to print\nAvaible variables:\n\t%s\n", options);
		}
	}
	
	public void PrintVariable(String variable)
	{
		if(this.parentServer.serverData.containsKey(variable))
			this.parentServer.face.getOut().printf("%s=%s\n", variable, this.parentServer.serverData.get(variable));
	}
}
