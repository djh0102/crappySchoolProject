import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayerListener;


public class BPInterface implements BasicPlayerListener
{

	private PlayerUI playerUI;
	BasicController control;
	BasicPlayer player;
	
	public BPInterface(PlayerUI ui)
	{
		// I need the playerUI to be able to control the gui
		playerUI = ui;
		player = new BasicPlayer();
		control = (BasicController) player;
		//player.setSleepTime(1);
		player.addBasicPlayerListener(this);
	}
	
	public void play(String filename) throws FileNotFoundException
	{
		
		if(filename == null)return;

		try
		{			
			
			control.open(new File(filename));
			// Start playback in a thread.
			control.play();
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
	public void pause() throws BasicPlayerException
	{
		control.pause();
	}
	public void resume() throws BasicPlayerException
	{
		control.setGain(playerUI.getCurrentVolume());
		control.resume();
	}
	public void stop() throws BasicPlayerException
	{
		control.stop();
	}
	public void seek(int x) throws BasicPlayerException
	{
		control.seek((long)x);
		control.setGain(playerUI.getCurrentVolume());
	
	}
	public void setGain(double gain) throws BasicPlayerException
	{
		control.setGain(gain);
	}
	@Override
	public void opened(Object stream, Map properties) 
	{
		// TODO Auto-generated method stub
		String songInfo = "Now Playing:\n";
		songInfo = songInfo + ("Title: " + properties.get("title"));
		songInfo += ("\nAuthor: " + properties.get("author"));
		int lg = (Integer) properties.get("mp3.length.bytes");
		long dur = (Long) properties.get("duration");
		playerUI.setProgressMax(lg);
		songInfo += ("\nDuration: " + convertMicroSeconds(dur));
		playerUI.setPlayerText(songInfo);
	}

	@Override
	// this is called a
	public void progress(int bytesread, long arg1, byte[] arg2, Map arg3) 
	{
		if(!playerUI.isSeeking())playerUI.setProgress(bytesread);// TODO Auto-generated method stub
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
			playerUI.playNextSong();
		}
		
	}
	public static String convertMicroSeconds(long lg)
	{
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
