import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;


public class MyTunesDB 
{
	private static String dbURL = "jdbc:derby:musicDB;create=true;user=APP;password=mine";
	private static Connection conn = null;
    private static Statement stmt = null;
    private String title;
	private String artist;
	private String album;
	private String genre;
	private String release;
	private String comment;
	private String duration_str;
	private long duration;
	private String fileName;
	DefaultTableModel model;
	TablePanel tableUI;
	private String[] columnNames = new String [] {"Title", "Artist", "Album", "Genre","Time","Year","Comment","Duration","FileName"};
	Object[] rowData;
	private static MyTunesDB mdb;
	//String orderby = "Title";
	ArrayList<DatabaseListener> listeners;
    
    // When the object is created, the embedded driver is loaded and a connection is opened
    // Constructor is private so we can control if/when it's called
    private MyTunesDB()
    {
    	listeners = new ArrayList<DatabaseListener>();
    	try
    	{
    		Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
    		//Get a connection
    		conn = DriverManager.getConnection(dbURL);
    		//deleteTable("ColumnState");
    		//deleteTable("library");
    		//createTable("library");
    		//createPlaylistTable();
    		//createTablePlaylistNames();
    		//clearTable("PlaylistNames");
    		//createTableColumnState();
    		//createTableSongHistory();
    	}
    	catch (Exception except)
    	{
    		except.printStackTrace();
    	}
    }
    // Singleton design pattern
    // the only way to get an instance of the database is by calling this method
    // database object is only created if it hasn't been already
    // this way there can be only one
    public static MyTunesDB getDataBaseObject()
    {
    	if(mdb == null)
    	{
    		mdb = new MyTunesDB();
    	}
    	return mdb;
    }
    
    // this funtion creates a new table in the database if it doesn't exist already
    // if the table does exist, the exception is ignored and nothing happens
    public void createTable(String tableName)
    {
    	// Every table/playlist in our music database will have the same schema
    	try {
			stmt = conn.createStatement();
			//stmt.execute("CREATE TABLE " + tableName + "(title varchar(64),artist varchar(64),album varchar(64),genre varchar(64),release varchar(10),comment varchar(255),duration_str varchar(10),duration bigint,filePath varchar(255) )");
			stmt.execute("CREATE TABLE " + tableName + "(title varchar(64),artist varchar(64),album varchar(64),genre varchar(64),duration_str varchar(10),release varchar(10),comment varchar(255),duration bigint,filePath varchar(255), PRIMARY KEY (filePath) )");
			System.out.println("CREATE TABLE " + tableName + "(title varchar(64),"
															+ "artist varchar(64),"
															+ "album varchar(64),"
															+ "genre varchar(64),"
															+ "year varchar(10),"
															+ "comment varchar(255),"
															+ "duration_str varchar(10),"
															+ "duration bigint,"
															+ "filePath varchar(255) UNIQUE,"
															+ "PRIMARY KEY(filePath))");
	        stmt.close();
		} catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			 if (e.getSQLState().equals("X0Y32"))System.out.println("");
			 else 
				 e.printStackTrace();
			
		}
        
    }
    // this function deletes every row from a table but not the table itself
    public void clearTable(String name)
    {
    	try
    	{
    		stmt=conn.createStatement();
    		stmt.execute("DELETE from "+name);
    		stmt.close();
    		//updateUI(name);
    	}
    	catch(SQLException e)
    	{
    		//System.out.println("\n clearTable() Failed!");
    		return;
    	}
    	
    }
    
    public boolean[] getColumnState()
    {
    	ResultSet rs = null;
    
    	boolean[] tmp = new boolean[6];
    	int[] values = new int[6];
    	try {
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery("select * from ColumnState");
			rs.first();
			values[0] = rs.getInt(1);
			values[1] = rs.getInt(2);
			values[2] = rs.getInt(3);
			values[3] = rs.getInt(4);
			values[4] = rs.getInt(5);
			values[5] = rs.getInt(6);
			for(int i =0; i < 6; i++)
			{
				
				if(values[i] == 1)tmp[i] = true;
				else
					tmp[i] = false;
				
				//System.out.print(tmp[i] + ",");
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	//System.out.println("::::::::::::: here is i " + i);
    	return tmp;
    }
    public void saveColumnState(boolean[] columnState)
    {
    	int[] tmp = new int[6];
    	for(int i =0; i < 6; i++)
    	{
    		if(columnState[i])tmp[i] = 1;
    		else
    			tmp[i] = 0;
    	}
    	try {
			stmt = conn.createStatement();
			stmt.execute("Delete from ColumnState");
			stmt.execute("INSERT INTO ColumnState VALUES("+ tmp[0] +"," + tmp[1] + "," + tmp[2] + ","+ tmp[3] + "," + tmp[4] + "," + tmp[5] + ")");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    // this function is called to get the index of a song
    // if song is in list, it's index is returned
    // if song not in list, -1 is returned
    public int getIndexOf(String name)
    {
    	String tableset = null;
    	try
    	{
    		ResultSet results = stmt.executeQuery("select * from library");
    		int i = 0;
    		while(results.next())
    		{
    			 tableset = results.getString(6);
    			 if(tableset.equals(name))return i;
    			i++;
    		}
    	}
    	catch(SQLException e){
    		
    	}
    	return -1;
    }
    // this function drops a table
    // this function will be modified to delete a playlist
    public void deleteTable(String name)
    {
    	try
    	{
    		stmt=conn.createStatement();
    		stmt.execute("DROP TABLE "+name);
    		stmt.close();
    	}
    	catch(SQLException e)
    	{
    		//System.out.println("\n deleteTable() Failed!");
    		return;
    	}
    }
    public void createTableSongHistory()
    {
    	try {
			stmt = conn.createStatement();
			stmt.execute("CREATE TABLE SongHistory(song varchar(256))");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    // this function deletes an individual tuple from the database
    public void deleteEntry(String tableName,String fileName)
    {
    	System.out.println(this.getClass().toString()+"::deleteEntry(String,String)");
    	String tmp = makeSQLCompatible(fileName);
    	try
    	{
    		stmt=conn.createStatement();
    		//System.out.println("delete from "+tableName + " where filePath = '" + fileName+"'");
    		if(!tableName.equals("Library"))stmt.execute("delete from playlist where filePath = '" + tmp+"' AND name ='" + tableName +"'");
    		else
    		{
    			stmt.execute("delete from playlist where filePath = '" + tmp + "'");
    			stmt.execute("delete from "+tableName + " where filePath = '" + tmp+"'");
    		}
    		stmt.close();
    		
    		
    	}
    	catch(SQLException e)
    	{
    		System.out.println("\n deleteTable() Failed!");
    		e.printStackTrace();
    		return;
    	}
    	fireDataBaseEvent(fileName,"delete");
    }
    // this function creates the playlist table
    public void createPlaylistTable()
    {
    	try {
			stmt = conn.createStatement();
			stmt.execute("CREATE TABLE Playlist (filePath varchar(255), name varchar(64), Constraint playlist_fk foreign key (filePath) References library (filePath),Constraint playlist_fk1 foreign key (name) References PlaylistNames (name))");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void createTablePlaylistNames()
    {
    	try {
			stmt = conn.createStatement();
			stmt.execute("CREATE TABLE PlaylistNames (name varchar(64) PRIMARY KEY)");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void createTableColumnState()
    {
    	try {
			stmt = conn.createStatement();
			stmt.execute("CREATE TABLE ColumnState(Artist smallint,Album smallint,Genre smallint,Time smallint,RYear smallint,Comment smallint)");
			stmt.execute("INSERT INTO ColumnState VALUES(1,1,1,1,1,1)");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public Object[] getPlaylistNames()
    {
    	Object[] names = null;
    	ResultSet results = null;
    	int rows,i=0;
    	try {
			stmt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			results = stmt.executeQuery("select * from PlaylistNames");
			results.last();
			rows = results.getRow();
			results.beforeFirst();
			names = new Object[rows];
			
			while(results.next())
			{
				names[i] = results.getString(1);
				i++;
			}
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return names;
    }
    // this function gets the row data of the table
    public Object[][] getDisplaySet(String name, String orderby, boolean des)
    {
    	String order = (des ==true)?"Desc":"Asc";
    	System.out.println(this.getClass().toString()+ "::getDisplaySet(" + name + ", " + orderby + ", "+ order+ ")");
    	int rows;
    	int columns;
    	int i = 0;
    	ResultSet results = null;
    	Object[][] tableset = null;
    	try {
			//stmt = conn.createStatement();
    		stmt =conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			if(name.equals("Library"))results = stmt.executeQuery("select * from " + name + " ORDER BY " + orderby + " " + order);
			
			else
			{
				results = stmt.executeQuery("select * from library inner join Playlist on library.filePath = Playlist.filePath where Playlist.name ='" + name+"'"+ " ORDER BY " + orderby + " " + order);
			}
			results.last();         //move pointer to last row
			rows = results.getRow();//row number of last row
			results.beforeFirst();        //move pointer back to the first row
	        ResultSetMetaData rsmd = results.getMetaData();
	        columns = rsmd.getColumnCount();
	        //System.out.println("this set has " + rows + " rows and " + columns + " columns");
	        tableset = new Object[rows][columns];
	        
	        while(results.next())
	        {
	        	tableset[i][0] = results.getString(1);
                tableset[i][1] = results.getString(2);
                tableset[i][2] = results.getString(3);
                tableset[i][3] = results.getString(4);
                tableset[i][4] = results.getString(5);
                tableset[i][5] = results.getString(6);
                tableset[i][6] = results.getString(7);
                tableset[i][7] = results.getLong(8);
                tableset[i][8] = results.getString(9);
  
                i++;
	        }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return tableset;
    	
    }
    public void addPlaylist(String str)
    {
    	System.out.println(this.getClass().toString()+"::addPlaylist("+str+")");
    	try {
			stmt = conn.createStatement();
			stmt.execute("insert into PlaylistNames values('" + str + "')");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Playlist: " + str + " already exists!");
		}
    }
    public void deletePlaylist(String str)
    {
    	System.out.println(this.getClass().toString()+"::deletePlaylist("+str+")");
    	try {
			stmt = conn.createStatement();
			stmt.execute("delete from Playlist where name = '" + str + "'");
			stmt.execute("delete from PlaylistNames where name = '" + str + "'");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	firePlaylistBaseEvent(str, "delete");
    }
    public void insertIntoPlaylist(String fileName,String playlistName)
    {
    	try {
			insertEntry("library",fileName);
			stmt = conn.createStatement();
			stmt.execute("insert into playlist values( '" + makeSQLCompatible(fileName) +"', '" + makeSQLCompatible(playlistName) + "')");
			stmt.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
    	fireDataBaseEvent(fileName,"insert");
    }
    // inserts a song into list
    // returns true if song is added to list, or if song is already in list
    // returns false if song could not be added to list
    public boolean insertEntry(String name, String file)
    {
    	System.out.println(this.getClass().toString()+"::insertEntry("+name+","+file+")");
    	fileName = makeSQLCompatible(file);
    	
    	// if file already in list, stop
    	if(getIndexOf(file) != -1)return true;
   
    	//System.out.println("In insert: args = " + name + ": " + file);
		Mp3File mp3file = null;
		try {
			mp3file = new Mp3File(file);
			if (mp3file.hasId3v1Tag()) 
			{
				ID3v1 id3v1 = mp3file.getId3v1Tag();
				title = makeSQLCompatible(id3v1.getTitle());
			    artist = makeSQLCompatible(id3v1.getArtist());
			    album = makeSQLCompatible(id3v1.getAlbum());
			    genre = makeSQLCompatible(id3v1.getGenreDescription());
			    release = makeSQLCompatible(id3v1.getYear());
			    comment = makeSQLCompatible(id3v1.getComment());
			    
			}// end if
			else if(mp3file.hasId3v2Tag())
			{
				ID3v2  id3v2 = mp3file.getId3v2Tag();
				title = makeSQLCompatible(id3v2.getTitle());
			    artist = makeSQLCompatible(id3v2.getArtist());
			    album = makeSQLCompatible(id3v2.getAlbum());
			    genre = makeSQLCompatible(id3v2.getGenreDescription());
			    release = makeSQLCompatible(id3v2.getYear());
			    comment = makeSQLCompatible(id3v2.getComment());
				  
			}//end elseif
			
		    duration = mp3file.getLengthInMilliseconds();
		    duration_str = convertMilliSeconds(duration);
		    //System.out.println("insert into " + name + " values ('" + title + "','" + artist + "','" + album +"','"+ genre + "'," +duration + ",'" + file + "')");
		
		} catch (UnsupportedTagException e1) {
			// tag can't be read
			// alert user that song can't be added to playlist
			System.out.println("UnsupportedTagException");
			return false;
		} catch (InvalidDataException e1) {
			// unsupported format
			// alert user to invalid file
			System.out.println("InvalidDataException");
			return false;
		} catch (IOException e1) {
			// file not found
			System.out.println("IOException");
			return false;
			//e1.printStackTrace();
		}
		
    	
    	try {
			stmt = conn.createStatement();
			//System.out.println("insert into " + name + " values ('" + title + "','" + artist + "','" + album +"','"+ genre + "',"+"'"+release+"','"+comment+"','"+duration_str+"'," +duration + ",'" + fileName + "')");
			//stmt.execute("insert into " + name + " values ('" + title + "','" + artist + "','" + album +"','"+ genre + "'," +duration + ",'" + fileName + "')");
			stmt.execute("insert into " + name + " values ('" + title + "','" + artist + "','" + album +"','"+ genre + "',"+"'"+duration_str+"','"+release+"','"+comment+"'," +duration + ",'" + fileName + "')");
			//System.out.println("insert into " + name + " values ('" + title + "','" + artist + "','" + album +"','"+ genre + "'," +duration + ",'" + file + "')");
			
	        stmt.close();
	        //updateUI(name);
		} 
    	catch (SQLException e) 
    	{
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return true;
		}
    	fireDataBaseEvent(fileName,"insert");
        return true;
    }
    // this function checks to see if a table is empty
    // returns true is table is empty, false if not
    public boolean isEmpty(String tableName) 
    {
    	boolean empty = true;
    	try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from " + tableName);
			empty = !rs.next();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return empty;
    }
    // this function makes a java string SQL compatible
    // if a single quote is detected in the string, another one
    // is inserted in front of it.  This is equivalent to inserting
    // a backslash in front of a " to make it printable in java
    private String makeSQLCompatible(String str)
    {
    	if (str == null)return "";
    	CharSequence s ="\'";
    	String output = "";
    	if(!str.contains(s))return str;
    	else
    	{
    		//System.out.println("im here");
    		for(int i = 0; i < str.length();i++)
    		{
    			char ptr = str.charAt(i);
    			if(ptr == '\'')
    			output+='\'';
    			output+=ptr;
    			
    		}
    	}
    	//System.out.println("output = " + output);
    	fireDataBaseEvent(fileName,"insert");
    	return output;
    }
   
    public Object[] getColumnNames()
    {
    	return columnNames;
    }
    public void saveSongHistory(ArrayList<String> songs)
    {
    	try {
			stmt=conn.createStatement();
			stmt.execute("Delete from SongHistory");
			for (int i = 0; i < songs.size();i++)
			{
				stmt.execute("INSERT INTO SongHistory VALUES('" + makeSQLCompatible(songs.get(i)) + "')");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public ArrayList<String> getSongHistory()
    {
    	ArrayList<String> songHistory = new ArrayList<String>();
    	int rows = 0;
    	ResultSet rs = null;
    	try {
			stmt=conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery("select * from SongHistory");
			while(rs.next())
			{
				songHistory.add(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return songHistory;
    }
    public void fireDataBaseEvent(String filename, String op)
    {
    	if (listeners.size() == 0)return;
    	
    	for(int i = 0; i < listeners.size(); i++)
    	{
    		listeners.get(i).dataBaseDataChanged(new DataBaseEvent(this,filename,op));
    	}
    }
    
    public void firePlaylistBaseEvent(String playlistName, String type)
    {
    	if (listeners.size() == 0)return;
    	
    	for(int i = 0; i < listeners.size(); i++)
    	{
    		listeners.get(i).playlistEvent(new PlaylistEvent(this,playlistName, type));
    	}
    }
    // this is how we link this database obj to the jTable in the gui.
    // any changes we make to the model are immediately reflected in the jTable
    public void addDataBaseListener(DatabaseListener listener)
    {
    	listeners.add(listener);
    }
    public void removeDataBaseListener(DatabaseListener listener)
    {
    	listeners.remove(listener);
    }
    public void setUI(TablePanel tp)
    {
    	//model = (DefaultTableModel) tp.getTableObj().getModel();
    	//tableUI = tp;
    }
    public static String convertMilliSeconds(long lg)
	{
		//if (lg==0)return "err";
		//System.out.println("Here is lg:: " + lg);
		long org,min, seconds;
		String seconds_str = "";
		min = (int)(lg/60000);
		lg = lg - (min * 60000);
		seconds = (lg/1000);
		if(seconds < 10)seconds_str = "0"+seconds;
		else seconds_str = seconds+""; 
		return(min +":" + seconds_str);
	}
    public static String convertMicroSeconds(long lg)
	{
		if (lg==0)return "err";
		System.out.println("Here in MyTunesDB: lb = " + lg);
		long min, seconds;
		String seconds_str = "";
		min = (int)(lg/60000000);
		System.out.println("Here in MyTunesDB: min = " + min);
		lg = lg - (min * 60000000);
		seconds = (lg/1000000);
		System.out.println("Here in MyTunesDB: seconds = " + seconds);
		if(seconds < 10)seconds_str = "0"+seconds;
		else seconds_str = seconds+""; 
		return(min +":" + seconds_str);
	}
    // update the jTable dataset to reflect the current state of the data base
    /*public void updateUI(String tableName)
    {
    	
    	if (model != null)
    	{
    		System.out.println(this.getClass().toString()+"::updateUI(String): arg0 = " + tableName+"\n\n");
    		JTable tm = tableUI.getTableObj();
    		model.setDataVector(getDisplaySet(tableName), columnNames);
    		tm.removeColumn(tm.getColumnModel().getColumn(tm.getColumnModel().getColumnIndex("FileName")));
    		tm.removeColumn(tm.getColumnModel().getColumn(tm.getColumnModel().getColumnIndex("Duration")));
    		tm.getColumnModel().getColumn(tm.getColumnModel().getColumnIndex("Year")).setMaxWidth(45);
    		tm.getColumnModel().getColumn(tm.getColumnModel().getColumnIndex("Time")).setMaxWidth(40);
    		tm.getColumnModel().getColumn(tm.getColumnModel().getColumnIndex("Genre")).setMaxWidth(60);
    		
    		//tm.moveColumn(column, targetColumn); // actually, just fix the db
    	}
    }*/
}
