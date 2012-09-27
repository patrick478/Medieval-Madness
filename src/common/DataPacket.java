package common;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class DataPacket {
	ByteBuffer buf = null;
	int readPos = 0;
	int writePos = 0;
	
	public static final String stringEncoding = "US-ASCII";
	private static final int defaultAllocation = 8096;
	
	public DataPacket()
	{
		buf = ByteBuffer.allocate(defaultAllocation);
	}
	public DataPacket(int s)
	{
		buf = ByteBuffer.allocate(s);
	}
	
	public DataPacket(byte[] oldData)
	{
		buf = ByteBuffer.wrap(oldData);
		writePos = 0;
		readPos = 0;
	}
	
	public void clear()
	{
		buf = ByteBuffer.allocate(defaultAllocation);
	}
	
	public byte[] getData()
	{
		return buf.array();
	}
	
	public void addByte(Byte b)
	{
		buf.put(writePos++, b);
	}
	
	public byte getByte()
	{
		return buf.get(readPos++);
	}
	
	public void addShort(int s)
	{
		this.addShort((short)s);
	}
	
	public void addShort(short s)
	{
		buf.putShort(writePos, s);
		writePos += 2;
	}
	
	public short peekShort()
	{
		return buf.getShort(readPos);
	}
	
	public short getShort()
	{
		readPos += 2;
		return buf.getShort(readPos-2);
	}
	
	public void addInt(int i)
	{
		buf.putInt(writePos, i);
		writePos += 4;
	}
	
	public int getInt()
	{
		readPos += 4;
		return buf.getInt(readPos - 4);
	}
	
	public void addFloat(float f)
	{
		buf.putFloat(writePos, f);
		writePos += 4;
	}
	
	public float getFloat()
	{
		readPos += 4;
		return buf.getFloat(readPos - 4);
	}
	
	public void addLong(Long l)
	{
		buf.putLong(writePos, l);
		writePos += 8;
	}
	
	public long getLong()
	{
		readPos += 8;
		return buf.getLong(readPos - 8);
	}
	
	public void addString(String s)
	{
		short strLen = (short)s.length();
		
		byte[] bytes = null;
		try {
			 bytes = s.getBytes(stringEncoding);
		} catch (UnsupportedEncodingException e) {
			return;
		}
		
		this.addShort(strLen);
		buf.position(writePos);
		buf.put(bytes);
		this.writePos += strLen;
	}
	
	public String getString()
	{
		short strlen = this.getShort();
		
		byte[] bytes = new byte[strlen];
		buf.position(readPos);
		buf.get(bytes, 0,  strlen);
		this.readPos += strlen;
		try {
			return new String(bytes, stringEncoding);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
}
