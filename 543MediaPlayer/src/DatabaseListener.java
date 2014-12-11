public interface DatabaseListener extends java.util.EventListener 
{
		// It's up to the client code to decide how to implement the handler(s)
		void dataBaseDataChanged(DataBaseEvent arg0);
		void playlistEvent(PlaylistEvent arg0);
}