package game.net;

import java.nio.channels.SocketChannel;

public class ServerDataEvent {
	private SocketChannel channel;
	private NetworkingHost server;
	private ServerClient client;
	private byte[] data;
	
	public ServerDataEvent(NetworkingHost s, SocketChannel sc, ServerClient c, byte[] d)
	{
		this.channel = sc;
		this.server = s;
		this.client = c;
		this.data = d;
	}
	
	public SocketChannel getChannel()
	{
		return this.channel;
	}
	
	public NetworkingHost getServer()
	{
		return this.server;
	}
	
	public ServerClient getClient()
	{
		return this.client;
	}
	
	public byte[] getData()
	{
		return this.data;
	}
}
