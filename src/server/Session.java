package server;

import java.nio.channels.*;

public class Session {
	private SocketChannel socket;
	private SessionState state;
	
	public Session() {
		state = SessionState.Welcome;
	}
	
	public SocketChannel getSocket()
	{
		return this.socket;
	}
	
	public SessionState getState()
	{
		return this.state;
	}
}
