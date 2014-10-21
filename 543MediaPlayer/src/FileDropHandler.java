import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;

import javax.activation.FileDataSource;
import javax.swing.TransferHandler;

class FileDropHandler extends TransferHandler 
{
 
	PlayerUI gui;
	public FileDropHandler(PlayerUI ui)
	{
		gui = ui;
	}
    public boolean canImport(TransferSupport supp) 
    {
         
        if (!supp.isDrop()) {
            return false;
        }
 
        /* return true if and only if the drop contains a list of files */
        return supp.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
    }
 
 
    public boolean importData(TransferSupport supp) 
    {
    	System.out.print("FileDropHandler::importData() ->");
        
        if (!canImport(supp)) {
            return false;
        }
 
        /* fetch the Transferable */
        Transferable t = supp.getTransferable();
 
        try {
            /* fetch the data from the Transferable */
            Object data = t.getTransferData(DataFlavor.javaFileListFlavor);
 
            /* data of type javaFileListFlavor is a list of files */
            java.util.List fileList = (java.util.List)data;
 
            /* loop through the files in the file list */
            
            for(int i = 0; i < fileList.size(); i++)
            {
            	File file = (File) fileList.get(i);
            	String str = file.getAbsolutePath();
            	gui.addToList(str);
            }
                

        } catch (UnsupportedFlavorException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
 
        return true;
    }
  
}