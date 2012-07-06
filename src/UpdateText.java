import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UpdateText extends Remote 
{	
	public void UpdateData(String text, int position, int lenght) throws RemoteException;
	public int Ping(int data) throws RemoteException;
}

