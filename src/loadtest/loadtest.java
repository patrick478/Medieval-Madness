package loadtest;

import java.io.*;
import java.net.*;
import java.util.Random;

import common.Packet;

public class loadtest {
	public static void main(String[] args) throws IOException {
		Socket testSocket = null;
		
		String target = "trentst.acidic.co.nz";
		int port = 14121;
		
		int numEqs = 500;
		int interval = 100;
		
		testSocket = tryConnect(target, port);
		
		if(testSocket == null)
			testSocket = tryConnect("localhost", port);
		
		if(testSocket == null)
		{
			System.out.println("Connection failed. Exiting..\n");
			return;
		}
		
		System.out.println("Connecting. Beginning load test");
		
		InputStream is = testSocket.getInputStream();
		OutputStream os = testSocket.getOutputStream();
		
		Packet p = new Packet();
		Packet reply = new Packet();
		for(int i = 0; i < numEqs; i++) {
			Random r = new Random();
			int a = r.nextInt(20) + 1;
			int b = r.nextInt(20) + 1;
			p.addShort(a);
			p.addShort(b);
			os.write(p.getData());
			System.out.printf("Test %d: Requesting %d*%d... ", i+1, a, b);
			p.clear();
			byte[] data = new byte[2];
			is.read(data, 0, 2);
			reply = new Packet(data);
			int result = (int)reply.getShort();
			if(result == (a * b))
				System.out.printf("Success. Server replied with %d\n", result);
			else
				System.out.printf("Failed. Server replied with %d\n", result);
			
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static Socket tryConnect(String address, int port) {
		Socket testSocket = null;
		System.out.printf("Attempting to connect to %s:%d\n", address, port);
		for(int i = 0; i < 3; i++)
		{
			System.out.printf("Connection attempt %d... ", i+1);
			try {
				testSocket = new Socket(address, port);			
			} catch(ConnectException e) {
				System.out.println("Failed");
			} catch (UnknownHostException e) {
				System.out.println("Failed");
			} catch (IOException e) {
				System.out.println("Failed");
			}
			
			if(testSocket != null && testSocket.isConnected()) 
			{
				System.out.printf("Success!\n");
				return testSocket;
			}
		}
		return null;
	}
}
