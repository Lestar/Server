import java.rmi.Remote;
import java.rmi.RemoteException;


public interface ActionsHistory extends Remote 
{
	public ActionData newPutString(String str, int position, ActionsHistoryImpl.action type, ActionData data, String login) throws RemoteException;
	public ActionData newGetString(ActionData data, String login) throws RemoteException;
	public ActionData newRestoreString(ActionData data, String login) throws RemoteException;
	public boolean isEmpty() throws RemoteException;
	public int getIndexAction() throws RemoteException;
	public int getSize() throws RemoteException;
	public String setStr() throws RemoteException;
	public void addDocList(UpdateText text) throws RemoteException;
	public void addUserList (String login) throws RemoteException;
	public String ServerIP () throws RemoteException;
	public int ServerPort() throws RemoteException;
	public void Disconnect(String projName, String login) throws RemoteException;
}
