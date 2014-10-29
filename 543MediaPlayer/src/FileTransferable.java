import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class FileTransferable implements Transferable {
         
		private final List files;
        //Object source;
       
   	   	public FileTransferable(List l) 
   	   	{
   		   files = l;
   	   	}
      
   	   	public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException 
   	   	{
   	   		if(!isDataFlavorSupported(flavor))
   	   			throw new UnsupportedFlavorException(flavor);
            return files;
   	   	}
   
   	    public DataFlavor[] getTransferDataFlavors() 
   	    {
           return new DataFlavor[] { DataFlavor.javaFileListFlavor };
   	    }
   	    
   	   
   	    public boolean isDataFlavorSupported(DataFlavor flavor) 
   	    {
           return flavor.equals(DataFlavor.javaFileListFlavor);
   	    }
   }