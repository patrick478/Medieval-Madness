package common;

import java.io.*;

public class Log
{
	private FileWriter fileOut;
	private boolean writeToConsole = false;
	public Log(String file, boolean writeToConsole)
	{
		try {
			fileOut = new FileWriter(file);
		} catch (IOException e) {
			System.out.printf("Log :: %s", e.toString());
		}
		
		this.writeToConsole = writeToConsole;
	}
	
	public void printf(String format, Object ... args)
	{
		String output = String.format(format,  args);
		if(this.writeToConsole)
			System.out.printf(output);
		
		if(!output.endsWith("\n"))
			output += "\n";
		
		try {
			fileOut.write(output);
			fileOut.flush();
		} catch (IOException e) {
			System.out.printf("Log :: %s", e.toString());
		}
	}
	
	public void println(String msg)
	{
		String output = msg += "\n";
		if(this.writeToConsole)
			System.out.printf(output);
		
		try {
			fileOut.write(output);
			fileOut.flush();
		} catch (IOException e) {
			System.out.printf("Log :: %s", e.toString());
		}
	}
	
	public void close()
	{
		try {
			this.fileOut.close();
		} catch (IOException e) {
			System.out.printf("Log :: %s", e.toString());
		}
	}
}
