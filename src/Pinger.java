import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;


public class Pinger implements Runnable
{
	private ArrayList<ActionsHistoryImpl> AllThreads;
	private ArrayList<String> LoggedList;
	private DefaultListModel<String> ProList;
	
	Pinger(ArrayList<ActionsHistoryImpl> AllThreads, ArrayList<String> LoggedList, DefaultListModel<String> ProList)
	{
		this.AllThreads = AllThreads;
		this.LoggedList = LoggedList;
		this.ProList = ProList;
	}

	public void run() 
	{
		for(int i = 0; i < AllThreads.size(); i++)
		{
			for(int j = 0; j < AllThreads.get(i).getDocList().size(); j++)
			{
				try 
				{
					AllThreads.get(i).getDocList().get(j).Ping(22);
				} 
				catch (RemoteException e) 
				{
					String log = AllThreads.get(i).UsersList.get(j);
					AllThreads.get(i).getDocList().remove(j);
					AllThreads.get(i).UsersList.remove(j);
					AllThreads.get(i).setUsers(-1);
					for(int k = 0; k < LoggedList.size(); k++)
					{
						if(LoggedList.get(k).equals(log)) LoggedList.remove(k);
					}
				}
			}
			/*if(AllThreads.get(i).getDocList().size() == 0)
			{
				AllThreads.remove(i);
				ProList.remove(i);
			}*/
		}
		for(int i = 0; i < AllThreads.size(); i++)
		{
			if(AllThreads.get(i).getUsers() == 0)
			{
				AllThreads.remove(i);
				ProList.remove(i);
				i--;
			}
		}
	}
}
