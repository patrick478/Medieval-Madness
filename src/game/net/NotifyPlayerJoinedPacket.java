package game.net;

import common.DataPacket;

import game.net.packets.Packet;

public class NotifyPlayerJoinedPacket extends Packet {

	public static final short ID = 12312;
	public NotifyPlayerJoinedPacket()
	{
		super(ID);
	}
	
	int nPlayers = 0;
	
	@Override
	public void fromData(DataPacket packet) {
		if(packet.getShort() != ID)
			return;
		
		this.nPlayers = packet.getShort();
	}

	@Override
	public DataPacket toData() {
		DataPacket dp = new DataPacket();
		dp.addShort(ID);
		dp.addShort(nPlayers);
		return dp;
	}

}
