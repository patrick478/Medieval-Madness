package server.face;

import java.io.*;

import server.Server;

public abstract class ServerFace implements Runnable
{
	public abstract void setup(Server server);
	public abstract void close();
	public abstract PrintStream getOut();
	public abstract InputStream getIn();
}