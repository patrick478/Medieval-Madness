package common.settings;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Scanner;

public class SettingsVersion1 extends SettingsVersion {

	@Override
	public void read(Scanner scanner, Map<String, String> settings) {
		while(scanner.hasNextLine())
		{
			String line = scanner.nextLine();
			
			if(line.length() <= 0) continue;
			
			String[] components = line.split("=");
			String name = components[0].toLowerCase();
			
			settings.put(name, components[1]);
		}
	}

	@Override
	public void write(PrintWriter writer, Map<String, String> settings) {
		writer.println("version 1");
		for(String name : settings.keySet())
		{
			writer.printf("%s=%s\n",  name, settings.get(name));
		}
	}

}
