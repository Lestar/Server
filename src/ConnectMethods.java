import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface ConnectMethods  extends Remote
{
	public static enum LogInfo {INCORRECT_LOGGIN, INCORRECT_PASS, CORRECT_DATA, ALREADY_LOGGED};
	public LogInfo Authenticate(String login, String pass) throws RemoteException;
	public ActionsHistory NewThread(String login, String pass, String projName) throws RemoteException;
	public boolean Registrate(String login, String pass) throws RemoteException;
	public ArrayList<String> getProjectList() throws RemoteException;
	public ActionsHistory ConfirmPass(int index, String pass) throws RemoteException;
}
