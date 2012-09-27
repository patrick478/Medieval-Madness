package server.datalayer;

import java.util.*;

public class DataJob
{
	String query = "";
	List<ArrayList<Object>> data = new ArrayList< ArrayList < Object > >();
	public boolean isNonQuery = false;
			
	public DataJob(String sql)
	{
		this.query = sql;
	}
	
	public String sql()
	{
		return this.query;
	}
}
