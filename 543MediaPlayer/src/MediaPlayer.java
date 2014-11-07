import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.swing.JOptionPane;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;


public class MediaPlayer implements BasicPlayerListener
{

	private PlayerPanel playerUI;
	BasicController control;
	BasicPlayer player;
	String currentSong;
	//static MediaPlayer mp;
	boolean playing = false;
    boolean stopped = true; 
	int currentBytes,bitrate;
	int tagsize;
	String duration;
	
	public MediaPlayer()
	{
		System.out.println("new basic player!");
		player = new BasicPlayer();
		control = (BasicController) player;
		//player.
		player.addBasicPlayerListener(this);
	}
	public MediaPlayer(PlayerPanel pp)
	{
		System.out.println("new basic player(PlayerUI)!");
		player = new BasicPlayer();
		control = (BasicController) player;
		player.addBasicPlayerListener(this);
		playerUI = pp;
	}
	
	public void play_pause_Event()
	{
		if(isStopped())
		{
			if(currentSong == null)playerUI.playFirstSong();
			else
				play(currentSong);
			stopped = false;
			playing = true;
			playerUI.setPlayButtonIcon(getClass().getResource("pause.png"));
			playerUI.setActive(true);
		}
		else if (isPlaying())
		{
			try {
				control.pause();
				playing = false;
				playerUI.setPlayButtonIcon(getClass().getResource("play.png"));
			} catch (BasicPlayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			try {
				control.resume();
				playerUI.reservePlayer();
				playing = true;
				playerUI.setPlayButtonIcon(getClass().getResource("pause.png"));
			} catch (BasicPlayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
    private static void setPlayerUI(PlayerPanel ui)
    {
    	//playerUI = ui;
    }
	
	public void play(String filename) 
	{
		
		if(filename == null)return;
		playerUI.reservePlayer();
		currentSong = filename;
		//System.out.println("Now playing: " + currentSong);
		playerUI.setCurrentSong(filename);
		try
		{			
			
			control.open(new File(filename));
			
			// Start playback in a thread.
			control.play();
			
			playerUI.setPlayButtonIcon(getClass().getResource("pause.png"));
			playing = true;
			stopped = false;
			control.setGain(playerUI.getCurrentVolume());
			control.setPan(0.0);

			// If you want to pause/resume/pause the played file then
			// write a Swing player and just call control.pause(),
			// control.resume() or control.stop().			
			// Use control.seek(bytesToSkip) to seek file
			// (i.e. fast forward and rewind). seek feature will
			// work only if underlying JavaSound SPI implements
			// skip(...). True for MP3SPI (JavaZOOM) and SUN SPI's
			// (WAVE, AU, AIFF).
			
		}
		catch (BasicPlayerException e)
		{
			e.printStackTrace();
		} 
	}
	public long getTagSize()
	{
		return tagsize;
	}
	public boolean isPlaying()
	{
		return playing;
	}
	public boolean isStopped()
	{
		return stopped;
	}
	
	public String getCurrentSong()
	{
		return currentSong;
	}
	public void pause()
	{
		try {
			control.pause();
			playerUI.setPlayButtonIcon(getClass().getResource("play.png"));
		} catch (BasicPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		playing = false;
	}

	public void stop() 
	{
		try {
			control.stop();
		} catch (BasicPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		playing = false;
		stopped = true;
		playerUI.setPlayButtonIcon(getClass().getResource("play.png"));
		playerUI.setProgress(0);
		
	}
	public void reset()
	{
		playerUI.reset();
		currentSong = null;
	}
	public void seek(int x) 
	{
		try {
			control.seek((long)x);
			control.setGain(playerUI.getCurrentVolume());
		} catch (BasicPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}
	public void setGain(double gain) 
	{
		try {
			control.setGain(gain);
		} catch (BasicPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public int getBitRate()
	{
		return bitrate;
	}
	@Override
	public void opened(Object stream, Map properties) 
	{
		// TODO Auto-generated method stub
		Mp3File mp3file = null;
		try {
			mp3file = new Mp3File(currentSong);
			bitrate = mp3file.getBitrate();
			tagsize = (int) (mp3file.getLength() - (mp3file.getLengthInMilliseconds() / 8 * bitrate));
			
			if(mp3file.hasId3v2Tag() && playerUI != null) 
			{
				playerUI.setCurrentArt(mp3file.getId3v2Tag().getAlbumImage());
				
			}
			else
				playerUI.setCurrentArt(null);
		} catch (UnsupportedTagException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvalidDataException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String songInfo = "Now Playing:\n";
		songInfo = songInfo + ("Title: " + properties.get("title"));
		songInfo += ("\nAuthor: " + properties.get("author"));
		
		/* the casting here is the cause of the inaccuracy */
		/* need to step back and rethink this part */
		
		int lg = (int) (mp3file.getLength() - tagsize);
		long dur = (Long) properties.get("duration");
		
		playerUI.setProgressMax(lg-tagsize);
		duration = convertMicroSeconds(dur);
		songInfo += ("\nDuration: " + convertMilliSeconds(((mp3file.getLength()-tagsize)/bitrate*8)));
		playerUI.setPlayerText(songInfo);
		playerUI.setCurrentTime(duration);
		playerUI.setDuration((int) dur);
	}

	@Override
	// this is called about 2x -- 3x a second (based on OBSERVED BEHAVIOR) 
	// to update song progress. 
	// NOTE: this is called by the same thread (created when we call play(String)) 
	// that is responsible for loading the buffer to play the song. If this function 
	// takes to much time, the buffer will "run dry" before this thread can fill it 
	// again and cause a disruption in the playing of the song (the clicking sound). 
	// So it is important for this function to do as little as possible!!!!!!!
	public void progress(int bytesread, long arg1, byte[] arg2, Map arg3) 
	{
		if(!playerUI.isSeeking())playerUI.setProgress(bytesread-tagsize);
	}

	@Override
	public void setController(BasicController arg0) {}

	@Override
	public void stateUpdated(BasicPlayerEvent arg0) 
	{
		if(arg0.getCode() == BasicPlayerEvent.EOM )
		{
			if(playerUI.getRepeat() == true)
				play(currentSong);
			else
				playerUI.playNextSong();
				
		}
		
	}
	public static String convertMicroSeconds(long lg)
	{
		long org,min, seconds;
		String seconds_str = "";
		min = (int)(lg/60000000);
		lg = lg - (min * 60000000);
		seconds = (lg/1000000);
		if(seconds < 10)seconds_str = "0"+seconds;
		else seconds_str = seconds+""; 
		return(min +":" + seconds_str);
	}
	public static String convertMilliSeconds(long lg)
	{
		long org,min, seconds;
		String seconds_str = "";
		min = (int)(lg/60000);
		lg = lg - (min * 60000);
		seconds = (lg/1000);
		if(seconds < 10)seconds_str = "0"+seconds;
		else seconds_str = seconds+""; 
		return(min +":" + seconds_str);
	}
	
}
