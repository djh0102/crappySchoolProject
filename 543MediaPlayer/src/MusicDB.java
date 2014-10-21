import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.table.DefaultTableModel;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;


public class MusicDB 
{
	private static String dbURL = "jdbc:derby:musicDB;create=true;user=APP;password=mine";
	private static Connection conn = null;
    private static Statement stmt = null;
    private String title;
	private String artist;
	private String album;
	private String genre;
	private long duration;
	private String fileName;
	DefaultTableModel model;
	private String[] columnNames = new String [] {"Title", "Artist", "Album", "Genre","Duration","FilePath"};
	Object[] rowData;
	private static MusicDB mdb;
    
    // When the object is created, the embedded driver is loaded and a connection is opened
    // Constructor is private so we can control if/when it's called
    private MusicDB()
    {
    	try
    	{
    		Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
    		//Get a connection
    		conn = DriverManager.getConnection(dbURL);
    		createTable("library");
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
    public static MusicDB getDataBaseObject()
    {
    	if(mdb == null)
    	{
    		mdb = new MusicDB();
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
			stmt.execute("CREATE TABLE " + tableName + "(title varchar(64),artist varchar(64),a"
					+ "lbum varchar(64),genre varchar(64),duration bigint,filePath "
					+ "varchar(255) )");
	
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
    		updateUI(name);
    	}
    	catch(SQLException e)
    	{
    		//System.out.println("\n clearTable() Failed!");
    		return;
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
    // this function deletes an individual tuple from the database
    public void deleteEntry(String tableName,String fileName)
    {
    	System.out.println(this.getClass().toString()+"::deleteEntry(String,String)");
    	fileName = makeSQLCompatible(fileName);
    	try
    	{
    		stmt=conn.createStatement();
    		//System.out.println("delete from "+tableName + " where filePath = '" + fileName+"'");
    		stmt.execute("delete from "+tableName + " where filePath = '" + fileName+"'");
    		stmt.close();
    		updateUI(tableName);
    	}
    	catch(SQLException e)
    	{
    		System.out.println("\n deleteTable() Failed!");
    		e.printStackTrace();
    		return;
    	}
    }
    // this function gets the row data of the table
    public Object[][] getDisplaySet(String name)
    {
    	int rows;
    	int columns;
    	int i = 0;
    	Object[][] tableset = null;
    	try {
			//stmt = conn.createStatement();
    		stmt =conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ResultSet results = stmt.executeQuery("select * from " + name);
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
                tableset[i][4] = results.getLong(5);
                tableset[i][5] = results.getString(6);
  
                i++;
	        }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return tableset;
    	
        
    }
    // this method prints the table to the console
    // for debugging purposes only
    public void printTable(String name)
    {
    	try
        {
            stmt = conn.createStatement();
            ResultSet results = stmt.executeQuery("select * from " + name);
            ResultSetMetaData rsmd = results.getMetaData();
            int numberCols = rsmd.getColumnCount();
          
            for (int i=1; i<=numberCols; i++)
            {
                //print Column Names
                System.out.print(rsmd.getColumnLabel(i)+"\t\t");  
            }

            System.out.println("\n--------------------------------------------------------------------------");

            while(results.next())
            {
                String title = results.getString(1).substring(0, 7);
                String artist = results.getString(2);
                String album = results.getString(3);
                String genre = results.getString(4);
                long duration = results.getLong(5);
                System.out.println(title + "\t\t" + artist+"\t\t" + album+"\t"+genre+"\t\t"+duration);
            }
            results.close();
            stmt.close();
        }
    	catch (SQLException sqlExcept)
        {
            sqlExcept.printStackTrace();
        }
    }
    // inserts a song into list
    // returns true if song is added to list, or if song is already in list
    // returns false if song could not be added to list
    public boolean insertEntry(String name, String file)
    {
    	System.out.println(this.getClass().toString()+"::insertEntry(String,String)");
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
			}// end if
			else if(mp3file.hasId3v2Tag())
			{
				ID3v2  id3v2 = mp3file.getId3v2Tag();
				title = makeSQLCompatible(id3v2.getTitle());
			    artist = makeSQLCompatible(id3v2.getArtist());
			    album = makeSQLCompatible(id3v2.getAlbum());
			    genre = makeSQLCompatible(id3v2.getGenreDescription());
				  
			}//end elseif
			
		    duration = mp3file.getLengthInMilliseconds();
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
			//System.out.println("insert into " + name + " values ('" + title + "','" + artist + "','" + album +"','"+ genre + "'," +duration + ",'" + fileName + "')");
			stmt.execute("insert into " + name + " values ('" + title + "','" + artist + "','" + album +"','"+ genre + "'," +duration + ",'" + fileName + "')");
			//System.out.println("insert into " + name + " values ('" + title + "','" + artist + "','" + album +"','"+ genre + "'," +duration + ",'" + file + "')");
			
	        stmt.close();
	        updateUI(name);
		} 
    	catch (SQLException e) 
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    	
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
    	return output;
    }
   
    public Object[] getColumnNames()
    {
    	return columnNames;
    }
    
    // this is how we link this database obj to the jTable in the gui.
    // any changes we make to the model are immediately reflected in the jTable
    public void setUI(DefaultTableModel dtm)
    {
    	model = dtm;
    }
    
    // update the jTable dataset to reflect the current state of the data base
    private void updateUI(String tableName)
    {
    	System.out.println(this.getClass().toString()+"::updateUI(String): arg0 = " + tableName+"\n\n");
    	if (model != null)
    	{
    		model.setDataVector(getDisplaySet(tableName), columnNames);
    	}
    }
}
