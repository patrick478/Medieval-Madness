package client.networking;

import java.io.IOException;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;

import common.Log;
import common.packets.LoginPacket;

import client.Client;

public class NetworkClient {
	private Log log = null;
	
	SocketChannel clientChannel;
	Selector selector;
	NetworkThread nt;
	Client client;
	
	public NetworkClient(Client c)
	{
		this.client = c;
		this.log = c.log;
	}
	
	public boolean isConnected()
	{
		return this.nt.isConnected;
	}

	public void Connect(String hostname, int port) {

	
		nt = new NetworkThread(hostname, port, this);
		Thread t = new Thread(nt);
		t.start();
	}
	
	// begin data methods
	public void beginLogin(String username, String password)
	{
		LoginPacket lp = new LoginPacket();
		lp.username = username;
		lp.password = password;
		this.nt.send(lp.toData());
	}
}
