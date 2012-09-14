package common;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.channels.spi.*;
import java.util.Iterator;

public class ServerLayer extends NetworkLayer implements Runnable {
	
	private InetSocketAddress hostAddress;
	private int port;
	
	private ServerSocketChannel listenChannel;
	private Selector selector;
	private Thread serverThread;
	
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
		} catch (Exception e)
		{
			
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
				}
			} catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	private void accept(SelectionKey key) throws IOException {
		ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
		
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
