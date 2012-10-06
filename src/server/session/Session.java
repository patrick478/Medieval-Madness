package server.session;

import java.nio.channels.*;
import java.util.ArrayList;
import java.util.List;

import common.DataPacket;
import common.Packet;

import server.game.ServerPlayer;
import server.net.ServerLayer;

public class Session {
	private SocketChannel socket;
	private SessionState state;
	private ServerLayer parentLayer;
	private ServerPlayer player;
	private int substate = 0;
	private List<Long> sentSegments;
	
	public Session(SocketChannel sc, ServerLayer pl) {
		state = SessionState.Welcome;
		socket = sc;
		this.parentLayer = pl;
		this.sentSegments = new ArrayList<Long>();
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

	public void setPlayer(ServerPlayer player) {
		this.player = player;
	}
	
	public ServerPlayer getPlayer()
	{
		return this.player;
	}
	
	public boolean hasSegment(long id)
	{
		return sentSegments.contains(id);
	}
	
	public void sentSegment(long id)
	{
		sentSegments.add(id);
	}
}
