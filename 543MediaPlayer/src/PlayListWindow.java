import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.filechooser.FileNameExtensionFilter;


public class PlayListWindow extends JFrame implements MouseListener 
{
	int windows = 0;
	int playerNum = 0;
	PlayerPanel player;
	JFrame newWindow;
	TablePanel table;
	PlaylistPanel plPanel;
	private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem static_Open;
    private JMenuItem static_Delete;
    private JMenuItem addSong;
    //private JMenuItem exit;
    private JMenuItem close;
    
    public PlayListWindow(String str)
    {
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
        newWindow.setJMenuBar(menuBar);
		
        player = new PlayerPanel();
        //player.setMainController(this);
        //currentPlayers[playerNum] = player;
        playerNum = playerNum+1;
        //setActivePlayer(player);
        if(windows > 2)table.setCurrentTableView("Library");
		table = new TablePanel(str);
		player.setTablePTR(table);
		table.setMediaPlayer(player.getMediaPlayer());
		//table.setCurrentTableView(str);
		player.setTablePTR(table);
		
		newWindow.setSize(900, 800);
		//player.setLocation(160,0);
		//table.setLocation(160,200);
		//plPanel.setLocation(0, 0);
		newWindow.add(player,BorderLayout.CENTER);
		newWindow.add(table,BorderLayout.SOUTH);
		//newWindow.add(table);
		//newWindow.add(plPanel);
		newWindow.setVisible(true);
    }
    public PlayerPanel getPlayerPanel()
    {
    	return player;
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
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
