package client.networking;

import java.io.IOException;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;

import common.Command;
import common.Log;
import common.packets.ClientSendCommandPacket;
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

	public void sendCommandStart(Command forward) {
		ClientSendCommandPacket cscp = new ClientSendCommandPacket();
		cscp.command = forward;
		cscp.active = true;
		nt.send(cscp.toData());
		System.out.printf("Notifying server that %s is now ACTIVE\n", forward.toString());
	}

	public void sendCommandEnd(Command forward) {
		ClientSendCommandPacket cscp = new ClientSendCommandPacket();
		cscp.command = forward;
		cscp.active = false;
		nt.send(cscp.toData());
		System.out.printf("Notifying server that %s is now INACTIVE\n", forward.toString());
	}
}
