import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.filechooser.FileNameExtensionFilter;


public class BaseWindow extends JFrame  implements MouseListener
{
	
	PlayerPanel[] currentPlayers = new PlayerPanel[7];// limit 7 open players at one time
	int windows = 0;
	int playerNum = 0;
	PlayerPanel player;
	JFrame newWindow;
	TablePanel table;
	myTunes controller;
	PlaylistPanel plPanel;
	private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem static_Open;
    private JMenuItem static_Delete;
    private JMenuItem addSong;
    private JMenuItem exit;
    //private JMenuItem close;
	
    public BaseWindow(myTunes control)
	{
    	controller = control;
		this.setResizable(false);
		this.setLayout(null);
		this.setTitle("myTunes by Mike and Daniel");
		menuBar = new JMenuBar();
        menu = new JMenu("File");
        static_Open = new JMenuItem("Open");
        static_Open.addMouseListener(this);
        static_Delete =  new JMenuItem("Delete");
        static_Delete.addMouseListener(this);
        addSong =  new JMenuItem("Add");
        addSong.addMouseListener(this);
        exit = new JMenuItem("Exit");
        exit.addMouseListener(this);
        
        menu.add(static_Open);
        menu.add(new JSeparator());
        menu.add(addSong);
        menu.add(new JSeparator());
        menu.add(static_Delete);
        menu.add(new JSeparator());
        menu.add(exit);
        menuBar.add(menu);
        this.setJMenuBar(menuBar);
		
        player = new PlayerPanel();
        player.setActive(true);
        currentPlayers[playerNum] = player;
        playerNum = playerNum+1;
		table = new TablePanel("Library");
		player.setTablePTR(table);
		table.setMediaPlayer(player.getMediaPlayer());
		
		plPanel= new PlaylistPanel();
		plPanel.setTablePanel(table);
		plPanel.setMainController(control);
		this.setSize(1060, 800);
		player.setLocation(160,0);
		table.setLocation(160,200);
		plPanel.setLocation(0, 0);
		windows = windows + 1;
		this.add(player);
		this.add(table);
		this.add(plPanel);
		this.setVisible(true);
		//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(new WindowListener() {
	        
			public void windowClosing(WindowEvent e) 
	        {
				controller.mainWindowClosing();
	        }

			@Override
			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowOpened(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

	    });
	}

    public PlayerPanel getPlayerPanel()
    {
    	return player;
    }
    public TablePanel getTablePanel()
    {
    	return table;
    }
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
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
		if(arg0.getSource() == exit)
		{
			
			System.exit(0);
		}
		
		if(arg0.getSource() == static_Open || arg0.getSource() == addSong)
		{
			JFileChooser chooser = new JFileChooser();
		    FileNameExtensionFilter filter = new FileNameExtensionFilter("MP3 Audio Files", "mp3");
		    chooser.setFileFilter(filter);
		    chooser.grabFocus();
		    int returnVal = chooser.showOpenDialog(menu);
		    
		    if(returnVal == JFileChooser.APPROVE_OPTION)
		    {
		    	String chosenFile = chooser.getSelectedFile().getAbsolutePath();
		    	if(arg0.getSource() == static_Open)player.play(chosenFile);
		    	else
		    		table.addToList(chosenFile);
		    }
		}
		if(arg0.getSource() == static_Delete)
		{
			table.deleteRows(table.getTableObj().getSelectedRows());
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
