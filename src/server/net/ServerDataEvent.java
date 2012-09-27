// full credit to rox-xmlrpc.sourceforge.net
// this class is a pretty much line-for-line his.

package server.net;

import java.nio.channels.SocketChannel;

public class ServerDataEvent {
	public ServerLayer server;
	public SocketChannel socket;
	public byte[] data;
	public String session;
	
	public ServerDataEvent(ServerLayer server, SocketChannel sc, byte[] data, String id)
	{
		this.server = server;
		this.socket = sc;
		this.data = data;
		this.session = id;
	}
}
