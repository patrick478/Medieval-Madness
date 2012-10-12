package common.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import common.DataPacket;

public class DataPacketTests {
	
	@Test
	public void TestByte() {
		DataPacket p = new DataPacket();
		
		p.addByte((byte)'j');
		assertEquals(p.getByte(), (byte)'j');
		
		p.addByte((byte)'\u0048');
		p.addByte((byte)'\u0012');
		
		assertFalse(p.getByte() == (byte)'\u0012');
		assertTrue(p.getByte() == (byte)'\u0012');
	}
	
	@Test
	public void TestShort() {
		DataPacket p = new DataPacket();
		
		short test = 2;
		p.addShort(test);
		assertEquals(p.getShort(), test);
		
		DataPacket pk = new DataPacket();
		pk.addShort(5);
		pk.addInt(50123);
		assertEquals(5, pk.peekShort());
		assertEquals(5, pk.getShort());
	}
	
	@Test
	public void TestString()
	{
		DataPacket p = new DataPacket();
		testString(p, "This is a cow");
		testString(p, "The quick brown fox jumped over the lazy dog!!#Q0312454");
		
		p.addString("A");
		p.addString("B");
		assertTrue("A".equals(p.getString()));
		assertTrue("B".equals(p.getString()));
	}
	
	public void testString(DataPacket p, String s)
	{
		p.addString(s);
		String r = p.getString();
		assertTrue(r.equals(s));
	}
	
	@Test
	public void TestInt()
	{
		DataPacket p = new DataPacket();
		p.addInt(18);
		assertEquals(18, p.getInt());
	}
	
	@Test
	public void TestLong()
	{
		DataPacket p = new DataPacket();
		long val = 190123123287398l;
		p.addLong(val);
		assertEquals(val, p.getLong());
	}
	
	@Test
	public void TestGetData()
	{
		DataPacket p = new DataPacket();
		p.addShort(1);

		for(int i = 0; i < p.getData().length; i++)
			System.out.printf("0x%02X ", p.getData()[i]);
		System.out.println();
	}
	
	@Test
	public void TestBoolean()
	{
		DataPacket dp = new DataPacket();
		dp.addBoolean(true);
		dp.addBoolean(false);
		
		DataPacket test = new DataPacket(dp.getData());
		assertTrue(test.getBoolean());
		assertFalse(test.getBoolean());
	}
}
