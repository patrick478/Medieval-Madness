package common;

public abstract class Packet {
	public final static short ID = 0;
	
	public boolean isReply = false; 
	
	public abstract void fromData(DataPacket packet);
	public abstract DataPacket toData();
	public abstract boolean replyValid();
}
