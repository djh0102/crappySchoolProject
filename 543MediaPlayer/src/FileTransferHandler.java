import java.awt.Component;
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
	String to_name = "";
	String from_name = "";
	DataFlavor myCustomFlavor = new DataFlavor(String[].class, "String Array");
	
	/* DataFlavor of an external drop */
	String external_OSX = "java.awt.datatransfer.DataFlavor[mimetype=application/x-java-url;representationclass=java.net.URL]";
	String external_WIN = "java.awt.datatransfer.DataFlavor[mimetype=application/x-java-file-list;representationclass=java.util.List]";
	
	public FileTransferHandler(TablePanel tp)
	{
		gui = tp;
	}
	
	public int getSourceActions(JComponent c) 
	{
	    return COPY;
	}

	protected Transferable createTransferable(JComponent c) 
	{
	    
		JTable table = (JTable) c;
		
		int[] rows = table.getSelectedRows();
		from_name = table.getName();
		System.out.println(":::: Drag gesture initiated from PlayList::::" + from_name + "\nAttempting to drop files:");
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		filenames = new ArrayList();
			
		for(int i = 0; i < rows.length; i++)
		{
			System.out.println((String)model.getValueAt(rows[i],model.getColumnCount()-1));
			String filename = (String)model.getValueAt(rows[i],model.getColumnCount()-1);
			filenames.add(new File(filename));
			//selectedData[i] = (String)model.getValueAt(rows[i-1],model.getColumnCount()-1);
		}
		Transferable t = new FileTransferable(filenames,c.getName());
		return t;
	}

	protected void exportDone(JComponent c, Transferable t, int action) 
	{
		/* no clean up required */ 
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
    	/* do not continue if the drop is not of a supported flavor */
    	System.out.println("FileTransferHandler::importData()\n type:: "+ supp.getDataFlavors()[0]);
        if (!canImport(supp)) 
        {
        	System.out.println("Drop type ::" + supp.getDataFlavors()[0] + " not supported");
            return false;
        }
        
        Component c =  supp.getComponent();
        
        /* get the name of the destination */
        to_name = c.getName();
        
        /* fetch the Transferable  */
        Transferable t = supp.getTransferable();
        
        /* get the flavor of the drop */
        DataFlavor[] d = supp.getDataFlavors();
        
        try {
            /* fetch the data from the Transferable */
            filenames = (List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);
           
            /* if NOT an EXTERNAL Drop */
            if(!external_OSX.equals(d[0].toString()) && !external_WIN.equals(d[0].toString()))
            {
            	
            	//Object obj = (Object)t.getTransferData(DataFlavor.stringFlavor);
            	from_name = (String) t.getTransferData(DataFlavor.stringFlavor);
            }
            else
            	from_name = "----external source-----";
            
       
            //System.out.println("made it to line 122");
            if(to_name.equals(from_name))
    		{
    			System.err.println("ERROR::PlayList::"+to_name +":: trying to drop on PlayList::" + from_name + ":::import aborted!!!" );
    			//from_name = "";
    			return false;
    		}
            else
            {
            	System.out.println("Drag from Playlist::" + from_name + " to PlayList::" + to_name + " accepted!!");
            }
    		for(int i = 0; i < filenames.size(); i++)
            {
    			System.out.println(filenames.get(i).getAbsolutePath());
            	gui.addToList(filenames.get(i).getAbsolutePath());
            }
    	    
        } catch (UnsupportedFlavorException e) {
        	System.out.println("FileTransferHandler::importData():: Unsupported Data Flavor!");
        	e.printStackTrace();
            return false;
        } catch (IOException e) {
            return false;
        }
        
        return true;
    }
    
}