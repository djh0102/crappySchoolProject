import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;


public class BaseWindow extends JFrame implements MouseListener,ActionListener, DatabaseListener
{
	
	//PlayerPanel[] currentPlayers = new PlayerPanel[7];// limit 7 open players at one time
	ArrayList<PlayerPanel> currentPlayers; 
	int windows = 0;
	int playerNum = 0;
	PlayerPanel player;
	JFrame newWindow;
	TablePanel table;
	myTunes controller;
	PlaylistPanel plPanel;
	private JMenuBar menuBar;
    private JMenu menu;
    private JMenu controls;
    private JMenuItem static_Open;
    private JMenuItem static_Delete;
    private JMenuItem addSong;
    private JMenuItem exit;
    private JMenuItem addPlayList;
    private JMenuItem play;
    private JMenuItem skip_next;
    private JMenuItem skip_prev;
    private JMenuItem gotoSong;
    private JMenu recent;
    private JCheckBoxMenuItem shuffle;
    private JCheckBoxMenuItem repeat;
    private JMenuItem volumeUP;
    private JMenuItem volumeDOWN;
    //private JMenuItem close;
	
    public BaseWindow(myTunes control)
	{
    	controller = control;
		this.setResizable(false);
		this.setLayout(null);
		//this.setFocusTraversalKeysEnabled(false);
		this.setTitle("myTunes by Mike and Daniel");
		//this.setFocusTraversalKeysEnabled(false);
		menuBar = new JMenuBar();
		/* 'File' option on the menu bar */
        menu = new JMenu("File");
        static_Open = new JMenuItem("Open");
        static_Open.addMouseListener(this);
        static_Delete =  new JMenuItem("Delete");
        static_Delete.addMouseListener(this);
        addSong =  new JMenuItem("Add");
        addSong.addMouseListener(this);
        exit = new JMenuItem("Exit");
        exit.addMouseListener(this);
        
        
        //KeyStroke volumeUp = KeyStroke.getKeyStroke('=');
        //exit.setAccelerator(volumeUp);
        //exit.addActionListener(this);
        //exit.setAccelerator(KeyStroke.getKeyStroke("Q"));
        menu.add(static_Open);
        menu.add(new JSeparator());
        menu.add(addSong);
        menu.add(new JSeparator());
        menu.add(static_Delete);
        menu.add(new JSeparator());
        menu.add(exit);
        menuBar.add(menu);
        
        /* 'PlayList' option on the menu bar */
        menu = new JMenu("Playlist");
        addPlayList = new JMenuItem("New PlayList");
        addPlayList.addMouseListener(this);
        menu.add(addPlayList);
        menuBar.add(menu);
        
        controls = new JMenu("Controls");
        controls.addMouseListener(this);
        play = new JMenuItem("Play");
        play.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,0));
        play.addActionListener(this);
        skip_next = new JMenuItem("Next");
        //skip_next.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,ActionEvent.CTRL_MASK));
        skip_next.setAccelerator(KeyStroke.getKeyStroke(']',ActionEvent.CTRL_MASK));
        skip_next.addActionListener(this);
        skip_prev = new JMenuItem("Previous");
        skip_prev.setAccelerator(KeyStroke.getKeyStroke('[',ActionEvent.CTRL_MASK));
        skip_prev.addActionListener(this);
        recent = new JMenu("Play Recent");
        gotoSong = new JMenuItem("Go to Current Song");
        gotoSong.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,ActionEvent.CTRL_MASK));
        gotoSong.addActionListener(this);
        volumeUP = new JMenuItem("Increase Volume");
        volumeUP.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,ActionEvent.CTRL_MASK));
        volumeUP.addActionListener(this);
        volumeDOWN = new JMenuItem("Decrease Volume");
        volumeDOWN.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,ActionEvent.CTRL_MASK));
        volumeDOWN.addActionListener(this);
        shuffle = new JCheckBoxMenuItem("Shuffle");
        shuffle.addActionListener(this);
        repeat = new JCheckBoxMenuItem("Repeat");
        repeat.addActionListener(this);
        
        controls.add(play);
        controls.add(skip_next);
        controls.add(skip_prev);
        controls.add(recent);
        controls.add(gotoSong);
        controls.add(new JSeparator());
        controls.add(volumeUP);
        controls.add(volumeDOWN);
        controls.add(new JSeparator());
        controls.add(shuffle);
        controls.add(repeat);
        menuBar.add(controls);
        
        
        this.setJMenuBar(menuBar);
		
        player = new PlayerPanel();
        player.setActive(true);
       // currentPlayers[playerNum] = player;
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
		MyTunesDB.getDataBaseObject().addDataBaseListener(this);
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
    
    public void importPlayerPanel(PlayListWindow plw)
    {
    	//System.out.println("my old id = " + player.getMediaPlayer().idNum);
    	
    	/* set this.table's state equal to tt.table's state */
    	
    	TablePanel tt = plw.getTablePanel();
    	//if(table.getCurrentTableView().equals(tt.getCurrentTableView()))return;
    	table.setMediaPlayer(tt.getMediaPlayer());
    	if(tt.isActive())table.setActive();
    	if(tt.getShuffle()) // if tt is shuffling transfer the shuffle information to this.table
    	{
    		/* tablepanel builds a new shuffle list when setShuffle(true) */
    		/* so we need to load tt's shuffle information to table */
    		table.importShuffleState(tt.getShuffleList(), tt.getShuffleIndex());
    	}
    	
    	PlayerPanel pp = plw.getPlayerPanel();
    	//System.out.println("my new id = " + pp.getMediaPlayer().idNum);
    	pp.setSize(pp.getWidth(), pp.getHeight()+10);
    	pp.setTablePTR(table);
    	controller.removePlayer(player);
    	this.remove(player);
    	this.validate();
    	pp.setLocation(160,0);
    	pp.grabFocus();
    	pp.revalidate();
    	player = pp;
    	this.add(pp);
    	this.paintComponents(this.getGraphics());
    	menuBar.grabFocus();
    	plw.dispose();
    
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
			controller.mainWindowClosing();
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
		
		else if(arg0.getSource() == static_Delete)
		{
			//table.deleteRows(table.getTableObj().getSelectedRows());
			//table.deleteRows(new int[] {-1});
			table.deleteRows();
		}
		
		else if (arg0.getSource() == addPlayList)
		{
			plPanel.addPlayList(null); /* arg is null, addPlayList will ask user for name */
		}
		
		else if(arg0.getSource() == controls)
		{
			shuffle.setSelected(player.shuffle.isSelected());
			repeat.setSelected(player.repeat.isSelected());
			recent.removeAll();
			ArrayList<String> songs = controller.getSongHistory();
			for(int i = 0; i < songs.size(); i++)
			{
				final JMenuItem jm = new JMenuItem(songs.get(i));
				jm.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent arg0) 
					{
						//System.out.println(jm.getText());
						player.play(jm.getText());
					}
					
				});
				recent.add(jm);
			}
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dataBaseDataChanged(DataBaseEvent arg0) 
	{
		System.out.println("BaseWindow::database Event dected!");
		table.updateUI(table.getCurrentTableView());
		
	}

	@Override
	public void playlistEvent(PlaylistEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		//System.out.println(this.getClass().toString() + "::ActionPerformed(arg0)::arg0 == " + arg0.getSource().toString());
		if(arg0.getSource() == volumeUP)
		{
			player.volumeUp();
			//System.exit(0);
		}
		else if(arg0.getSource() == volumeDOWN)
		{
			player.volumeDown();
			//System.exit(0);
		}
		else if(arg0.getSource() == skip_next)
		{
			//System.out.println("made it here!!!!!!!!!!!!!!!!!!!!!!");
			player.playNextSong();
		}
		else if (arg0.getSource() == play)
		{
			player.playFirstSong();
		}
		else if (arg0.getSource() == gotoSong)
		{
			table.highlightCurrentSong();
		}
		else if (arg0.getSource() == shuffle)
		{
			player.syncShuffle(shuffle.isSelected());
		}
		else if (arg0.getSource() == repeat)
		{
			player.syncRepeat(repeat.isSelected());
		}
		
	}

}
