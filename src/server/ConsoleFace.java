package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class ConsoleFace extends ServerFace implements Runnable {
	Server parentServer;
	@Override
	public void setup(Server server) {
		this.parentServer = server;
	}

	@Override
	public void close() {
	}

	@Override
	public PrintStream getOut() {
		return System.out;
	}

	@Override
	public InputStream getIn() {
		return System.in;
	}

	@Override
	public void run() {
		// read console commands
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
		String input = "";
		while(true)
		{
			try {
				input = console.readLine();
			} catch (IOException e) {
				//this.log.printf("Server :: Console Error() :: %s\n", e.toString());
				break;
			}
			parentServer.HandleCommandLine(input);
		}
	}

}
