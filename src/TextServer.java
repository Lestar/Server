
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JOptionPane;


public class TextServer implements Runnable
{
    private ServerSocket ss; 
    
	private Thread serverThread;
    private int port;
    private String ChatText = "";

    BlockingQueue<SocketProcessor> q = new LinkedBlockingQueue<SocketProcessor>();

    public TextServer(int port) throws IOException 
    {
        ss = new ServerSocket(port);
        this.port = ss.getLocalPort();
    }
    public void run() 
    {
        serverThread = Thread.currentThread();
        while (true) 
        {
            Socket s = getNewConn();
            if (serverThread.isInterrupted()) 
            { 
                break;

            }
            else if (s != null)
            { 
                try 
                {
                    final SocketProcessor processor = new SocketProcessor(s);
                    final Thread thread = new Thread(processor);
                    thread.setDaemon(true); 
                    thread.start(); 
                    q.offer(processor); 
                }
                catch (IOException ignored) {}  
            }
        }
   }
   private Socket getNewConn() 
   {
        Socket s = null;
        try 
        {
            s = ss.accept();

        } 
        catch (IOException e) 
        {
        	JOptionPane.showMessageDialog(null, this, "Chat server error", port);
            shutdownServer(); 
        }
        return s;
    }
    public synchronized void shutdownServer() 
    {
    	for (SocketProcessor s: q) 
    	{
    		s.close();
        }
        if (!ss.isClosed()) 
        {
            try 
            {
                ss.close();
            } 
            catch (IOException ignored) {}
        }
    }
    public void SendToAll(String line)
    {
    	for (SocketProcessor sp:q) 
    	{
            sp.send(line);
        }
    }
    public class SocketProcessor implements Runnable
    {
        Socket s; 
        BufferedReader br; 
        BufferedWriter bw;
        
        SocketProcessor(Socket socketParam) throws IOException
        {
            s = socketParam;
            br = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
            bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8") );
        }
        public void run() 
        {
            while (!s.isClosed()) 
            { 
                String line = null;
                try
                {
                    line = br.readLine();
                    setChatText(getChatText() + (line + "\n"));
                } 
                catch (IOException e) 
                {
                    close();
                }
                if (line == null) 
                { 
                    close(); 
                }
                else if ("shutdown".equals(line)) 
                {
                    serverThread.interrupt();
                    try 
                    {
                        new Socket("localhost", port); 
                    } 
                    catch (IOException ignored) 
                    { 

                    } 
                    finally 
                    {
                        shutdownServer(); 
                    }

                } 
                else 
                { 
                	for (SocketProcessor sp:q) 
                	{
                        sp.send(line);
                    }
                }
            }
        }
        public synchronized void send(String line)
        {
            try 
            {
                bw.write(line);
                bw.write("\n"); 
                bw.flush(); 
            } 
            catch (IOException e) 
            {
            	JOptionPane.showMessageDialog(null, this, "Chat server error", port);
                close();
            }
        }
        public synchronized void close() 
        {
            q.remove(this);
            if (!s.isClosed())
            {
                try 
                {
                    s.close(); 
                }
                catch (IOException ignored) {}
            }
        }
        protected void finalize() throws Throwable
        {
            super.finalize();
            close();
        }
    }
    public ServerSocket getSs() 
    {
		return ss;
	}
	public String getChatText() 
	{
		return ChatText;
	}
	public void setChatText(String chatText)
	{
		ChatText = chatText;
	}
 }


