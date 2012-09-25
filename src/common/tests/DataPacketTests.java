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
}