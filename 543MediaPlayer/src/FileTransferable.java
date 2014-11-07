import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileTransferable implements Transferable {
         
		private final List<File> files;
        String source;
		//DataFlavor myCustomFlavor = new DataFlavor(String[].class, "String Array");
   	   	public FileTransferable(List<File> l, String name) 
   	   	{
   	   		//System.out.println("FileTransferable::constructor(" + name + ")");
   	   		if(name != null)source = name;
   	   		else
   	   			source = "external";
   		    files = l;
   	   	}
   	   	
   	   	public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException 
   	   	{
   	   		//System.out.println("FileTransferable::getTransferData(" + flavor + ")");
   	   		if(!isDataFlavorSupported(flavor))
   	   			throw new UnsupportedFlavorException(flavor);
   	   		if(flavor.equals(DataFlavor.javaFileListFlavor))return files;
   	   		else
   	   		{
   	   			if(source == null)return "external";
   	   			else
   	   				return source;
   	   		}
   	   			
   	   		
   	   	}
   
   	    public DataFlavor[] getTransferDataFlavors() 
   	    {
   	       //System.out.println("FileTransferable::getTransferDataFlavors()");
           return new DataFlavor[] { DataFlavor.javaFileListFlavor, DataFlavor.stringFlavor };
   	    }
   	    
   	   
   	    public boolean isDataFlavorSupported(DataFlavor flavor) 
   	    {
   	    	//System.out.println("FileTransferable::isDataFlavorSupported(" + flavor + ")");
   	    	boolean supported = false;
   	    	if (flavor.equals(DataFlavor.javaFileListFlavor) || flavor.equals(DataFlavor.stringFlavor))supported = true;
   	    	//System.out.println("supported:: " + supported);
           return supported;
   	    }
   }
