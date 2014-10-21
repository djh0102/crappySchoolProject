import java.io.File;
import java.io.IOException;
import java.util.Map;
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

	private static PlayerPanel playerUI;
	BasicController control;
	BasicPlayer player;
	String currentSong;
	TablePanel tableControl;
	static MediaPlayer mp;
	boolean playing = false, stopped = true, loop = false;
	int currentBytes, tagsize = 0, loop_start,loop_end,bitrate;
	String duration;
	
	private MediaPlayer()
	{
		player = new BasicPlayer();
		control = (BasicController) player;
		//player.setSleepTime(1);
		player.addBasicPlayerListener(this);
	}
	
	public static MediaPlayer getMediaPlayerObj(PlayerPanel ui)
	{
		setPlayerUI(ui);
		if(mp == null)mp = new MediaPlayer();
		
		return mp;
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
				playing = true;
				playerUI.setPlayButtonIcon(getClass().getResource("pause.png"));
			} catch (BasicPlayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static MediaPlayer getMediaPlayerObj()
	{
		if(mp == null)mp = new MediaPlayer();
		return mp;
	}
	
    private static void setPlayerUI(PlayerPanel ui)
    {
    	playerUI = ui;
    }
	public void setTableControl(TablePanel tp)
	{
		tableControl = tp;
	}
	public void play(String filename) 
	{
		
		if(filename == null)return;
		currentSong = filename;
		
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
	public void resume()
	{
		try {
			control.setGain(playerUI.getCurrentVolume());
			control.resume();
			playerUI.setPlayButtonIcon(getClass().getResource("pause.png"));
		} catch (BasicPlayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		playing = true;
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
			tagsize = mp3file.getStartOffset();
			if(mp3file.hasId3v2Tag() && playerUI != null) 
			{
				playerUI.setCurrentArt(mp3file.getId3v2Tag().getAlbumImage());
				
			}
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
		int lg = (Integer) properties.get("mp3.length.bytes");
		long dur = (Long) properties.get("duration");
		playerUI.setProgressMax(lg);
		duration = convertMicroSeconds(dur);
		songInfo += ("\nDuration: " + duration);
		songInfo += ("\nBitrate: " + bitrate);
		songInfo += ("\nsize: "+ (mp3file.getLength()-tagsize));
		songInfo += ("\nmydur: " + convertMicroSeconds((mp3file.getLength()-tagsize)/bitrate*8000));
		playerUI.setPlayerText(songInfo);
		
	}

	@Override
	// this is called a
	public void progress(int bytesread, long arg1, byte[] arg2, Map arg3) 
	{
		if(!playerUI.isSeeking())playerUI.setProgress(bytesread);
		
		playerUI.setCurrentTime(convertMicroSeconds((bytesread-tagsize)/bitrate*8000) + "/" + duration);
		
	}

	@Override
	public void setController(BasicController arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateUpdated(BasicPlayerEvent arg0) 
	{
		// TODO Auto-generated method stub
		if(arg0.getCode() == BasicPlayerEvent.EOM )
		{
			// tell the UI to play next song in playlist
			// playerUI.playNextSong();
			if(playerUI.getRepeat() == true)
				play(currentSong);
			else
				playerUI.playNextSong();
				
		}
		
	}
	public static String convertMicroSeconds(long lg)
	{
		//if (lg==0)return "err";
		//System.out.println("Here is lg:: " + lg);
		int min, seconds;
		String seconds_str = "";
		min = (int)(lg/60000000);
		lg = lg - (min * 60000000);
		seconds = (int)(lg/1000000);
		if(seconds < 10)seconds_str = "0"+seconds;
		else seconds_str = seconds+""; 
		return(min +":" + seconds_str);
	}
	
}
