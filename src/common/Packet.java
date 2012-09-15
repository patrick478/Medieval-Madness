package common;

import java.util.*;
import java.io.*;

public class Packet {
	private Queue<Byte> data = new LinkedList<Byte>();
	
	public Packet()
	{
		
	}
	
	public Packet(byte[] b) {
		for(int i = 0; i < b.length; i++)
			data.add(b[i]);
	}
	
	public void addByte(Byte b)
	{
		data.add(b);
	}
	
	public byte getByte() {
		return (data.remove());
	}
	
	public void addShort(Short s)
	{
		byte[] bytes = new byte[2];
		bytes[1] = (byte)(s >>> 0);
		bytes[0] = (byte)(s >>> 8);
		data.add(bytes[0]);
		data.add(bytes[1]);
	}
	
	public short getShort()
	{
		byte b1 = data.remove();
		byte b2 = data.remove();
		
		short result = (short)(((b2 & 0xFF) << 0) + (b1 << 8));
		return result;
	}
}
