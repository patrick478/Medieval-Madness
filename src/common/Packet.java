package common;

public abstract class Packet {
	
	private final short id;
	
	public Packet(short id_) {
		id = id_;
	}
	
	public final short ID()
	{
		return this.id;
	}
	
	public boolean isReply = false; 
	
	public abstract void fromData(DataPacket packet);
	public abstract DataPacket toData();
	public abstract boolean replyValid();
}
