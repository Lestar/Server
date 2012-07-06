import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class ActionsHistoryImpl extends UnicastRemoteObject implements ActionsHistory
{
	private static final long serialVersionUID = 1L;
	
	private volatile ArrayList<actionText> actionsList;
	public static enum action {ADD, SUB};
	private int indexAction = 0;
	private String doc;
	private String Owner;
	private String ProjName;
	private String AccessPass;
	private int Users = 0;
	private SimpleFrame frame;
	public ArrayList<UpdateText> DocList = new ArrayList<UpdateText>();
	public ArrayList<String> UsersList = new ArrayList<String>();
	private TextServer server;
	
	ActionsHistoryImpl(SimpleFrame frame)  throws RemoteException
	{
		
		try 
		{
			server = new TextServer(0);
			Thread serverChatThread = new Thread(server);
			serverChatThread.setDaemon(true);
			serverChatThread.start();
		} 
		catch (IOException e) 
		{
			JOptionPane.showMessageDialog(frame, "Runtime error");
		}
		this.frame = frame;
		actionsList = new ArrayList<actionText>();
	}
	public synchronized ActionData newPutString(String str, int position, action type, ActionData data, String login) throws RemoteException
	{
		if(indexAction > 0)
		{
			while(actionsList.size() != indexAction)
			{
				actionsList.remove(actionsList.size() - 1);
			}
		}
		
		actionsList.add(new actionText(str, position, type));
		setIndexAction(indexAction + 1);
		doc = data.getPanelText();
		if(type == action.ADD)
		{
			for(int i = 0; i < UsersList.size(); i++)
			{
				if(!UsersList.get(i).equals(login))
				{
				DocList.get(i).UpdateData(doc, position, str.length());
				}
			}
		}
		if(type == action.SUB)
		{
			for(int i = 0; i < UsersList.size(); i++)
			{
				if(!UsersList.get(i).equals(login))
				{
				DocList.get(i).UpdateData(doc, position, -str.length());
				}
			}
		}
		return new ActionData(data.getCaretPosition(), data.getPanelText(), true);
	}
	public synchronized ActionData newGetString(ActionData data, String login) throws RemoteException
	{
		if (indexAction <= 0) return data;
		actionText a = actionsList.get(indexAction-1);
		String temp = data.getPanelText();
		indexAction--;
		data.setChanged(true);
		if(a.actionType == action.ADD)
		{
			if(a.position == 0)
			{
				data.setPanelText(temp.substring(a.str.length(), temp.length()));
				data.setCaretPosition(a.position);
				doc = data.getPanelText();
				for(int i = 0; i < UsersList.size(); i++)
				{
					if(!UsersList.get(i).equals(login))
					{
						DocList.get(i).UpdateData(doc, a.position, -a.str.length());
					}
				}
				return data;
			}
			if(a.position > 0 && a.position < temp.length())
			{
				data.setPanelText(temp.substring(0, a.position) + temp.substring(a.position + a.str.length(), temp.length()));
				data.setCaretPosition(a.position);
				doc = data.getPanelText();
				for(int i = 0; i < UsersList.size(); i++)
				{
					if(!UsersList.get(i).equals(login))
					{
						DocList.get(i).UpdateData(doc, a.position, -a.str.length());
					}
				}
				return data;
			}
			if(a.position == temp.length())
			{
				data.setPanelText(temp.substring(0, a.position));
				data.setCaretPosition(a.position);
				doc = data.getPanelText();
				for(int i = 0; i < UsersList.size(); i++)
				{
					if(!UsersList.get(i).equals(login))
					{
						DocList.get(i).UpdateData(doc, a.position, -a.str.length());
					}
				}
				return data;
			}
		}
		if(a.actionType == action.SUB)
		{
			if(a.position == 0)
			{
				data.setPanelText(a.str + temp);
				data.setCaretPosition(a.position + a.str.length());
				doc = data.getPanelText();
				for(int i = 0; i < UsersList.size(); i++)
				{
					if(!UsersList.get(i).equals(login))
					{
						DocList.get(i).UpdateData(doc, a.position, a.str.length());
					}
				}
				return data;
			}
			if(a.position > 0 && a.position < temp.length())
			{
				data.setPanelText(temp.substring(0, a.position) + a.str + temp.substring(a.position, temp.length()));
				data.setCaretPosition(a.position + a.str.length());
				doc = data.getPanelText();
				for(int i = 0; i < UsersList.size(); i++)
				{
					if(!UsersList.get(i).equals(login))
					{
						DocList.get(i).UpdateData(doc, a.position, a.str.length());
					}
				}
				return data;
			}
			if(a.position == temp.length())
			{
				data.setPanelText(temp + a.str);
				data.setCaretPosition(a.position + a.str.length());
				doc = data.getPanelText();
				for(int i = 0; i < UsersList.size(); i++)
				{
					if(!UsersList.get(i).equals(login))
					{
						DocList.get(i).UpdateData(doc, a.position, a.str.length());
					}
				}
				return data;
			}
		}
		return data;
	}
	public synchronized ActionData newRestoreString(ActionData data, String login) throws RemoteException
	{
		if(indexAction > actionsList.size()-1) return data;
		actionText a = actionsList.get(indexAction);
		String temp = data.getPanelText();
		indexAction++;
		data.setChanged(true);
		if(a.actionType == action.ADD)
		{
			if(a.position == 0)
			{
				data.setPanelText(a.str + temp);
				data.setCaretPosition(a.position + a.str.length());
				doc = data.getPanelText();
				for(int i = 0; i < UsersList.size(); i++)
				{
					if(!UsersList.get(i).equals(login))
					{
						DocList.get(i).UpdateData(doc, a.position, a.str.length());
					}
				}
				return data;
			}
			if(a.position > 0 && a.position < temp.length())
			{
				data.setPanelText(temp.substring(0, a.position));
				data.setCaretPosition(a.position + a.str.length());
				doc = data.getPanelText();
				for(int i = 0; i < UsersList.size(); i++)
				{
					if(!UsersList.get(i).equals(login))
					{
						DocList.get(i).UpdateData(doc, a.position, a.str.length());
					}
				}
				return data;
			}
			if(a.position == temp.length())
			{
				data.setPanelText(temp+a.str);
				data.setCaretPosition(a.position + a.str.length());
				doc = data.getPanelText();
				for(int i = 0; i < UsersList.size(); i++)
				{
					if(!UsersList.get(i).equals(login))
					{
						DocList.get(i).UpdateData(doc, a.position, a.str.length());
					}
				}
				return data;
			}
		}
		if(a.actionType == action.SUB)
		{
			if(a.position == 0)
			{
				data.setPanelText(temp.substring(a.str.length(), temp.length()));
				data.setCaretPosition(a.position);
				doc = data.getPanelText();
				for(int i = 0; i < UsersList.size(); i++)
				{
					if(!UsersList.get(i).equals(login))
					{
						DocList.get(i).UpdateData(doc, a.position, -a.str.length());
					}
				}
				return data;
			}
			if(a.position > 0 && a.position < temp.length())
			{
				data.setPanelText(temp.substring(0, a.position) + temp.substring(a.position + a.str.length(), temp.length()));
				data.setCaretPosition(a.position);
				doc = data.getPanelText();
				for(int i = 0; i < UsersList.size(); i++)
				{
					if(!UsersList.get(i).equals(login))
					{
						DocList.get(i).UpdateData(doc, a.position, -a.str.length());
					}
				}
				return data;
			}
			if(a.position == temp.length())
			{
				data.setPanelText(temp.substring(0, a.position));
				data.setCaretPosition(a.position);
				doc = data.getPanelText();
				for(int i = 0; i < UsersList.size(); i++)
				{
					if(!UsersList.get(i).equals(login))
					{
						DocList.get(i).UpdateData(doc, a.position, -a.str.length());
					}
				}
				return data;
			}
		}
		return data;
	}
	public synchronized ArrayList<actionText> getActionList()
	{
		return actionsList;
	}
	public synchronized int getIndexAction() throws RemoteException
	{
		return indexAction;
	}
	public synchronized void setIndexAction(int indexAction) 
	{
		this.indexAction = indexAction;
	}
	class actionText
	{
		String str;
		int position;
		action actionType;
		public actionText(String text, int pos, action type)
		{
			str = text;
			position = pos;
			actionType = type;
		}
	}
	public synchronized void Disconnect(String projName, String login) throws RemoteException
	{
		for(int i = 0; i < frame.getAllThreads().size(); i++)
		{
			if(frame.getAllThreads().get(i).getProjName().equals(projName))
			{
				frame.getAllThreads().get(i).setUsers(-1);
				for(int j = 0; j < frame.getLoggedList().size(); j++)
				{
					if(frame.getLoggedList().get(j).equals(login))
					{
						frame.getLoggedList().remove(j);
					}
				}
				for(int j = 0; j < UsersList.size(); j++)
				{
					if(UsersList.get(j).equals(login))
					{
						DocList.remove(j);
						UsersList.remove(j);
					}
				}
			}
			if(frame.getAllThreads().get(i).getUsers() == 0)
			{
				frame.getAllThreads().remove(i);
				frame.getProList().remove(i);
				return;
			}
		}
	}
	public synchronized boolean isEmpty() throws RemoteException
	{
		return actionsList.isEmpty();
	}
	public synchronized int getSize() throws RemoteException 
	{
		return actionsList.size();
	}
	public synchronized String setStr()
	{
		return doc;
	}
	public synchronized String getOwner() 
	{
		return Owner;
	}
	public synchronized void setOwner(String owner) 
	{
		Owner = owner;
	}
	public synchronized String getProjName() 
	{
		return ProjName;
	}
	public synchronized void setProjName(String projName) 
	{
		ProjName = projName;
	}
	public synchronized String getAccessPass() 
	{
		return AccessPass;
	}
	public void setAccessPass(String accessPass)
	{
		AccessPass = accessPass;
	}
	public synchronized int getUsers()
	{
		return Users;
	}
	public synchronized void setUsers(int user)
	{
		Users += user;
	}
	public synchronized void addDocList(UpdateText text) throws RemoteException
	{
		DocList.add(text);
	}
	public synchronized void addUserList (String login) throws RemoteException
	{
		UsersList.add(login);
	}
	public synchronized String ServerIP () throws RemoteException
	{
		try 
		{
			return InetAddress.getLocalHost().getHostAddress();
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	public synchronized int ServerPort() throws RemoteException
	{
		return server.getSs().getLocalPort();
	}
	public TextServer getServer() 
	{
		return server;
	}
	public void setServer(TextServer server)
	{
		this.server = server;
	}
	public ArrayList<UpdateText> getDocList()
	{
		return DocList;
	}
}