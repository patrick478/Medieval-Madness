package loadtest;

import java.io.*;
import java.net.*;
import java.util.Random;

import common.DataPacket;

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
			System.out.println("Connection failed. Exiting..\r\n");
			return;
		}
		
		System.out.println("Connecting. Beginning load test");
		
		InputStream is = testSocket.getInputStream();
		OutputStream os = testSocket.getOutputStream();
		
		int success = 0;
		int failure = 0;
		
		DataPacket p = new DataPacket();
		DataPacket reply = new DataPacket();
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
			reply = new DataPacket(data);
			int result = (int)reply.getShort();
			if(result == (a * b))
			{
				success++;
				System.out.printf("Success. Server replied with %d\r\n", result);
			}
			else
			{
				failure++;
				System.out.printf("Failed. Server replied with %d\r\n", result);
			}
			
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		os.close();
		is.close();
		testSocket.close();
		
		System.out.printf("Test complete. Success: %d. Failure: %d. Percentage Pass: %%%f\r\n", success, failure, ((float)success/(float)500) * 100);
		
	}
	
	public static Socket tryConnect(String address, int port) {
		Socket testSocket = null;
		System.out.printf("Attempting to connect to %s:%d\r\n", address, port);
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
				System.out.printf("Success!\r\n");
				return testSocket;
			}
		}
		return null;
	}
}
