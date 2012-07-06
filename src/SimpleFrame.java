import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;


public class SimpleFrame extends JFrame
{
	private static final long serialVersionUID = 1L;

	private Box vbox1;
	private JLabel ProjList;
	private JList<String> PList;
	private Box vbox2;
	private JLabel UsersList;
	private JEditorPane UList;
	private JScrollPane ScrollUList;
	private Box vbox3;
	private JLabel Time;
	private JLabel TimeText;
	private JLabel Chat;
	private JEditorPane ChatFrame;
	private JScrollPane ScrollChat;
	private JLabel Message;
	private JEditorPane MessageFrame;
	private JScrollPane ScrollMessage;
	private Box hbox1;
	private Box hbox2;
	private JLabel SumInfo;
	private JLabel Info;
	private JButton GetSummary;
	private JButton Exit;
	private Box vbox;
	private Container contentPane;
	private String DataBase = "Accounts.dat";
	private ConnectionImpl history; 
	private ArrayList<ActionsHistoryImpl> AllThreads;
	private ArrayList<String> LoggedList;
	private DefaultListModel<String> ProList;
	private int days = 0;
	private int hours = 0;
	private int minutes = 0;
	private Timer timer;
	private Timer pinger;
	
	
	SimpleFrame()
    {
		this.addWindowListener(new WindowListener()
		{
			public void windowActivated(WindowEvent arg0){}
			public void windowClosed(WindowEvent arg0) {}
			public void windowClosing(WindowEvent arg0) 
			{
				for(int i = 0; i < AllThreads.size(); i++)
				{
					AllThreads.get(i).getServer().shutdownServer();
				}
				history.DB.Disconnect();
			}
			public void windowDeactivated(WindowEvent arg0)	{}
			public void windowDeiconified(WindowEvent arg0)	{}
			public void windowIconified(WindowEvent arg0) {}
			public void windowOpened(WindowEvent arg0) {}
		});
		ProList = new DefaultListModel<String>();
		LoggedList = new ArrayList<String>();
		setSize(700,510);
		this.setLocation(200, 200);
		setTitle("Text Editor Server");
		setLayout(new BorderLayout());
		AllThreads = new ArrayList<ActionsHistoryImpl>();
		
		vbox1 = Box.createVerticalBox();
		ProjList = new JLabel("Projects List");
		ProjList.setAlignmentX(CENTER_ALIGNMENT);
		PList = new JList<String>(ProList);
		PList.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent arg0)
			{
				if(arg0.getClickCount() == 2)
				{
					ChatFrame.setText(AllThreads.get(PList.getSelectedIndex()).getServer().getChatText());
					UList.setText(null);
					ArrayList<String> list = AllThreads.get(PList.getSelectedIndex()).UsersList;
					if(list.size() == 0) return;
					for(int i = 0; i < list.size(); i++)
					{
						UList.setText(UList.getText() + list.get(i) + "\n");
					}
				}
			}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {}
			
		});
		PList.setAlignmentX(CENTER_ALIGNMENT);
		PList.setMinimumSize(new Dimension(200, 350));
		PList.setMaximumSize(new Dimension(200, 350));
		PList.setPreferredSize(new Dimension(200, 350));
		PList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		PList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		
		vbox1.add(ProjList);
		vbox1.add(Box.createVerticalStrut(10));
		vbox1.add(PList);
		vbox1.add(Box.createVerticalStrut(10));
		
		vbox2 = Box.createVerticalBox();
		UsersList = new JLabel("Users List");
		UsersList.setAlignmentX(CENTER_ALIGNMENT);
		UList = new JEditorPane();
		UList.setEditable(false);
		ScrollUList = new JScrollPane(UList);
		ScrollUList.setAlignmentX(CENTER_ALIGNMENT);
		ScrollUList.setMinimumSize(new Dimension(200, 350));
		ScrollUList.setMaximumSize(new Dimension(200, 350));
		ScrollUList.setPreferredSize(new Dimension(200, 350));
		
		vbox2.add(UsersList);
		vbox2.add(Box.createVerticalStrut(10));
		vbox2.add(ScrollUList);
		vbox2.add(Box.createVerticalStrut(10));
				
		vbox3 = Box.createVerticalBox();
		Time = new JLabel("Runtime");
		Font font = Time.getFont();
		font.deriveFont(font.getSize2D()*2);
		font.deriveFont(Font.BOLD);
		Time.setFont(font);
		Time.setAlignmentX(CENTER_ALIGNMENT);
		TimeText = new JLabel("0 days 00 hours 00 minutes");
		TimeText.setFont(font);
		TimeText.setAlignmentX(CENTER_ALIGNMENT);
		Chat = new JLabel("Chat");
		Chat.setAlignmentX(CENTER_ALIGNMENT);
		ChatFrame = new JEditorPane();
		ChatFrame.setEditable(false);
		ScrollChat = new JScrollPane(ChatFrame);
		ScrollChat.setMinimumSize(new Dimension(200, 200));
		Message = new JLabel("Message");
		Message.setAlignmentX(CENTER_ALIGNMENT);
		MessageFrame = new JEditorPane();
		ScrollMessage = new JScrollPane(MessageFrame);
		ScrollMessage.setMinimumSize(new Dimension(200, 100));
		MessageFrame.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent arg0) 
			{
				if(arg0.getKeyChar() == KeyEvent.VK_ENTER)
				{
					int index = PList.getSelectedIndex();
					if(index > -1)
					{
						ActionsHistoryImpl ptr = AllThreads.get(index);
						ptr.getServer().SendToAll("Admin: " + MessageFrame.getText());
						ptr.getServer().setChatText(ptr.getServer().getChatText() + "Admin: " + MessageFrame.getText() + "\n");
						ChatFrame.setText(ptr.getServer().getChatText());
					}
				}
			}
			public void keyReleased(KeyEvent arg0) 
			{
				if(arg0.getKeyChar() == KeyEvent.VK_ENTER)
					MessageFrame.setText(null);
			}
			public void keyTyped(KeyEvent arg0) {}
			
		});
		vbox3.add(Box.createVerticalStrut(10));
		vbox3.add(Time);
		vbox3.add(Box.createVerticalStrut(10));
		vbox3.add(TimeText);
		vbox3.add(Box.createVerticalStrut(30));
		vbox3.add(Chat);
		vbox3.add(Box.createVerticalStrut(10));
		vbox3.add(ScrollChat);
		vbox3.add(Box.createVerticalStrut(10));
		vbox3.add(Message);
		vbox3.add(Box.createVerticalStrut(10));
		vbox3.add(ScrollMessage);
		vbox3.add(Box.createVerticalStrut(30));
		
		hbox1 = Box.createHorizontalBox();
		hbox1.add(Box.createHorizontalStrut(20));
		hbox1.add(vbox1);
		hbox1.add(Box.createHorizontalStrut(20));
		hbox1.add(vbox2);
		hbox1.add(Box.createHorizontalStrut(20));
		hbox1.add(vbox3);
		hbox1.add(Box.createHorizontalStrut(20));
		
		hbox2 = Box.createHorizontalBox();
		SumInfo = new JLabel("Summary info");
		Info = new JLabel("0 projects, 0 users");
		GetSummary = new JButton("Get summary info");
		GetSummary.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				int users = 0;
				int projects = 0;
				for(int i = 0; i < AllThreads.size();i++)
				{
					users += AllThreads.get(i).getUsers();
				}
				projects = AllThreads.size();
				
				Info.setText(projects + " projects, " + users + " users");
			}
			
		});
		Exit = new JButton("Exit");
		Exit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				System.exit(0);
			}
		});
		Exit.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent arg0)
			{
				System.exit(0);
			}
			public void keyReleased(KeyEvent arg0) {}
			public void keyTyped(KeyEvent arg0) {}
		});
		hbox2.add(SumInfo);
		hbox2.add(Box.createHorizontalStrut(20));
		hbox2.add(Info);
		hbox2.add(Box.createHorizontalStrut(20));
		hbox2.add(GetSummary);
		hbox2.add(Box.createHorizontalStrut(20));
		hbox2.add(Exit);
		
		vbox = Box.createVerticalBox();
		vbox.add(hbox1);
		vbox.add(hbox2);
		vbox.add(Box.createVerticalStrut(10));
		contentPane = getContentPane();
	    contentPane.add(vbox, BorderLayout.CENTER);
	    this.setResizable(false);
		
	    timer = new Timer(60000, new ActionListener()
	    {
			public void actionPerformed(ActionEvent e) 
			{
				minutes++;
				if(minutes == 60)
				{
					hours++;
					minutes = 0;
					if(hours == 24)
					{
						hours = 0;
						days++;
					}
				}
				TimeText.setText(days + " days " + hours +" hours " + minutes + " minutes");
			}
	    	
	    });
	    timer.start();
	    pinger = new Timer(1000, new ActionListener()
	    {
	    	Pinger ping = new Pinger(AllThreads, LoggedList, ProList);
	    	
			public void actionPerformed(ActionEvent arg0) 
			{
				ping.run();
			}
	    });
	    pinger.start();
		try 
		{
			@SuppressWarnings("unused")
			Registry stReg = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
			history = new ConnectionImpl(this);
			Naming.rebind("history", history);
		} 
		catch (RemoteException e) 
		{
			JOptionPane.showMessageDialog(this, "RMI Registry error");
		} 
		catch (MalformedURLException e) 
		{
			JOptionPane.showMessageDialog(this, "RMI Registry error");
			System.exit(0);
		}
    }

	public String getDataBase() 
	{
		return DataBase;
	}
	
	public ArrayList<ActionsHistoryImpl> getAllThreads()
	{
		return AllThreads;
	}

	public ArrayList<String> getLoggedList() 
	{
		return LoggedList;
	}

	public void setLoggedList(ArrayList<String> loggedList) 
	{
		LoggedList = loggedList;
	}

	public DefaultListModel<String> getProList() 
	{
		return ProList;
	}

	public void setProList(DefaultListModel<String> proList) 
	{
		ProList = proList;
	}
	
}