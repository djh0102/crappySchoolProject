import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;


public class TablePanel extends JPanel implements MouseListener 
{
	JTable table;
	DefaultTableModel model;
	JScrollPane scroller;
	MyTunesDB database;
	int currentIndex,maxIndex;
	DropHandler MyTransferHandler;
	MediaPlayer mplayer;
	String currentSong;
	String currentTableView;
	PlayerPanel playerUI;
	JPopupMenu popup;
	JMenuItem popOpen;
	JMenuItem popDelete;
	
	public TablePanel()
	{
		currentTableView = "Library";
		this.setSize(800,600);
		this.setBorder(BorderFactory.createTitledBorder(currentTableView));
		currentIndex = 0;
	
		MyTransferHandler = new DropHandler(this);
		mplayer=MediaPlayer.getMediaPlayerObj();
		database = MyTunesDB.getDataBaseObject();
		model = new DefaultTableModel(database.getDisplaySet("library"), database.getColumnNames()) 
    	{
    		@Override
    	    public boolean isCellEditable(int row, int column) 
    	    {
    	       return false;
    	    }
    	};
    	database.setUI(model);
    	table = new JTable(model)
        {
        	  public Component prepareRenderer(TableCellRenderer renderer,int Index_row, int Index_col) 
        	  {
        		  Component comp = super.prepareRenderer(renderer, Index_row, Index_col);
        		  //even index, selected or not selected
        		  if (Index_row % 2 == 0 && !isCellSelected(Index_row, Index_col)) 
        		  {
        			  comp.setBackground(Color.black);
        			  comp.setForeground(Color.white);
        		  } 
        		  else if (isCellSelected(Index_row, Index_col))  
        		  {
        			  comp.setBackground(Color.RED);
        			  comp.setForeground(Color.WHITE);
        		  }
        		  else 
        		  {
        			  comp.setBackground(Color.white);
        			  comp.setForeground(Color.black);
        		  }
        		  return comp;
        	  	}
        	  
        };
        
        table.addMouseListener(this);
        table.setDropMode(DropMode.USE_SELECTION);
        table.setTransferHandler(MyTransferHandler);
        table.setModel(model);
        maxIndex=table.getRowCount();
        Dimension d = new Dimension(850,500);
        table.setPreferredScrollableViewportSize(d);
        scroller = new javax.swing.JScrollPane(table);
        scroller.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.darkGray, java.awt.Color.black, java.awt.Color.black, java.awt.Color.lightGray));
        scroller.setAutoscrolls(true);
        
        popup = new JPopupMenu();
        popOpen = new JMenuItem("Open");
        popOpen.addMouseListener(this);
        popDelete = new JMenuItem("Delete");
        popDelete.addMouseListener(this);
        popup.add(popOpen);
        popup.add(new JSeparator());
        popup.add(popDelete);
        scroller.setViewportView(table);
        table.setFillsViewportHeight(true);
        
        this.add(scroller);
		
	}
	public JTable getTableObj()
	{
		return table;
	}
	public void scrollToSelected()
	{
		Rectangle cellRect = table.getCellRect(table.getSelectedRow(), 0, false);
		if (cellRect != null) {
		table.scrollRectToVisible(cellRect);
		}
	}
	public boolean addToList(String fileName)
	{
		boolean success = false;
    	if(fileName.endsWith("mp3"))
    	{
    		success = database.insertEntry("library", fileName);
    		currentIndex = database.getIndexOf(fileName);
    		maxIndex = table.getRowCount();
    		playerUI.updateTableCount();
    	}
    	return success;
	}
	public void deleteRows(int[] rows)
    {
    	for(int i = rows.length; i > 0; i--)
    	{
    		String fileName = (String)(model.getValueAt(rows[i-1],model.getColumnCount()-1));
    		System.out.println("deleting: " + fileName);
    		database.deleteEntry("library", fileName);
    		maxIndex--;
    		
    	}
    	playerUI.updateTableCount();
    }
	public void setPlayerUI(PlayerPanel ui)
	{
		playerUI = ui;
	}
	
	public int getIndexOf(String str)
    {
    	for(int i = 0; i < table.getRowCount(); i++)
    	{
    		if(str.equals((String)table.getValueAt(i, 5)))
    		{
    			return i;
    		}
    	}
    	return -1;
    }
	
	/*public String getNextSong()
	{
		currentIndex = (currentIndex+1)%table.getRowCount();
		table.setRowSelectionInterval(currentIndex, currentIndex);
		return (String)table.getValueAt(currentIndex, 5);
		
	}*/
	
	public void hookUP(MediaPlayer mm)
	{
		mplayer = mm;
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getClickCount() == 2 && !arg0.isConsumed())
		{
			if(arg0.getSource() == table)
			{
				if(mplayer.isPlaying())
					mplayer.stop();
					
				currentIndex = table.getSelectedRow();
				mplayer.play((String)table.getValueAt(currentIndex, 5));
				currentSong=(String)table.getValueAt(currentIndex, 5);
				playerUI.setCurrentIndex(currentIndex);
			}
		}
		
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.isMetaDown())
		{
			popup.show(arg0.getComponent(),arg0.getX(),arg0.getY());
		}
		if(arg0.getSource() == popDelete)
		{
			deleteRows(table.getSelectedRows());
		}
		if(arg0.getSource() == popOpen)
		{
			
			JFileChooser chooser = new JFileChooser(); 
			chooser.setLocation(arg0.getX(), arg0.getY());
		    FileNameExtensionFilter filter = new FileNameExtensionFilter("MP3 Audio Files", "mp3");
		    chooser.setFileFilter(filter);
		    chooser.grabFocus();
		    int returnVal = chooser.showOpenDialog(popup);
		    
		    if(returnVal == JFileChooser.APPROVE_OPTION) 
		    {
		    	String chosenFile = chooser.getSelectedFile().getAbsolutePath();
		    	if(addToList(chosenFile))
		    	{
		    		mplayer.stop();
		    		table.setRowSelectionInterval(currentIndex, currentIndex);
					mplayer.play(chosenFile);
					currentSong=chosenFile;
					playerUI.setCurrentIndex(currentIndex);
					playerUI.updateTableCount();
		    	}
		    }
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void paintComponent(Graphics g) 
    {
		ImageIcon ic = new ImageIcon(getClass().getResource("background.png"));
      g.drawImage(ic.getImage(), 0, 0, getWidth(), getHeight(), this);
    }
	/*public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
        	
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) 
            {
            	System.out.println(info.getName() + ": " + info.getClassName());
                if ("Nimbus".equals(info.getName())) 
                {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                  
                    //break;
                }
                
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PlayerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PlayerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PlayerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PlayerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
		TablePanel tp = new TablePanel();
		JFrame jf = new JFrame();
		jf.add(tp);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setSize(tp.getSize());
		jf.setVisible(true);
	}*/


}
