import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class ConnectionImpl extends UnicastRemoteObject implements ConnectMethods
{
	private static final long serialVersionUID = 1L;
	SimpleFrame frame;
	SQL DB;
		

	ConnectionImpl(SimpleFrame frame) throws RemoteException 
	{
		this.frame = frame;
		DB = new SQL();
		DB.Connect();
	}
	public LogInfo Authenticate(String login, String pass) throws RemoteException
	{
		ResultSet result;
		try 
		{
			Statement statement = DB.getConn().createStatement();
			result = statement.executeQuery("SELECT * FROM Users WHERE name = '" + login + "'");
			if(!result.next())
			{
				return LogInfo.INCORRECT_LOGGIN;
			}
			else
			{
				if(!result.getString(2).equals(pass))
				{
					return LogInfo.INCORRECT_PASS;
				}
				for(int i = 0; i < frame.getLoggedList().size(); i++)
				{
					if(frame.getLoggedList().get(i).equals(login))
						return LogInfo.ALREADY_LOGGED;
				}
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		try 
		{
			Date d = new Date(System.currentTimeMillis());
			Statement statement = DB.getConn().createStatement();
			result = statement.executeQuery("UPDATE Users SET online_date = " + d + " WHERE name = '" + login + "'");
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return LogInfo.CORRECT_DATA;
	}
	public boolean Registrate(String login, String pass) throws RemoteException
	{
		Date d = new Date(System.currentTimeMillis());
		ResultSet result;
		try 
		{
			Statement statement = DB.getConn().createStatement();
			result = statement.executeQuery("SELECT * FROM Users WHERE name = '" + login + "'");
			if (result.next()) return false;
			else 
			{
				statement.executeQuery(" INSERT INTO Users ( name, pass, reg_date, online_date) " +
						"VALUES " + "( '" + login + "', '" + pass + "', '" + d + "', '" + d + "') ");
				return true;
			}
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		return false;
	}
	public ActionsHistory NewThread(String login, String pass ,String projName) throws RemoteException
	{
		ActionsHistoryImpl link = new ActionsHistoryImpl(frame);
		link.setOwner(login);
		link.setProjName(projName);
		link.setAccessPass(pass);
		link.setUsers(1);
		frame.getAllThreads().add(link);
		frame.getProList().addElement(projName);
		return link;
	}
	
	public ArrayList<String> getProjectList() throws RemoteException
	{
		ArrayList<String> list = new ArrayList<String>();
		for(int i = 0; i < frame.getAllThreads().size(); i++)
		{
			list.add("" + frame.getAllThreads().get(i).getProjName() + " : " + frame.getAllThreads().get(i).getOwner());
		}
		return list;
	}
	public ActionsHistory ConfirmPass(int index, String pass) throws RemoteException
	{
		if(frame.getAllThreads().get(index).getAccessPass().equals(pass))
		{
			frame.getAllThreads().get(index).setUsers(1);
			return frame.getAllThreads().get(index);
		}
		else
			return null;
	}
}
