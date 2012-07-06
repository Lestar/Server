import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;


public class SQL 
{
	private String DBDriver = "com.mckoi.JDBCDriver";
	private String UserName = "User";
	private String Password = "123123";
	private Connection conn = null;
	private Statement statement;
		
	public boolean Connect()
	{
		boolean isConnected = false;
		try 
		{
			Class.forName(DBDriver).newInstance();
			conn = (Connection)DriverManager.getConnection("jdbc:mckoi:local://ExampleDB.conf?create=false", UserName, Password);
			isConnected = true;
			//createTable();
		} 
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		} 
		catch (InstantiationException e) 
		{
			e.printStackTrace();
		} 
		catch (IllegalAccessException e) 
		{
			e.printStackTrace();
		}
		return isConnected;
	}
	public void Disconnect()
	{
		try 
		{
			conn.close();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
	}
	public Connection getConn() 
	{
		return conn;
	}
	public void setConn(Connection conn) 
	{
		this.conn = conn;
	}
	public void createTable()
	{
		Date d = new Date(System.currentTimeMillis());
		ResultSet result;
		try 
		{
			statement = conn.createStatement();
			statement.executeQuery(
					  "    CREATE TABLE Users ( " +
					  "       name      	VARCHAR(100) NOT NULL, " +
					  "       pass      	VARCHAR(100) NOT NULL, " + 
					  "       reg_date      DATE,                   " +
					  "       online_date   DATE                   ) ");
			statement.executeQuery(
					  "    INSERT INTO Users ( name, pass, reg_date, online_date) VALUES " +
					  "      ( 'Lestar', 'starsong', '" + d +"', '" + d +"') ");
			result = statement.executeQuery("SELECT * FROM Users WHERE name = 'Lestar'");
			if(result.next()) 
				System.out.println("  " + result.getString(1) + " " + result.getString(2) + " " + result.getDate(3) + " " + result.getDate(4));
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}