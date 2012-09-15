package server;

import java.nio.channels.*;
import java.util.*;

public class ServerWorker implements Runnable {
	private List<ServerDataEvent> queue = new LinkedList<ServerDataEvent>();
	
	public void processData(ServerLayer server, SocketChannel sc, byte[] data, int dsize)
	{
		byte[] dataCopy = new byte[dsize];
		System.arraycopy(data, 0, dataCopy, 0, dsize);
		synchronized(queue) {
			queue.add(new ServerDataEvent(server, sc, dataCopy));
			queue.notify();
		}
	}

	@Override
	public void run() {
		ServerDataEvent dataEvent;
		
		while(true)
		{
			synchronized(queue) {
				while(queue.isEmpty()) {
					try {
						queue.wait();
					} catch(InterruptedException ie) {
						// TODO: Blank catch.. really?
					}
				}
				dataEvent = (ServerDataEvent)queue.remove(0);
			}
			
			System.out.printf("Recieved %d byte%s\n\t", dataEvent.data.length, (dataEvent.data.length == 1 ? "s" : ""));
			for(int i = 0; i < dataEvent.data.length; i++)
				System.out.printf("0x%02X", dataEvent.data[i]);
			System.out.printf("\n\n");
			//dataEvent.server.send(dataEvent.socket, dataEvent.data);
		}
	}	
}
