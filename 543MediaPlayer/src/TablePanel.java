import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;


public class TablePanel extends JPanel implements MouseListener, DatabaseListener
{
	//static int counter = 0;
	JTable table;
	DefaultTableModel model;
	JScrollPane scroller;
	MyTunesDB database;
	int currentIndex,maxIndex;
	FileTransferHandler MyTransferHandler;
	MediaPlayer mplayer;
	String currentTableView;
	JPopupMenu popup;
	JMenuItem popOpen;
	JMenuItem popDelete;
	JMenu addToPlaylist;
	boolean active = false;
	String orderby = "Title";
	boolean order = true;
	int lastClickedColumn;
	
	public TablePanel(String str)
	{
		currentTableView = str;
		this.setSize(900,600);
		this.setBorder(BorderFactory.createTitledBorder(currentTableView));
		currentIndex = 0;
	
		MyTransferHandler = new FileTransferHandler(this);
		//mplayer=MediaPlayer.getMediaPlayerObj();
		
		database = MyTunesDB.getDataBaseObject();
		database.addDataBaseListener(this);
		
		model = new DefaultTableModel(database.getDisplaySet("Library",orderby,order), database.getColumnNames()) 
    	{
    		@Override
    	    public boolean isCellEditable(int row, int column) 
    	    {
    	       return false;
    	    }
    	};
    	
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
        			  //comp.setBackground(Color.RED);
        			  //comp.setForeground(Color.WHITE);
        		  }
        		  else 
        		  {
        			  comp.setBackground(Color.white);
        			  comp.setForeground(Color.black);
        		  }
        		  return comp;
        	  }
        	  
        	  
        };
        table.getTableHeader().addMouseListener(new MouseAdapter() {
  	      @Override
  	      public void mouseClicked(MouseEvent mouseEvent) {
  	        int index = table.convertColumnIndexToModel(table.columnAtPoint(mouseEvent.getPoint()));
  	        if (index >= 0) {
  	          System.out.println("Clicked on column " + index);
  	          setorderBy(index);
  	        }
  	      };
  	    });
        table.setSelectionBackground(Color.RED);
        table.setSelectionForeground(Color.WHITE);
        table.setName(str);
        database.setUI(this);
        table.addMouseListener(this);
        table.setDropMode(DropMode.USE_SELECTION);
        table.setDragEnabled(true);
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
        updateUI(currentTableView);
        this.add(scroller);	
        
	}
	public JTable getTableObj()
	{
		return table;
	}
	public void setMediaPlayer(MediaPlayer mp)
    {
    	mplayer = mp;
    }
	
	public void scrollToSelected()
	{
		Rectangle cellRect = table.getCellRect(table.getSelectedRow(), 0, false);
		if (cellRect != null) 
		{
			table.scrollRectToVisible(cellRect);
		}
	}
	public void setActive()
	{
		table.setSelectionBackground(Color.GREEN);
		table.setSelectionForeground(Color.BLACK);
	}
	public void setInactive()
	{
		table.setSelectionBackground(Color.RED);
		table.setSelectionForeground(Color.WHITE);
	}
	public boolean addToList(String fileName)
	{
		boolean success = false;
    	if(fileName.endsWith("mp3"))
    	{
    		//success = database.insertEntry("Library", fileName);
    		if(!currentTableView.equals("Library"))database.insertIntoPlaylist(fileName, currentTableView);
    		else
    			database.insertEntry("library", fileName);
    		
    		currentIndex = database.getIndexOf(fileName);
    		maxIndex = table.getRowCount();
    	}
    	
    	//updateUI(currentTableView); // handled by 
    	return success;
	}
	
	public void deleteRows(int[] rows)
    {
    	for(int i = rows.length-1; i >= 0; i--)
    	{
    		String fileName = (String)((String)model.getValueAt(rows[i], model.getColumnCount()-1));
    		System.out.println("deleting: " + fileName);
    		database.deleteEntry(currentTableView, fileName);
    	}
    	
    	maxIndex = table.getRowCount();
    	//updateUI(currentTableView);
    }
	
	public int getIndexOf(String str)
    {
    	for(int i = 0; i < table.getRowCount(); i++)
    	{
    		if(str.equals((String)model.getValueAt(i, model.getColumnCount()-1)))
    		{
    			return i;
    		}
    	}
    	return -1;
    }
	
	public String getCurrentSong()
	{
		if(table.getSelectedRow() == -1)
		{
			table.setRowSelectionInterval(0, 0);
			currentIndex = 0;
		}
		else 
		{
			currentIndex = table.getSelectedRow();
			
		}
		return (String)model.getValueAt(currentIndex, model.getColumnCount()-1);
		
	}
	
	public String getNextSong()
	{
		currentIndex = (currentIndex+1)%table.getRowCount();
		table.setRowSelectionInterval(currentIndex, currentIndex);
		scrollToSelected();
		return (String)model.getValueAt(currentIndex, model.getColumnCount()-1);
		
	}
	
	public String getPreviousSong()
	{
		maxIndex = table.getRowCount();
		System.out.println("maxIndex = " + maxIndex + " and currentIndex == " + currentIndex);
	    currentIndex = (currentIndex == 0) ? maxIndex-1:currentIndex-1;
		table.setRowSelectionInterval(currentIndex, currentIndex);
		scrollToSelected();
		return (String)model.getValueAt(currentIndex, model.getColumnCount()-1);
	}
	
	public void setCurrentTableView(String str)
	{
		currentTableView = str;
		this.setBorder(BorderFactory.createTitledBorder(currentTableView));
		updateUI(currentTableView);
		int tmp = -1;
		if(mplayer!=null && mplayer.getCurrentSong() != null)tmp = getIndexOf(mplayer.getCurrentSong());
		if(tmp != -1)
		{
			currentIndex = tmp;
			table.setRowSelectionInterval(currentIndex, currentIndex);
			
		}
		else 
			currentIndex = 0;
		
		
	}
	/* when the user clicks on a column, this function is called */
	/* the pass parameter is the column number that was clicked on */
	public void setorderBy(int column)
	{
		
		
		if(lastClickedColumn == column)
		{
			order = !order;
		}
		lastClickedColumn = column;
		switch(column)
		{
			case 0:
				orderby = "Title";
				break;
			case 1:
				orderby = "Artist";
				break;
			case 2:
				orderby = "Album";
				break;
			case 3:
				orderby = "Genre";
				break;
			case 4:
				orderby = "Duration";
				break;
			case 5:
				orderby = "Release";
				break;
			case 6:
				orderby = "Comment";
				break;
			default:
				orderby = "Title";
		}
		updateUI(currentTableView);
	}
	
	public String getCurrentTableView()
	{
		return currentTableView;
	}
	
	
	
	private void buildPopUp(MouseEvent arg0)
	{
		Object[] playlistNames;
		JMenuItem submenuItem;
		popup = new JPopupMenu();
        popOpen = new JMenuItem("Open");
        popOpen.addMouseListener(this);
        popDelete = new JMenuItem("Delete");
        popDelete.addMouseListener(this);
        popup.add(popOpen);
        popup.add(new JSeparator());
        popup.add(popDelete);
        popup.add(new JSeparator());
        addToPlaylist = new JMenu("Add Playlist");
        playlistNames = database.getPlaylistNames();
        for(int i = 0; i < playlistNames.length; i++)
        {
        	//addToPlaylist.add(new JMenuItem((String)playlistNames[i]));
        	submenuItem = new JMenuItem((String)playlistNames[i]);
        	submenuItem.addMouseListener(this);
        	addToPlaylist.add(submenuItem);
        	if(i+1 < playlistNames.length)addToPlaylist.add(new JSeparator());
        }
        popup.add(addToPlaylist);
        popup.show(arg0.getComponent(),arg0.getX(),arg0.getY());
        
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
					
				//mplayer.pauseOthers();
				currentIndex = table.getSelectedRow();
				mplayer.play((String)model.getValueAt(currentIndex, model.getColumnCount()-1));
				currentIndex = table.getSelectedRow();
				arg0.consume();
			}
		}
		
	}
	
	
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.isMetaDown())
		{
			//popup.show(arg0.getComponent(),arg0.getX(),arg0.getY());
			buildPopUp(arg0);
		}
		
		else if(arg0.getSource() == popDelete)
		{
			deleteRows(table.getSelectedRows());
		}
		
		else if(arg0.getSource() == popOpen)
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
		    		currentIndex = getIndexOf(chosenFile);
		    		if(currentIndex != -1);
		    		JOptionPane.showMessageDialog(null,"CurenntIndex == " + currentIndex);
		    		table.setRowSelectionInterval(currentIndex, currentIndex);
					mplayer.play(chosenFile);
					//currentSong=chosenFile;
		    	}
		    }
		}
		else
		{
			if(arg0.getSource() instanceof JMenuItem)
			{
				JMenuItem selected = (JMenuItem)arg0.getSource();
				System.out.println("You clicked on:" + selected.getText());
				//System.out.println("table.getSelectedRow() == " + table.getSelectedRow());
				if(table.getSelectedRow() != -1)
				{
					String playlistName = selected.getText();
					String fileName = (String) model.getValueAt(table.getSelectedRow(), model.getColumnCount()-1);
					database.insertIntoPlaylist(fileName, playlistName);
				}
			}
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}
	
	@Override
	public void paintComponent(Graphics g) 
    {
		ImageIcon ic = new ImageIcon(getClass().getResource("background.png"));
      g.drawImage(ic.getImage(), 0, 0, getWidth(), getHeight(), this);
    }
	
	public void updateUI(String tableName)
    {
    	
    	if (model != null)
    	{
    		table.setName(tableName);
    		System.out.println("------- TablePanel UpDate Initiated ---------");
    		//System.out.println(this.getClass().toString()+"::updateUI(String): arg0 = " + tableName);
    		//JTable tm = tableUI.getTableObj();
    		model.setDataVector(database.getDisplaySet(tableName,orderby,order), database.getColumnNames());
    		table.removeColumn(table.getColumnModel().getColumn(table.getColumnModel().getColumnIndex("FileName")));
    		table.removeColumn(table.getColumnModel().getColumn(table.getColumnModel().getColumnIndex("Duration")));
    		table.getColumnModel().getColumn(table.getColumnModel().getColumnIndex("Year")).setMaxWidth(45);
    		table.getColumnModel().getColumn(table.getColumnModel().getColumnIndex("Time")).setMaxWidth(40);
    		table.getColumnModel().getColumn(table.getColumnModel().getColumnIndex("Genre")).setMaxWidth(60);
    		
    		//tm.moveColumn(column, targetColumn); // actually, just fix the db
    	}
    	maxIndex = table.getRowCount();
    	int tmp = -1;
    	/* both inserts and deletes can change where the current song is in list */
    	
    	/* if media player is not null && if media player has a song opened, then find it in table */
    	if(mplayer!=null && mplayer.getCurrentSong() != null)
    	{
    		tmp = getIndexOf(mplayer.getCurrentSong());
    	    if(tmp != -1)currentIndex = tmp;	
    	}	
    	/*[ tmp != -1 ] indicates that song was found in list, set the currentIndex */
    	//currentIndex = (tmp == -1) ? 0:tmp;
    	System.out.println(this.getClass().toString()+":: updateUI("+tableName+"):: currentIndex = " + currentIndex + ", maxIndex = "+ maxIndex);
    	/* highlight the currentIndex */
    	table.setRowSelectionInterval(currentIndex, currentIndex);
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
	@Override
	public void dataBaseDataChanged(DataBaseEvent arg0) {
		
		if(arg0.getOperation().equals("delete"))
		{
			String name = arg0.getFileName();
			String name1 = mplayer.getCurrentSong();
			System.out.println("databasechanged:: name = " + name + " and name1 = " + name1);
			System.out.println("currentTableview == " + currentTableView);
			if(name1 != null){
				if(name1.equals(name))
				{
					System.out.println("currentTableview == " + currentTableView);
					mplayer.stop();
					mplayer.reset();
				}
			}
		}
		
		updateUI(currentTableView);
		
	}
	@Override
	public void playlistDeleted(PlaylistEvent arg0) {
		// TODO Auto-generated method stub
		
	}


}
