package game.net.packets;

import common.DataPacket;

public class WelcomePacket extends Packet
{
	public static final short ID = 1;
	public WelcomePacket()
	{
		super(ID);
	}
	
	public int playerIndex = 999;
	public int maxPlayers = 1;
	
	@Override
	public void fromData(DataPacket packet) {
		if(packet.getShort() != WelcomePacket.ID)
			return;
		
		this.playerIndex = packet.getShort();
		this.maxPlayers = packet.getShort();
	}
	@Override
	public DataPacket toData() {
		DataPacket dp = new DataPacket();
		dp.addShort(WelcomePacket.ID);
		dp.addShort(playerIndex);
		dp.addShort(this.maxPlayers);
		return dp;
	}
}
