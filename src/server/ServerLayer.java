package server;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.channels.spi.*;
import java.util.Iterator;

import common.NetworkLayer;

public class ServerLayer extends NetworkLayer implements Runnable {
	
	private InetSocketAddress hostAddress;
	private int port;
	
	private ServerSocketChannel listenChannel;
	private Selector selector;
	private Thread serverThread;
	
	private ServerWorker worker;
	private Thread workerThread;
	
	private  ByteBuffer readBuffer = ByteBuffer.allocate(8192);
	
	public ServerLayer(int port)
	{
		this.port = port;
		this.hostAddress = new InetSocketAddress(this.port);
		try {
			this.selector = this.createSelector();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean Start() {
		try {
			this.serverThread = new Thread(this);
			this.serverThread.start();
			
			this.worker = new ServerWorker();
			this.workerThread = new Thread(this.worker);
			this.workerThread.start();
		} catch (Exception e)
		{
			// TODO: this is a blank catch - fix it fool
		}
		return true;
	}

	@Override
	public void run() {
		while(true) {
			try {
				this.selector.select();
				
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
		
		this.listenChannel.socket().bind(this.hostAddress);
		
		this.listenChannel.register(socketSelector, SelectionKey.OP_ACCEPT);
		return socketSelector;
	}
}
