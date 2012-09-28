package common.settings;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Scanner;

public abstract class SettingsVersion {
	public abstract void read(Scanner scanner, Map<String, String> settings);
	public abstract void write(PrintWriter write, Map<String, String> settings);
}
