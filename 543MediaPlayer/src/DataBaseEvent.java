
public class DataBaseEvent extends java.util.EventObject 
{
	Object source;
	String fileName;
	String op;
	public DataBaseEvent(Object obj, String name, String type)
	{
		super(obj);
		source = obj;
		fileName = name;
		op = type;
	}
	
	public Object getSource()
	{
		return source;
	}

	public String getFileName() 
	{
		return fileName;
	}
	public String getOperation() 
	{
		return op;
	}
	
}
