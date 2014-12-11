import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.filechooser.FileNameExtensionFilter;


class myTunes extends JFrame 
{
	//PlayerPanel[] currentPlayers = new PlayerPanel[7];// limit 7 open players at one time
	ArrayList<PlayerPanel> currentPlayers = new ArrayList<PlayerPanel>(); 
	//int playerNum = 0;
	ArrayList<String> songHistory = new ArrayList<String>();
	BaseWindow bw;
	public myTunes() 
	{
		bw = new BaseWindow(this);
		songHistory = MyTunesDB.getDataBaseObject().getSongHistory();
		//currentPlayers[playerNum] = bw.getPlayerPanel();
		addPlayer(bw.getPlayerPanel());
		//playerNum = playerNum+1;
		bw.getPlayerPanel().setMainController(this);
	}
	public void openPlayListWindow(String str)
	{
		TablePanel tp = bw.getTablePanel();
		if(tp!=null)tp.setCurrentTableView("Library");
		PlayListWindow plw = new PlayListWindow(this,str);
		//currentPlayers[playerNum] = plw.getPlayerPanel();
		addPlayer(plw.getPlayerPanel());
		//playerNum = playerNum+1;
		plw.getPlayerPanel().setMainController(this);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new myTunes();
	}
	public void reservePlayer(PlayerPanel pp)
	{
		for(int i =0; i < currentPlayers.size(); i++)
		{
			PlayerPanel tmp = currentPlayers.get(i);
			
			if(tmp != pp)
			{
				tmp.shutUP();
			}
		}
	}
	public void moveToBaseWindow(PlayListWindow plw)
	{
		//currentPlayers.remove(bw.getPlayerPanel());
		bw.importPlayerPanel(plw);
		//currentPlayers.remove(bw.getPlayerPanel());
	}
	public void removePlayer(PlayerPanel rm)
	{
		int id = rm.getMediaPlayer().idNum;
		System.out.println("target id = " + id);
		rm.Stop();
		currentPlayers.remove(rm);
		System.out.println("deletePlayer::There are " + currentPlayers.size() + " players in the list");
	}
	public void addPlayer(PlayerPanel rm)
	{
		currentPlayers.add(rm);
		System.out.println("addPlayer::There are " + currentPlayers.size() + " players in the list");
	}
	public void mainWindowClosing()
	{
		MyTunesDB.getDataBaseObject().saveSongHistory(songHistory);
		for(int i =0; i < currentPlayers.size(); i++)
		{
			currentPlayers.get(i).Stop();	
		}
		System.exit(0);
	}
	public void addSongToHistory(String str)
	{
		if(songHistory.size() >=10)songHistory.remove(0);
		songHistory.add(str);
	}
	public ArrayList<String> getSongHistory()
	{
		return songHistory;
	}

	

}
