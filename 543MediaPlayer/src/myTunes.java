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


class myTunes extends JFrame 
{
	PlayerPanel[] currentPlayers = new PlayerPanel[7];// limit 7 open players at one time
	int playerNum = 0;
	public myTunes() 
	{
		BaseWindow bw = new BaseWindow(this);
		currentPlayers[playerNum] = bw.getPlayerPanel();
		playerNum = playerNum+1;
		bw.getPlayerPanel().setMainController(this);
	}
	public void openPlayListWindow(String str)
	{
		PlayListWindow plw = new PlayListWindow(str);
		currentPlayers[playerNum] = plw.getPlayerPanel();
		playerNum = playerNum+1;
		plw.getPlayerPanel().setMainController(this);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new myTunes();
	}
	public void reservePlayer(PlayerPanel pp)
	{
		for(int i =0; i < playerNum; i++)
		{
			if(currentPlayers[i] != pp)
			{
				currentPlayers[i].shutUP();
			}
		}
	}

	

}
