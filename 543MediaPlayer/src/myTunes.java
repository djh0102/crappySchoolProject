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


class myTunes extends JFrame implements MouseListener
{
	PlayerPanel player;
	TablePanel table;
	private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem static_Open;
    private JMenuItem static_Delete;
    private JMenuItem addSong;
    private JMenuItem exit;
	public myTunes() 
	{
		this.setSize(900,800);
		this.setResizable(false);
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
		table = new TablePanel();
		player.setTablePTR(table);
		table.hookUP(MediaPlayer.getMediaPlayerObj());
		
		this.add(player, BorderLayout.CENTER);
		this.add(table,BorderLayout.SOUTH);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new myTunes();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == exit)
		{
			System.exit(0);
		}
		if(e.getSource() == static_Open || e.getSource() == addSong)
		{
			JFileChooser chooser = new JFileChooser();
		    FileNameExtensionFilter filter = new FileNameExtensionFilter("MP3 Audio Files", "mp3");
		    chooser.setFileFilter(filter);
		    chooser.grabFocus();
		    int returnVal = chooser.showOpenDialog(menu);
		    
		    if(returnVal == JFileChooser.APPROVE_OPTION)
		    {
		    	String chosenFile = chooser.getSelectedFile().getAbsolutePath();
		    	if(e.getSource() == static_Open)MediaPlayer.getMediaPlayerObj().play(chosenFile);
		    	else
		    		table.addToList(chosenFile);
		    }
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
