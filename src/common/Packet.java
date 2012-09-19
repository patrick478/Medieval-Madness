package common;

public abstract class Packet {
	protected short ID = 0;
	
	public short getID()
	{
		return this.ID;
	}
	
	public boolean isReply = false; 
	
	public abstract void fromData(DataPacket packet);
	public abstract DataPacket toData();
	public abstract boolean replyValid();
}
