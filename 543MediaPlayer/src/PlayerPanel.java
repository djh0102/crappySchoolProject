import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;

public class PlayerPanel extends JPanel implements ActionListener, MouseListener
{
	/* gui stuff */
	JLabel nowPlayingArt;
	JLabel timeRemaining;
	String duration = "0:00";
	JLabel currentTime;
	JButton play;
	JButton stop;
	JButton skip_prev;
	JButton skip_next;
	JToggleButton repeat;
	JToggleButton loop_start;
	JToggleButton loop_end;
	JCheckBox doLoop;
	JSlider volumeSlide;
	JSlider progressSlide;
	JScrollPane jsp1;
	//GraphicPanel currentArtwork;
	JTextArea nowPlaying;
	JPopupMenu popup;
	JMenuItem popShowLarge;
    MediaPlayer player;
    myTunes controller;
    /* player stuff */
  
    /* state variables */
    boolean stopped = true;
    boolean playing = false;
    boolean seeking = false;
    boolean active;
    boolean loop;
    String currentSong;
    /* loop stuff */
	String loop_start_song; 
	String loop_end_song;  /* these two must be equal in order to loop */
	public int loop_startbytes; /* byte location to start loop */
	int loop_endbytes;   /* byte location to end loop */
	int currentbytes;
	int duration_micro;  /* need this to show "time left in song"*/
	byte[] largeArt;     /* the artwork of current song (before resize) */
	
	TablePanel tablepanel;
	
	class LoopWatcher extends Thread
	{
		boolean seek = false;
		
		public void run()
		{
			System.out.println(loop_startbytes);
			loop_startbytes = loop_startbytes - (328 * player.getBitRate());
			System.out.println(loop_startbytes);
			while(true)
			{
				//System.out.println("Loopwatcher is alive");
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if((loop== true) && (currentbytes >= loop_endbytes-(100*player.getBitRate())))
				{
					seek = true;
				}
				else if((loop==true) && (currentbytes < loop_endbytes))
					seek=false;
				
				if(seek)
				{
					player.seek(loop_startbytes);
					seek = false;
				}
			}
		}
	}
	
	LoopWatcher lp;
	
    public PlayerPanel()
	{
		//player = MediaPlayer.getMediaPlayerObj(this);
    	player = new MediaPlayer(this);
		this.setSize(900,200);
		this.setLayout(null); /* i'll put things exactly where i want them */
		
		/* create the buttons put them in place */
		skip_prev = new JButton();
		skip_prev.setIcon(new ImageIcon(getClass().getResource("prev.png")));
		skip_prev.setLocation(12, 12);
		skip_prev.setSize(new Dimension(50,50));
		skip_prev.addActionListener(this);
		
		play = new JButton();
		play.setIcon(new ImageIcon(getClass().getResource("play.png")));
		play.setLocation(72, 12);
		play.setSize(new Dimension(50,50));
		play.addActionListener(this);
		
		stop = new JButton();
		stop.setIcon(new ImageIcon(getClass().getResource("stop.png")));
		stop.setLocation(132, 12);
		stop.setSize(new Dimension(50,50));
		stop.addActionListener(this);
		
		skip_next = new JButton();
		skip_next.setIcon(new ImageIcon(getClass().getResource("next.png")));
		skip_next.setLocation(192, 12);
		skip_next.setSize(new Dimension(50,50));
		skip_next.addActionListener(this);
		
		loop_start = new JToggleButton("A");
		loop_start.setSize(30,30);
		loop_start.setLocation(685,13);
		loop_start.addActionListener(this);
		
		loop_end = new JToggleButton("B");
		loop_end.setSize(30,30);
		loop_end.setLocation(727,13);
		loop_end.addActionListener(this);
		
		/* The volume and progress sliders */
		volumeSlide = new JSlider();
		volumeSlide.setMaximum(100);
		volumeSlide.setOrientation(javax.swing.JSlider.VERTICAL);
		volumeSlide.setSize(20, 130);
		volumeSlide.setLocation(254, 8);
		volumeSlide.setValue(60);
		volumeSlide.addChangeListener(new ChangeListener() 
        {
        	// whenever the value of volumeSlide changes, set the volume to
        	// the current value of the slider
        	  public void stateChanged(ChangeEvent event) 
        	  {
        		  player.setGain((double)(volumeSlide.getValue())/100);
			  } 
        	  
        });
		
		progressSlide = new JSlider();
		progressSlide.setMaximum(100);
		progressSlide.setValue(50);
		progressSlide.setOrientation(JProgressBar.HORIZONTAL);
		progressSlide.setLocation(312, 150);
		progressSlide.setSize(300, 30);
		progressSlide.setValue(0);
		progressSlide.addMouseListener(this);
		progressSlide.addChangeListener(new ChangeListener() 
        {
			/* whenever this value of the slider changes, the time of the current slider location is calculated */
        	  public void stateChanged(ChangeEvent event) 
        	  {
        		  if(player.getCurrentSong() != null)
  				  {
  				    currentTime.setText(convertMicroSeconds((progressSlide.getValue())/player.getBitRate()*8000));
  				    timeRemaining.setText(convertMicroSeconds(duration_micro - (progressSlide.getValue())/player.getBitRate()*8000));
  				  }
			  } 
        	  
        });
		
		timeRemaining = new JLabel("0:00");
		timeRemaining.setSize(90,50);
		timeRemaining.setLocation(progressSlide.getX()+300-10,progressSlide.getY()-12);
		
		currentTime = new JLabel(duration);
		currentTime.setSize(90,50);
		currentTime.setLocation(progressSlide.getX()-25,progressSlide.getY()-12);
		
		/* the label the hold the artwork from the mp3 */
		nowPlayingArt = new JLabel();
		nowPlayingArt.setSize(120,120);
		nowPlayingArt.setBackground(new Color(159,0,0));
		nowPlayingArt.setLocation(290, 12);
		nowPlayingArt.setBorder(new LineBorder(Color.WHITE, 3, true)); 
		nowPlayingArt.setIcon(new ImageIcon("/Users/daniel/Desktop/defualtArt.png"));
		nowPlayingArt.addMouseListener(this);
		
		/* a simple text area to show simple song info */
		nowPlaying = new JTextArea();
		nowPlaying.setSize(200,120);
		nowPlaying.setBorder(new LineBorder(Color.WHITE, 3, true));
		nowPlaying.setBackground(new Color(129,120,120));
		nowPlaying.setForeground(Color.WHITE);
		nowPlaying.setLocation(415,12);
		
		jsp1 = new JScrollPane(nowPlaying);
		jsp1.setLocation(411,13);
		jsp1.setSize(220,120);
		
		repeat = new JToggleButton("R");
		repeat.setSize(30,30);
		repeat.setLocation(643,13);
		
		popup = new JPopupMenu();
		popShowLarge = new JMenuItem("Show Large Art");
		popShowLarge.addMouseListener(this);
		popup.add(popShowLarge);
		
		this.add(skip_prev);
		this.add(play);
		this.add(stop);
		this.add(skip_next);
		this.add(volumeSlide);
		this.add(progressSlide);
		this.add(timeRemaining);
		this.add(currentTime);
		this.add(nowPlayingArt);
		this.add(jsp1);
		this.add(repeat);
		this.add(loop_start);
		this.add(loop_end);
		this.setIgnoreRepaint(true);
		this.setVisible(true);
		//MediaDuration md = new MediaDuration();
		//md.start();
	}
    /* control functions */
    public void setTablePTR(TablePanel tpa)
    {
    	/* connect to TablePanel for song list  */
    	tablepanel = tpa;
    }
    
    public double getCurrentVolume()
    {
    	return (double)volumeSlide.getValue()/100;
    }
   
    public void setProgress(int x)
    {
    	progressSlide.setValue(x);
    	currentbytes = x;	
    }
    /* this is used to check if the mediaplayer is playing our song */
    public void setCurrentSong(String str)
    {
    	currentSong = str;
    }
    public void setProgressMax(int x)
    {
    	progressSlide.setMaximum(x);
    }
    public void setActive(boolean b)
    {
    	active = b;  	
    }
  
    public void setPlayerText(String str)
    {
    	nowPlaying.setText(str);
    }
    public void setMainController(myTunes mt)
    {
    	controller = mt;
    }
    public void shutUP()
    {
    	player.pause();
    }
    public boolean isSeeking()
    {
    	return seeking;
    }
    public void play(String str)
    {
    	reservePlayer();
    	player.play(str);
    }
    public boolean getRepeat()
    {
    	return repeat.isSelected();
    }
    
    public void setPlayButtonIcon(URL url)
    {
    	play.setIcon(new ImageIcon(url));
    }
    
    public void setCurrentArt(byte[] img) throws IOException
    {
    	if((img != null) && img.length > 10)
    	{
    		largeArt = img;
    		ImageIcon imc = new ImageIcon(img);
    		Image image = imc.getImage();
    		Image im2 = image.getScaledInstance(120, 120, 0);
    		ImageIcon newIcon = new ImageIcon(im2);
    		nowPlayingArt.setIcon(newIcon);
    	}
    	else
    	{
    		ImageIcon tmp = new ImageIcon(getClass().getResource("defualtArt.png"));
    		largeArt = null;
    		nowPlayingArt.setIcon(tmp);
    	}
    }
    
    public void playFirstSong()
    {
    	if(!player.isPlaying())controller.reservePlayer(this);
    	player.play(tablepanel.getFirstSong());
    }
    
    public void playNextSong() 
    {
    	controller.reservePlayer(this);
    	player.stop();
    	player.play(tablepanel.getNextSong());
    }
    
    public void playPreviousSong() 
    {
    	controller.reservePlayer(this);
    	player.stop();
    	player.play(tablepanel.getPreviousSong());
    }
    public void reservePlayer()
    {
    	controller.reservePlayer(this);
    }
    public void setCurrentTime(String str)
    {
    	timeRemaining.setText(str);
    }
    public MediaPlayer getMediaPlayer()
    {
    	return player;
    }
    public void Stop()
    {
    	player.stop();
    }
	public void mouseClicked(MouseEvent arg0) 
	{
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
		if(arg0.getSource() == progressSlide)
		{
			seeking = true;  // block the bpinterface from setting the slider value
			BasicSliderUI ui = (BasicSliderUI)progressSlide.getUI();
			int value = ui.valueForXPosition( arg0.getX() );
			progressSlide.setValue(value);
		}
		
		if (arg0.isMetaDown())
		{
			popup.show(arg0.getComponent(),arg0.getX(),arg0.getY());
		}
		
		if(arg0.getSource() == popShowLarge)
		{
			if(largeArt != null)
			{
				JFrame temp = new JFrame();
				temp.setLocation(200,150);
				ImageIcon img = new ImageIcon(largeArt);
				JLabel temp1 = new JLabel();
				int width = img.getIconWidth();
				int height = img.getIconHeight();
				temp1.setIcon(img);
				temp.add(temp1);
				temp.setSize(width,height);
				temp.setVisible(true);
			}
			else
				JOptionPane.showMessageDialog(nowPlayingArt, "Current song has no album art.");
		}
		
		
	}
	
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getSource() == progressSlide)
		{
			int x = progressSlide.getValue();
			player.seek(x);
			seeking = false; // allow the media player to resume setting progressSlide value (position)
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getSource() == skip_next)
		{
			playNextSong();
			
		}
		else if(arg0.getSource() == play)
		{
			if(!player.isPlaying())controller.reservePlayer(this);
			player.play_pause_Event();
		}
			
		else if (arg0.getSource() == skip_prev)
		{
			playPreviousSong();
		}
		else if(arg0.getSource() == stop)
		{
			player.stop();
			/* if loopwatcher thread running, kill it */
			if(lp != null)lp.stop();
			/* clear the R, A and B buttons */
			loop_start.setSelected(false);
			loop_end.setSelected(false);
			repeat.setSelected(false);
			/* clear the progress time */
			timeRemaining.setText(convertMicroSeconds(duration_micro));
			currentTime.setText("0:00");
			
		}
		else if(arg0.getSource() == loop_start)
		{
			/* user unselected the button */
			if(!loop_start.isSelected())
			{
				clearLoop();
				loop_end.setSelected(false);
			}
			/* user selected the button */
			else
				setLoopStart();
		}
		
		else if(arg0.getSource() == loop_end)
		{
			if(!loop_end.isSelected())
			{
				/* user unselected the button */
				clearLoop();
				loop_start.setSelected(false);
			}
			/* user selected the button */
			else
				setLoopEnd();
		}
		
	}
	
	public void setDuration(int dur)
	{
		duration_micro = dur;
	}
	public void setLoopStart()
	{
		loop_startbytes = progressSlide.getValue();
		loop_start_song = player.getCurrentSong();
		lp = new LoopWatcher();
		lp.start();
	}
	
	public void setLoopEnd()
	{
		loop_endbytes = currentbytes;
		loop_end_song = player.getCurrentSong();
		if(loop_start_song.equals(loop_end_song) && (loop_endbytes > loop_startbytes))
		{
			loop = true;
		}
	}
	
	public void clearLoop()
	{
		loop_startbytes = 0;
		loop_endbytes = 0;
		loop_start_song = null;
		loop_end_song = null;
		loop = false;
		
		if(lp.isAlive())
		{
			System.out.println("killing loopWatcher");
			lp.stop();
		}
	}
	
	public Dimension getSize()
	{
		return new Dimension(this.getWidth(),this.getHeight());
	}
	
	public void paintComponent(Graphics g) 
    {
		ImageIcon ic = new ImageIcon(getClass().getResource("background2.png"));
      g.drawImage(ic.getImage(), 0, 0, getWidth(), getHeight(), this);
    }
	
	public static String convertMicroSeconds(long lg)
	{
		//if (lg==0)return "err";
		long org,min, seconds;
		String seconds_str = "";
		min = (int)(lg/60000000);
		lg = lg - (min * 60000000);
		seconds = (lg/1000000);
		if(seconds < 10)seconds_str = "0"+seconds;
		else seconds_str = seconds+""; 
		return(min +":" + seconds_str);
	}
	/*public static void main(String[] args)
	{
		PlayerPanel pp = new PlayerPanel();
		JFrame jf = new JFrame();
		jf.add(pp);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setSize(pp.getSize());
		jf.setVisible(true);
	}*/
	
}
