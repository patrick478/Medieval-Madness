package common;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.Queue;

public class DataPacket {
	private LinkedList<Byte> data = new LinkedList<Byte>();
	private static final String stringEncoding = "US-ASCII"; // happy, Josh/Ben!? :P
	
	public DataPacket()
	{
		
	}
	
	public DataPacket(byte[] b) {
		for(int i = 0; i < b.length; i++)
			data.add(b[i]);
	}
	
	// TODO: rewrite this function to use .toArray()
	public byte[] getData() {
		byte[] ret = new byte[this.data.size()];
		for(int i = 0; i < this.data.size(); i++)
			ret[i] = this.data.get(i);
		
		return ret;
	}
	
	public void clear() {
		this.data.clear();
	}
	
	public void addByte(Byte b)
	{
		data.add(b);
	}
	
	public byte getByte() {
		return (data.remove());
	}
	
	private void addShortInternal(short s)
	{
		byte[] bytes = new byte[2];
		bytes[1] = (byte)(s >>> 0);
		bytes[0] = (byte)(s >>> 8);
		data.add(bytes[0]);
		data.add(bytes[1]);
	}
	
	public void addShort(short s)
	{
		this.addShortInternal(s);
	}
	
	public void addShort(int s) {
		this.addShortInternal((short)s);
	}
	
	public short getShort()
	{
		byte b1 = data.remove();
		byte b2 = data.remove();
		
		short result = (short)(((b2 & 0xFF) << 0) + (b1 << 8));
		return result;
	}
	
	public short peekShort()
	{
		byte b1 = data.get(0);
		byte b2 = data.get(1);
		short result = (short)(((b2 & 0xFF) << 0) + (b1 << 8));
		return result;
	}
	
	public void addString(String s)
	{
		int stringLength = s.length();
		this.addShort(stringLength);
		byte[] strBytes = null;
		
		try {
			strBytes = s.getBytes(DataPacket.stringEncoding);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		for(int i = 0; i < strBytes.length; i++)
			this.data.add(strBytes[i]);
	}
	
	public String getString()
	{
		int stringLength = this.getShort();
		if(this.data.size() < stringLength)
			return null;
		
		byte[] stringBytes = new byte[stringLength];
		for(int i = 0; i < stringLength; i++)
			stringBytes[i] = this.data.remove();
		
		try {
			return new String(stringBytes, DataPacket.stringEncoding);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
