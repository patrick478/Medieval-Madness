package server;

public abstract class Command {
	protected Server parentServer;
	public Command(Server server)
	{
		this.parentServer = server;
	}
	
	public abstract void execute(String[] args);
}
