package server;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.channels.spi.*;
import java.util.Iterator;

import common.Log;
import common.NetworkLayer;

public class ServerLayer extends NetworkLayer implements Runnable {
	
	private InetSocketAddress hostAddress;
	
	public Server parentServer;
	public Log log;
	public boolean ready = false;
	
	private int port;
	
	private ServerSocketChannel listenChannel;
	private Selector selector;
	private Thread serverThread;
	
	private ServerWorker worker;
	private Thread workerThread;
	
	private  ByteBuffer readBuffer = ByteBuffer.allocate(8192);
	
	public ServerLayer(int port, Server server)
	{
		this.parentServer = server;
		this.port = port;
		this.log = this.parentServer.log;
		this.hostAddress = new InetSocketAddress(this.port);
		try {
			this.selector = this.createSelector();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(this.selector == null) return;
		
		this.ready = true;
	}

	@Override
	public boolean Start() {
		if(!this.ready) return false;
		try {
			this.serverThread = new Thread(this);
			this.serverThread.start();
			
			this.worker = new ServerWorker(this.parentServer);
			this.workerThread = new Thread(this.worker);
			this.workerThread.start();
		} catch (Exception e)
		{
			// TODO: this is a blank catch - fix it fool
		}
		return true;
	}
	
	public void stopAndWait() {
		try {
			synchronized(this.serverThread)
			{
				while(this.serverThread.isAlive())
				{
					this.serverThread.wait(1000);
					this.serverThread.interrupt();
				}
			}
			
			synchronized(this.workerThread)
			{
				while(this.workerThread.isAlive())
				{
					this.workerThread.wait(1000);
					this.workerThread.interrupt();
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		this.parentServer.log.printf("ListenWorker :: starting up\n");
		
		while(!this.parentServer.getStatus().equals(ServerStatus.Stopping)) {
			try {
				this.selector.select();
				if(Thread.interrupted()) break;
				
				Iterator selectedKeys = this.selector.selectedKeys().iterator();
				while(selectedKeys.hasNext())
				{
					SelectionKey key = (SelectionKey)selectedKeys.next();
					selectedKeys.remove();
					
					if(!key.isValid())
						continue;
					
					if(key.isAcceptable())
						this.accept(key);
					else if(key.isReadable());
						this.read(key);
				}
			} catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		this.parentServer.log.printf("ListenWorker :: detected shutdown - stopping\n");
		
	}
	
	private void read(SelectionKey key) throws IOException {
		if(!key.isValid() || !key.isReadable())
			return;
		
		SocketChannel sc;
		try {
			sc = (SocketChannel)key.channel();	
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
		
		this.readBuffer.clear();
		
		int rx =0;
		try {
			rx = sc.read(this.readBuffer);
		} catch(IOException ex) {
			key.cancel();
			sc.close();
			// TODO: Deregister the client from the sessions list
			return;
		}
		
		if(rx == -1)
		{
			// clean shutdown
			// TODO: Degregister the client from the network sessions list
			sc.close();
			key.cancel();
			return;
		}
		
		this.worker.processData(this, sc, this.readBuffer.array(), rx);
	}
	
	private void accept(SelectionKey key) throws IOException {
		ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
		key.attach("123");
		
		SocketChannel sc = ssc.accept();
		sc.configureBlocking(false);
		
		sc.register(this.selector,  SelectionKey.OP_READ);		
	}
	
	private Selector createSelector() throws IOException {
		Selector socketSelector = SelectorProvider.provider().openSelector();
		
		this.listenChannel = ServerSocketChannel.open();
		this.listenChannel.configureBlocking(false);
		
		try {
		this.listenChannel.socket().bind(this.hostAddress);
		} catch(Exception e) {
			this.log.printf("Unable to bind, port probably already in use\n");
			return null;
		}
		
		this.listenChannel.register(socketSelector, SelectionKey.OP_ACCEPT);
		return socketSelector;
	}
}
