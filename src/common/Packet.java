package common;

import java.util.LinkedList;
import java.util.Queue;

public class Packet {
	private LinkedList<Byte> data = new LinkedList<Byte>();
	
	public Packet()
	{
		
	}
	
	public Packet(byte[] b) {
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
}
