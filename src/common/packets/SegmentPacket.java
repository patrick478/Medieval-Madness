package common.packets;

import common.DataPacket;
import common.Packet;
import common.map.Segment;

public class SegmentPacket extends Packet
{
	public static final short ID = 4;
	public Segment segment;
	
	public SegmentPacket() {
		super(ID);
	}

	@Override
	public void fromData(DataPacket packet) {
//		System.out.println(packet.peekShort());
		if(packet.getShort() != SegmentPacket.ID)
			return;
				
		int xPos = packet.getInt();
		int zPos = packet.getInt();
//		System.out.printf("xpos=%d\tzpos=%d\n", xPos, zPos);
		int iDim = packet.getShort();
		int jDim = packet.getShort();
//		System.out.printf("Trying to read %dx%d floats=%d\n", iDim, jDim, iDim*jDim);
		
		float[][] data = new float[iDim][jDim];
		
		for(int i = 0; i < iDim; i++)
		{
			for(int j = 0; j < jDim; j++)
			{
				data[i][j] = packet.getFloat();
			}
		}
		
		this.segment = new Segment(xPos, zPos, data);
	}

	@Override
	public DataPacket toData() {
		DataPacket dp = new DataPacket();
		dp.addShort(SegmentPacket.ID);
		dp.addInt(segment.xPos);
		dp.addInt(segment.zPos);
		
		float[][] data = segment.getData();
//		System.out.printf("Writing %dx%d=%d\n", data.length, data[0].length, data[0].length * data.length);
		dp.addShort(data.length);
		dp.addShort(data[0].length);
		
		for(int i = 0; i < data.length; i++)
		{
			for(int j = 0; j < data[0].length; j++)
			{
				dp.addFloat(data[i][j]);
			}
		}
		return dp;
	}

	@Override
	public boolean replyValid() {
		// TODO Auto-generated method stub
		return false;
	}
}
