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
	
	public DataPacket(byte[] oldData, boolean hasLength)
	{
		buf = ByteBuffer.wrap(oldData);
		int len = oldData.length;
		if(hasLength)
		{
			buf = ByteBuffer.wrap(oldData);
			buf.position(2);
			buf = buf.slice();
			len -= 2;
		}
		writePos = len;
		readPos = 0;
	}
	
	public DataPacket(byte[] oldData)
	{
		this(oldData, true);
	}
	
	public void printPacket()
	{
		for(int i = 0; i < writePos; i++)
			System.out.printf("0x%02X ", this.buf.get(i));
		System.out.println();
	}
	
	public void clear()
	{
		buf = ByteBuffer.allocate(defaultAllocation);
	}
	
	public byte[] getData()
	{
		byte[] data = new byte[writePos+2];
		for(int i = 0; i < writePos; i++)
			data[i+2] = buf.get(i);
		
		byte b1 = (byte)(writePos >>> 8);
		byte b2 = (byte)(writePos & 0xFF);
		
		data[0] = b1;
		data[1] = b2;
		
//		System.out.printf("getData(): rwrote a header with %d bytes.. %02X %02X\n", writePos, b1, b2);
		
		return data;
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
//		System.out.println("Wrote an SHORT");
		buf.putShort(writePos, s);
		writePos += 2;
	}
	
	public short peekShort()
	{
//		System.out.println("Read an SHORT");
//		System.out.printf("this=%s\treadpos=%d\n", this, readPos);
		return buf.getShort(readPos);
	}
	
	public short getShort()
	{
		short s = this.peekShort();
		readPos += 2;
		return s;
	}
	
	public void addInt(int i)
	{
//		System.out.printf("this=%s\twritepos=%d\n", this, writePos);
		buf.putInt(writePos, i);
		writePos += 4;
//		System.out.println("Wrote an INT");
	}
	
	public int getInt()
	{
//		System.out.println("Read an INT");
		readPos += 4;
		return buf.getInt(readPos - 4);
	}
	
	public void addFloat(float f)
	{
//		System.out.println("Wrote an FLOAT");
		buf.putFloat(writePos, f);
		writePos += 4;
	}
	
	public float getFloat()
	{
//		System.out.println("Read an FLOAT");
//		System.out.printf("buf.limit()=%d\treadPos=%d\n", buf.limit(), readPos);
		if(buf.limit() - 3 < readPos)
			return 0;
		
		readPos += 4;
		return buf.getFloat(readPos - 4);
	}
	
	public void addLong(long l)
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
