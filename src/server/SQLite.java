package server;

import java.io.*;
import java.security.*;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.logging.*;

import com.almworks.sqlite4java.*;
import common.Log;

public class SQLite extends DataProvider implements Runnable {
	
	String dbFilename = "save/unknown.db";
	SQLiteConnection db = null;
	Log log = null;
	Thread workerThread = null;
	
	private boolean started = false;
	
	private BlockingQueue<DataJob> jobQueue = new LinkedBlockingQueue<DataJob>();
	
	public SQLite(String filename)
	{
		this.dbFilename = filename;
	}
	
	public void run()
	{
		start();
		DataJob current = null;
		while(true)
		{
			try {
				 current = jobQueue.take();
			} catch (InterruptedException e) {
				break;
			}
			
			SQLiteStatement st = null;
			try {
				if(!current.isNonQuery)
				{
//					System.out.println("Running query: " + current.sql());
					st = this.db.prepare(current.sql());
					while(st.step())
					{
						ArrayList<Object> row = new ArrayList<Object>();
						for(int i = 0; i < st.columnCount(); i++)
						{
							row.add(st.columnValue(i));
						}
						current.data.add(row);
					}
				}
				else
				{
//					System.out.println("Running non-query: " + current.sql());
					this.db.exec(current.sql());
				}
			} catch(Exception e)
			{
			}
			
			synchronized(current) 
			{
				current.notify();
			}
		}
	}
	
	private boolean start()
	{
		if(started) return true;
		
		this.log.printf("Starting up..\n");
		
		Logger.getLogger("com.almworks.sqlite4java").setLevel(Level.OFF);
		this.db = new SQLiteConnection(new File(this.dbFilename));
		try {
			this.db.open();
		} catch (SQLiteException e) {
			return false;
		}
		
		started = true;
		
		if(!tableExists("users") && createUsersTable())
			this.log.printf("Created users table.\n");
		
		return true;
	}
	
	public String getSalt(String username)
	{
		String query = String.format("SELECT salt FROM users WHERE username='%s';", username);
		
		DataJob dj = new DataJob(query);
		this.jobQueue.add(dj);
		try {
			synchronized(dj)
			{
				dj.wait();
			}
		} catch (InterruptedException e) {
			return null;
		}
		
		if(dj.data.size() != 1 && dj.data.get(0).size() != 1)
			return (String)dj.data.get(0).get(0);
		
		return null;
	}

	@Override
	public boolean passwordCorrect(String username, String password) {
    	String salt = this.getSalt(username);
    	if(salt == null) return false;
    	
    	String password_hash = getPasswordHash(password, salt);
		String query = String.format("SELECT COUNT(*) FROM users WHERE username='%s' AND password_hash='%s';", username, password_hash);
		
		DataJob dj = new DataJob(query);
		this.jobQueue.add(dj);
		try {
			synchronized(dj)
			{
				dj.wait();
			}
		} catch (InterruptedException e) {
			return false;
		}
		return true;
	}
	
	// THIS ONE
	@Override
	public boolean accountExists(String username) {
		String query = String.format("SELECT COUNT(*) FROM users WHERE username='%s'", username);
		DataJob dj = new DataJob(query);
		this.jobQueue.add(dj);
		try {
			synchronized(dj)
			{
				dj.wait();
			}
		} catch (InterruptedException e) {
			return false;
		}
		
		if(dj.data.size() == 1 && dj.data.get(0).size() == 1 && (Integer)dj.data.get(0).get(0) > 0)
			return true;
		
		return false;
	}
	
	@Override
	public boolean createAccount(String username, String password) {
		if(this.accountExists(username)) return false;
		
    	String salt = Long.toString(System.currentTimeMillis());
    	String password_hash = getPasswordHash(password, salt);
		String query = String.format("INSERT INTO users (username, password_hash, salt) VALUES ('%s', '%s', '%s');", username, password_hash, salt);
		
		DataJob dj = new DataJob(query);
		dj.isNonQuery = true;
		
		this.jobQueue.add(dj);
		try {
			synchronized(dj)
			{
				dj.wait();
			}
		} catch (InterruptedException e) {
			return false;
		}		
		
    	
		return true;
	}
	
	// These may not be used in the process thread
	private boolean tableExists(String tableName) {
		if(!this.start()) return false;
		
		int nTables = 0;
		SQLiteStatement st = null;
		try {
			st = this.db.prepare(String.format("SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='%s';", tableName));
			st.step();
			nTables = st.columnInt(0);
		}
		catch (SQLiteException e) {
			return false;
		}
		if(nTables > 0) return true;
		return false;
	}
	
	private boolean createUsersTable()
	{
		if(!start()) return false;
		
		String statement = "CREATE TABLE 'users' ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'username' VARCHAR(32), 'password_hash' VARCHAR(65), 'salt' VARCHAR(20))";
		try {
			this.db.exec(statement);
		} catch (SQLiteException e) {
			return false;
		}
		return true;
	}
	
	// These are native	
	private String getPasswordHash(String password, String salt)
	{
		String password_pre_hash = String.format("%s%s%s", salt, password, salt);
		return getHash(password_pre_hash);
	}
	
	private String getHash(String msg)
	{
        MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        md.update(msg.getBytes());
 
        byte byteData[] = md.digest();
 
        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
 
        //convert the byte to hex format method 2
        StringBuffer hexString = new StringBuffer();
    	for (int i=0;i<byteData.length;i++) {
    		String hex=Integer.toHexString(0xff & byteData[i]);
   	     	if(hex.length()==1) hexString.append('0');
   	     	hexString.append(hex);
    	}
    	return hexString.toString();
	}

	@Override
	public void startup(PrintStream out) {
		this.log = new Log("db.log", true, out);
		this.log.setPrefix("(DataProvider-SQLite) ");
		
		this.workerThread = new Thread(this);
		this.workerThread.start();
	}

	@Override
	public void stopAndWait() {
		try {
			synchronized(this.workerThread)
			{
				while(this.workerThread.isAlive())
				{
					this.workerThread.wait(1000);
					this.workerThread.interrupt();
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
