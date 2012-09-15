package common;

import java.io.*;
import java.util.*;

public class Log
{
	private FileWriter fileOut;
	private boolean writeToConsole = false;
	public static final String osNewline = System.getProperty("line.separator");
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
		
		Date now = new Date();
		String dtnow = now.toString();
		String output = String.format("[%s] %s", dtnow, String.format(format,  args));
		if(this.writeToConsole)
			System.out.printf(output);
		
		try {
			fileOut.write(output);
			fileOut.write("\r\n");
			fileOut.flush();
		} catch (IOException e) {
			System.out.printf("Log :: %s", e.toString());
		}
	}
	
	public void println(String msg)
	{
		this.printf("%s\n", msg);
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
