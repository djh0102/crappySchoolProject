import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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


public class PlayListWindow extends JFrame implements MouseListener, DatabaseListener, ActionListener
{
	//int windows = 0;
	//int playerNum = 0;
	PlayerPanel player;
	boolean dead = false;
	JFrame newWindow;
	TablePanel table;
	PlaylistPanel plPanel;
	myTunes controller;
	private JMenuBar menuBar;
    private JMenu menu;
    private JMenu controls;
    private JMenuItem static_Open;
    private JMenuItem static_Delete;
    private JMenuItem addSong;
    //private JMenuItem exit;
    private JMenuItem play;
    private JMenuItem skip_next;
    private JMenuItem skip_prev;
    private JMenuItem gotoSong;
    private JMenu recent;
    private JCheckBoxMenuItem shuffle;
    private JCheckBoxMenuItem repeat;
    private JMenuItem volumeUP;
    private JMenuItem volumeDOWN;
    private JMenuItem close;
    
    public PlayListWindow(myTunes mytun,String str)
    {
    	controller = mytun;
    	newWindow = new JFrame(str);
		newWindow.setResizable(false);
		//newWindow.setLayout(null);
		menuBar = new JMenuBar();
        menu = new JMenu("File");
        static_Open = new JMenuItem("Open");
        static_Open.addMouseListener(this);
        static_Delete =  new JMenuItem("Delete");
        static_Delete.addMouseListener(this);
        addSong =  new JMenuItem("Add");
        addSong.addMouseListener(this);
        close = new JMenuItem("Close Window");
        close.addMouseListener(this);
        
        menu.add(static_Open);
        menu.add(new JSeparator());
        menu.add(addSong);
        menu.add(new JSeparator());
        menu.add(static_Delete);
        menu.add(new JSeparator());
        menu.add(close);
        menuBar.add(menu);
        controls = new JMenu("Controls");
        controls.addMouseListener(this);
        play = new JMenuItem("Play");
        play.setAccelerator(KeyStroke.getKeyStroke(' '));
        play.addActionListener(this);
        skip_next = new JMenuItem("Next");
        skip_next.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,ActionEvent.CTRL_MASK));
        skip_next.addActionListener(this);
        skip_prev = new JMenuItem("Previous");
        skip_prev.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,ActionEvent.CTRL_MASK));
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
        
       // volumeUP = new JMenuItem("Volume Up");
        //volumeDOWN = new JMenuItem("Volume Down");
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
        newWindow.setJMenuBar(menuBar);
		
        player = new PlayerPanel();
        //playerNum = playerNum+1;
        //if(windows > 2)table.setCurrentTableView("Library");
		table = new TablePanel(str);
		MyTunesDB.getDataBaseObject().addDataBaseListener(this);
		player.setTablePTR(table);
		table.setMediaPlayer(player.getMediaPlayer());
		player.setTablePTR(table);
		
		newWindow.setSize(900, 800);
		newWindow.add(player,BorderLayout.CENTER);
		newWindow.add(table,BorderLayout.SOUTH);
		newWindow.setVisible(true);
		
		newWindow.addWindowListener(new WindowListener() {
	        
			public void windowClosing(WindowEvent e) 
	        {
	            //player.Stop();
				dead = true;
				System.out.print("PlayListWindow::WindowClosing()->");
	            controller.removePlayer(player);
	            newWindow = null;
	            dispose();
	            deRegister();
	        }

			@Override
			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				// TODO Auto-generated method stub
				//player.Stop();
				System.out.print("PlayListWindow::WindowClosed()->");
	            //controller.removePlayer(player);
	            //MyTunesDB.getDataBaseObject().removeDataBaseListener(this);
				deRegister();
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
    public void deRegister()
    {
    	MyTunesDB.getDataBaseObject().removeDataBaseListener(this);
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
		
		if(arg0.getSource() == close)
		{
			player.Stop();
			controller.removePlayer(player);
			dead=true;
			newWindow.dispose();
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
	public void dataBaseDataChanged(DataBaseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playlistEvent(PlaylistEvent arg0) {
		// TODO Auto-generated method stub
		if(dead)newWindow=null;
		System.out.println("PlaylistWindow::playlistEvent() == " + arg0.getPlaylistName() + " event type:: " + arg0.getType() + " dead = " +dead);
		if(table.getCurrentTableView().equals(arg0.getPlaylistName()) && arg0.getType().equals("move") && !dead)
		{
			System.out.println("Oh, that's me, moving to baseWindow!!!");
			//player.shutUP();
			//dead = true;
			MyTunesDB.getDataBaseObject().removeDataBaseListener(this);
			controller.moveToBaseWindow(this);
			//this.dispose();
			newWindow.dispose();
			newWindow = null;
			this.dispose();
			
			//newWindow = null;
			//this.dispose();
		}
		else if(table.getCurrentTableView().equals(arg0.getPlaylistName()) && arg0.getType().equals("delete"))
		{
			System.out.println("Oh, that's me, goodbye!!!");
			player.Stop();
			controller.removePlayer(player);
			newWindow.dispose();
		}
		
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
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
