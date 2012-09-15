package common.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import common.Packet;

public class PacketTests {
	
	@Test
	public void TestByte() {
		Packet p = new Packet();
		
		p.addByte((byte)'j');
		assertEquals(p.getByte(), (byte)'j');
		
		p.addByte((byte)'\u0048');
		p.addByte((byte)'\u0012');
		
		assertFalse(p.getByte() == (byte)'\u0012');
		assertTrue(p.getByte() == (byte)'\u0012');
	}
	
	@Test
	public void TestShort() {
		Packet p = new Packet();
		
		short test = 2;
		p.addShort(test);
		assertEquals(p.getShort(), test);
		
	}
}
