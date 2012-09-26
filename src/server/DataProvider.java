package server;

import java.io.PrintStream;

public abstract class DataProvider {
	public abstract void stopAndWait();
	public abstract void startup(PrintStream out);
	public abstract boolean accountExists(String username);
	public abstract boolean passwordCorrect(String username, String password);
	public abstract boolean createAccount(String username, String password);
}
