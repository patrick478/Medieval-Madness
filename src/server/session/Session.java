package server.session;

import java.nio.channels.*;

import common.DataPacket;
import common.Packet;

import server.net.ServerLayer;

public class Session {
	private SocketChannel socket;
	private SessionState state;
	private ServerLayer parentLayer;
	private int substate = 0;
	
	public Session(SocketChannel sc, ServerLayer pl) {
		state = SessionState.Welcome;
		socket = sc;
		this.parentLayer = pl;
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
	
	public void send(Packet dp)
	{
		this.send(dp.toData());
	}
	
	public void send(DataPacket dp)
	{
		this.parentLayer.send(this.socket, dp.getData());
	}
}
