package common.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import common.DataPacket;
import common.packets.LoginPacket;

public class PacketTests {
	// TODO: More tests!!
	
	@Test
	public void TestLoginPacket() {
		LoginPacket p = new LoginPacket();
		p.username = "benanderson1";
		p.password = "letmein123";
		
		LoginPacket test = new LoginPacket();
		test.fromData(p.toData());
		assertTrue(test.username.equals("benanderson1"));
		assertTrue(test.password.equals("letmein123"));
		assertFalse(test.loginOkay);
		
		p = new LoginPacket();
		p.isReply = true;
		p.loginOkay = true;
		
		test = new LoginPacket();
		test.fromData(p.toData());
		assertTrue(test.isReply);
		assertTrue(test.loginOkay);
	}
}
