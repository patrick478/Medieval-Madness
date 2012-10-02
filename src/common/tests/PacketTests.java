package common.tests;

import static org.junit.Assert.*;
import initial3d.engine.Quat;
import initial3d.engine.Vec3;

import org.junit.Test;

import common.DataPacket;
import common.map.Segment;
import common.map.SegmentGenerator;
import common.packets.ChangeEntityModePacket;
import common.packets.EntityMode;
import common.packets.EntityUpdatePacket;
import common.packets.LoginPacket;
import common.packets.SegmentPacket;

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
	
	@Test
	public void TestSegmentPacket() {
		Segment seg = new SegmentGenerator(System.currentTimeMillis()).getSegment(127, 127);
		SegmentPacket p = new SegmentPacket();
		p.segment = seg;
		
		byte[] data = p.toData().getData();
		
		SegmentPacket test = new SegmentPacket();
		test.fromData(new DataPacket(data));
		
//		System.out.printf("%d,%d-size=%d\n", seg.xPos, seg.zPos, seg.getData().length);
//		System.out.printf("%d,%d-size=%d\n", test.segment.xPos, test.segment.zPos, test.segment.getData().length);
		
		float[][] d1 = seg.getData();
		//System.out.println(test.segment.toString());
		float[][] d2 = test.segment.getData();
		
		for(int i = 0; i < d1.length; i++)
		{
			for(int j = 0; j < d1[0].length; j++)
			{
//				System.out.printf("%f %f\n", d1[i][j], d2[i][j]);
				assertTrue(Math.abs(d1[i][j] - d2[i][j]) < 0.00001);
			}
		}
	}
	
	@Test
	public void TestChangeEntityModePacket()
	{
		ChangeEntityModePacket pk = new ChangeEntityModePacket();
		long id = 87309709341283123l;
		EntityMode em = EntityMode.Born;
		
		pk.entityID = id;
		pk.mode = em;
		
		DataPacket dp = pk.toData();
		
		ChangeEntityModePacket test = new ChangeEntityModePacket();
		test.fromData(new DataPacket(dp.getData()));
		
		assertEquals(test.entityID, id);
		assertEquals(test.mode, em);
	}
	
	@Test
	public void TestEntityUpdatePacket(){
		
		EntityUpdatePacket pk = new EntityUpdatePacket();
		long id = 68743268374683274l;
		Vec3 position = Vec3.create(463874f, 67328f, 74983f);
		Vec3 velocity = Vec3.create(463874f, 67328f, 74983f);
		Quat orientation = Quat.create(463874f, 67328f, 74983f, 4554f);
		Vec3 angularVel = Vec3.create(463874f, 67328f, 74983f);

		pk.entityID = id;
		pk.position = position;
		pk.orientation = orientation;
		pk.angularVel = angularVel;
		pk.velocity = velocity;
		
		DataPacket dp = pk.toData();

		EntityUpdatePacket test = new EntityUpdatePacket();
		test.fromData(new DataPacket(dp.getData()));
		
		assertEquals(test.entityID, id);
		assertEquals(test.position, position);
		assertEquals(test.orientation, orientation);
		assertEquals(test.angularVel, angularVel);
		assertEquals(test.velocity, velocity);


	}

	
}
