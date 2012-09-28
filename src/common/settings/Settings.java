package common.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * @author "Ben Anderson (BageDevimo)"
 * A helper class to make reading and writing server settings files easy as
 */
public class Settings {
	private String filename = "default.mms";
	private Map<String, String> settings = new HashMap<String, String>();
	private static Map<Integer, SettingsVersion> settingsVersions = new HashMap<Integer, SettingsVersion>();
	private SettingsVersion sv = null;
	static {
		settingsVersions.put(1,  new SettingsVersion1());
	}
	
	/***
	 * A default constructor taking a file name of the settings file to open
	 * @param settingsFile A string containing the file name of the file
	 * @throws FileNotFoundException Throws a FileNotFoundException if the file doesn't exist at the specified path
	 */
	public Settings(String settingsFile) throws FileNotFoundException
	{
		this.filename = settingsFile;
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(this.filename);
		} catch(FileNotFoundException fe)
		{
			sv = settingsVersions.get(1);
			return;
		}
		
		Scanner scanner = new Scanner(fis);
		int version = 1;
		if(scanner.hasNext())
		{
			String vCode = scanner.next();
			if(vCode.equals("version") && scanner.hasNextInt())
			{
				version = scanner.nextInt();
			}
		}
		
		if(settingsVersions.containsKey(version))
			sv = settingsVersions.get(version);
		else
		{
			scanner.close();
			throw new RuntimeException("Settings version not supported. Requires settings version " + version);
		}
		
		sv.read(scanner, this.settings);
		
		scanner.close();
	}	
	
	public void saveChanges()
	{
		FileWriter fw = null;
		try {
			fw = new FileWriter(this.filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PrintWriter out = new PrintWriter(fw);
		
		sv.write(out,  this.settings);
		out.close();
	}
	
	public void setValue(String name, Object value)
	{
		this.settings.put(name, value.toString());
	}
	
	public String getValue(String name, Object defaultValue)
	{
		if(!this.settings.containsKey(name))
			this.setValue(name, defaultValue.toString());
		return this.settings.get(name);
	}
	
	public int getIntValue(String name, int val)
	{
		if(!this.settings.containsKey(name))
			this.setValue(name, Integer.toString(val));
		return Integer.parseInt(this.settings.get(name));
	}
	
	public long getLongValue(String name, long val)
	{
		if(!this.settings.containsKey(name))
			this.setValue(name, Long.toString(val));
		return Long.parseLong(this.settings.get(name));		
	}
}
