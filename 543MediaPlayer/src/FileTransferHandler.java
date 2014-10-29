import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableModel;

class FileTransferHandler extends TransferHandler 
{
 
	TablePanel gui;
	List<File> filenames;
	JComponent source;
	
	public FileTransferHandler(TablePanel tp)
	{
		gui = tp;
	}
	
	public int getSourceActions(JComponent c) 
	{
		source = c;
	    return COPY;
	}

	protected Transferable createTransferable(JComponent c) 
	{
	    
		JTable table = (JTable) c;
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		filenames = new ArrayList<File>();
		int[] rows = table.getSelectedRows();
		//System.out.println("Need to create a transferable containing files:");
		for(int i = 0; i < rows.length; i++)
		{
			//System.out.println((String)model.getValueAt(rows[i],model.getColumnCount()-1));
			String filename = (String)model.getValueAt(rows[i],model.getColumnCount()-1);
			filenames.add(new File(filename));
		}
		Transferable t = new FileTransferable(filenames);
		
		return t;
	}

	protected void exportDone(JComponent c, Transferable t, int action) {
	    if (action == MOVE) {
	       // c.removeSelection();
	    }
	}

    public boolean canImport(TransferSupport supp) 
    {
         
        if (!supp.isDrop()) {
            return false;
        }
        /* return true if and only if the drop transferable contains a list of files */
        return supp.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
    }
 
 
    public boolean importData(TransferSupport supp) 
    {
    	System.out.print("FileDropHandler::importData() ->");
        
        if (!canImport(supp)) {
            return false;
        }
        
        /* don't allow a component to drag and drop on itself */
        if(source == supp.getComponent())return false;
        
        /* fetch the Transferable  */
        /* the transferable is an object of FileTransferable defined in FileTransferable.java */
        Transferable t = supp.getTransferable();
        
        try {
            /* fetch the data from the Transferable */
            Object data = t.getTransferData(DataFlavor.javaFileListFlavor);
 
            /* data of type javaFileListFlavor is a list of files */
            java.util.List<File> fileList = (java.util.List<File>)data;
 
            /* loop through the files in the file list */
            /* for each file call addToList in the gui */
            for(int i = 0; i < fileList.size(); i++)
            {
            	File file = fileList.get(i);
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