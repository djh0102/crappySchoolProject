import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;


public class PlaylistPanel extends JPanel implements MouseListener
{
	JTree library;
	JTree playList;
	JScrollPane jsp;
	JScrollPane jsp1;
	JPopupMenu jpop;
	JMenuItem createNew;
	JMenuItem deleteList;
	JMenuItem newWindow;
	TablePanel table;
	MyTunesDB database;
	myTunes control;
	public PlaylistPanel() 
	{
		jpop = new JPopupMenu();
		createNew = new JMenuItem("Add Playlist");
		createNew.addMouseListener(this);
		deleteList = new JMenuItem("Delete Playlist");
		deleteList.addMouseListener(this);
		newWindow = new JMenuItem("Open in new Window");
		newWindow.addMouseListener(this);
		jpop.add(createNew);
		jpop.add(deleteList);
		jpop.add(newWindow);
		this.setSize(160, 800);
		jsp = new JScrollPane();
		jsp1 = new JScrollPane();
		jsp.setSize(150,120);
		jsp1.setSize(150,25);
		this.setLayout(null);
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Library");
		library = new JTree(rootNode);
		library.addTreeSelectionListener(new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent e) 
			{	
				if (library.isSelectionEmpty()) return;
				System.out.println("PlaylistPanel::setSelectedPlaylist(\"library\")");
				playList.clearSelection();
			}
			
			});
		
		 library.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				
				if(arg0.getClickCount() == 2 && !arg0.isConsumed())
				{
					arg0.consume();
					if (library.isSelectionEmpty()) return;
					//System.out.println("TablePanel::setTableView(\"library\")");
					table.setCurrentTableView("Library");
				}
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseExited(MouseEvent arg0) {}

			@Override
			public void mousePressed(MouseEvent arg0) {}

			@Override
			public void mouseReleased(MouseEvent arg0) {}
			
		});
		jsp1.setLocation(10, 10);
		jsp.setLocation(10, 35);
		rootNode = new DefaultMutableTreeNode("Playlist");
		playList = new JTree(rootNode);
		//playList.setRootVisible(false);
		
		playList.addTreeSelectionListener(new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent e) 
			{
				if (playList.isSelectionEmpty()) return;
				if(getSelectedNode().equals("Playlist"))
				{
					
				}
				//if(!name.equals("Playlist"))System.out.println("PlaylistPanel::setSelectedPlaylist(\"" + name +"\")");
				library.clearSelection();
			}
			
			});
		playList.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				
				if(arg0.isMetaDown())
				{
					if (playList.isSelectionEmpty()) return;
					//System.out.println("right click detected on::" +getSelectedNode());
					jpop.show(playList, arg0.getX(), arg0.getY());
					//if(!name.equals("Playlist"))System.out.println("PlaylistPanel::showpopup(\"" + name +"\")");
				}
				if(arg0.getClickCount() == 2 && !arg0.isConsumed())
				{
					arg0.consume();
					if (playList.isSelectionEmpty()|| getSelectedNode().equals("Playlist")) return;
					//System.out.println("double click detected on::" +getSelectedNode());
					table.setCurrentTableView(getSelectedNode());
				}
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseExited(MouseEvent arg0) {}

			@Override
			public void mousePressed(MouseEvent arg0) {}

			@Override
			public void mouseReleased(MouseEvent arg0) {}
			
		});
		database = MyTunesDB.getDataBaseObject();
		
		jsp.setViewportView(playList);
		jsp1.setViewportView(library);
		this.add(jsp);
		this.add(jsp1);
		buildPlaylistTree();
	}
	
	public void deletePlaylist()
	{
		int x = JOptionPane.showConfirmDialog(null, "Are you sure?");
		//System.out.println("Here is x::" + x);
		if(x == 0)
		{
			database.deletePlaylist(getSelectedNode());
			buildPlaylistTree();
		}
	}
	public void setMainController(myTunes mt)
	{
		control = mt;
	}
	public void addPlayList(String str)
	{
		if(str == null)
		{
			str = JOptionPane.showInputDialog("Enter a name for new Playlist");
		}
		
		if(str.length() > 0 && isValid(str))
		{
			database.addPlaylist(str);
			buildPlaylistTree();
			table.setCurrentTableView(str);
		}
		
	}
	public String getSelectedNode()
	{
		TreePath tp = playList.getSelectionPath();
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp.getLastPathComponent();
		Object userobj = node.getUserObject();
		return(userobj.toString());
	}
	public void setTablePanel(TablePanel tp)
	{
		table = tp;
	}
	public void buildPlaylistTree()
	{
	
		Object[] names = database.getPlaylistNames();
		DefaultTreeModel model = (DefaultTreeModel)playList.getModel();
		MutableTreeNode root = (MutableTreeNode) model.getRoot();
		while(!model.isLeaf(root))
		{
			model.removeNodeFromParent((MutableTreeNode)model.getChild(root, 0));
		}
		
		for(int i = 0; i < names.length; i++)
		{
			model.insertNodeInto((new DefaultMutableTreeNode((String)names[i])), root, root.getChildCount());
		}
		playList.expandPath(new TreePath(playList.getModel().getRoot()));
	}
	public void showPopUp()
	{
		// TODO show the appropriate option for selected node
	}
	/*public static void main(String[] args) 
	{
		
		PlaylistPanel pp = new PlaylistPanel();
		JFrame jf = new JFrame();
		jf.add(pp);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setSize(pp.getSize());
		jf.setVisible(true);

	}*/
	
	public boolean isValid(String str)
	{
		boolean valid = false;
		for(int i = 0; i < str.length(); i++)
		{
			if((isAlpha(str.charAt(i)) || (isDigit(str.charAt(i)))))
			{
				valid = true;
			}
		}
		
		return valid;
	}
	private boolean isAlpha(char a)
	{
		boolean type = false;
		if((a >= 'a') && (a <= 'z') )type = true;
		else if((a >= 'A') && (a <= 'Z') ) type = true;
		
		return type;
	}
	private boolean isDigit(char a)
	{
		boolean type = false;
		if((a >= '0') && (a <= '9') )type = true;
		
		return type;
	}
	@Override
	public void paintComponent(Graphics g) 
    {
		ImageIcon ic = new ImageIcon(getClass().getResource("background.png"));
		g.drawImage(ic.getImage(), 0, 0, getWidth(), getHeight(), this);
    }

	@Override
	public void mouseClicked(MouseEvent arg0) {
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
		//System.out.println("arg0.getSource() == " + arg0.getSource());
		if(arg0.getSource() == createNew)
		{
			addPlayList(null);
		}
		
		if(arg0.getSource() == deleteList)
		{
			deletePlaylist();
		}
		if(arg0.getSource() == newWindow)
		{
			arg0.consume();
			if(control!= null)
			control.openPlayListWindow(getSelectedNode());
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	

}
