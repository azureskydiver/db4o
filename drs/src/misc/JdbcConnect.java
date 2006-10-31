import java.sql.Connection;
import java.sql.DriverManager;

public class JdbcConnect {
	public static void main(String[] args) {
		Connection conn = null;

		try {
			conn = derby();
			System.out.println("Database connection established");
			conn.close();
			System.out.println("Database connection terminated");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static Connection oracle() throws Exception {
		Connection conn;
		String userName = "db4o";
		String password = "db4o";
		String url = "jdbc:oracle:thin:@192.168.1.176:1521:XE";
		Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
		conn = DriverManager.getConnection(url, userName, password);
		return conn;
	}

	static Connection mssql() throws Exception {
		Connection conn;
		String userName = "db4o";
		String password = "db4o";
		String url = "jdbc:sqlserver://192.168.1.176:1089;databaseName=drs";
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
				.newInstance();
		conn = DriverManager.getConnection(url, userName, password);
		return conn;
	}

	static Connection db2() throws Exception {
		Connection conn;
		String userName = "db4o";
		String password = "db4o";
		String url = "jdbc:db2://192.168.1.176:50000/db4o";
		Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
		conn = DriverManager.getConnection(url, userName, password);
		return conn;
	}
	
	static Connection derby() throws Exception {
		Connection conn;
		String userName = "db4o";
		String password = "db4o";
		String url = "jdbc:derby:./derby-drs;create=true";
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
		conn = DriverManager.getConnection(url, userName, password);
		return conn;
	}
}
