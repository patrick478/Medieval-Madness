package game.modelloader;

public class ContentRequestItem {
	private String filename;
	private ContentRequest caller;
	
	public ContentRequestItem(String f, ContentRequest r)
	{
		this.filename = f;
		this.caller = r;
	}
	
	public String getFilename()
	{
		return this.filename;
	}
	
	public ContentRequest getCaller()
	{
		return this.caller;
	}
}
