public class PlaylistEvent extends java.util.EventObject 
{
	Object source;
	String playlistName;
	String eventType;
	public PlaylistEvent(Object obj, String name, String type)
	{
		super(obj);
		source = obj;
		playlistName = name;
		eventType = type;
	}
	
	public Object getSource()
	{
		return source;
	}

	public String getPlaylistName() 
	{
		return playlistName;
	}
	public String getType()
	{
		return eventType;
	}
	
}