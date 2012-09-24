package server;

import java.nio.channels.*;

public class Session {
	private SocketChannel socket;
	private SessionState state;
	private int substate = 0;
	
	public Session(SocketChannel sc) {
		state = SessionState.Welcome;
		socket = sc;
	}
	
	public SocketChannel getSocket()
	{
		return this.socket;
	}
	
	public SessionState getState()
	{
		return this.state;
	}

	public int getSubstate()
	{
		return this.substate;
	}
	
	public void setState(SessionState s)
	{
		this.state = s;
	}

	public void setSubstate(int substate) {
		this.substate = substate;
	}
}
