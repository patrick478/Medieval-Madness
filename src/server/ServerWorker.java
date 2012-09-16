package server;

import java.nio.channels.*;
import java.util.*;

import common.Packet;

public class ServerWorker implements Runnable {
	private List<ServerDataEvent> queue = new LinkedList<ServerDataEvent>();
	private Server parentServer;
	
	public ServerWorker(Server s) {
		this.parentServer = s;
	}
	
	public void processData(ServerLayer server, SocketChannel sc, byte[] data, int dsize, String id)
	{
		byte[] dataCopy = new byte[dsize];
		System.arraycopy(data, 0, dataCopy, 0, dsize);
		synchronized(queue) {
			queue.add(new ServerDataEvent(server, sc, dataCopy, id));
			queue.notify();
		}
	}

	@Override
	public void run() {
		ServerDataEvent dataEvent;
		
		boolean exiting = false;
		
		this.parentServer.log.printf("ServerWorker :: starting up\n");
		
		while(!exiting)
		{
			synchronized(queue) {
				while(queue.isEmpty()) {
					try {
						queue.wait();
					} catch(InterruptedException ie) {
						exiting = true;
						break;
					}
				}
				if(exiting) break;
				dataEvent = (ServerDataEvent)queue.remove(0);
			}
			
			System.out.printf("Recieved %d byte%s [SessionID=%s]\n", dataEvent.data.length, (dataEvent.data.length == 1 ? "" : "s"), dataEvent.session);
			Packet p = new Packet(dataEvent.data);
			
			//dataEvent.server.send(dataEvent.socket, dataEvent.data);
		}
		
		this.parentServer.log.printf("ServerWorker :: detected shutdown - stopping\n");
	}	
}
