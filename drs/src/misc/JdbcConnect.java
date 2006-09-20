import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class JdbcConnect {
	public static void main(String[] args) {
		Connection conn = null;

		try {
			String userName = "testa";
			String password = "testa";
			String url = "jdbc:oracle:thin:@localhost:1521:step";
			Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
			conn = DriverManager.getConnection(url, userName, password);
			System.out.println("Database connection established");

			final DatabaseMetaData metaData = conn.getMetaData();
			final ResultSet col = metaData.getColumns(null, null, "LISTCONTENT", "DRS_VERSION");
			while (col.next()) {
				final String TYPE_NAME = col.getString("TYPE_NAME");
				System.out.println("TYPE_NAME = " + TYPE_NAME);

				final String COLUMN_SIZE = col.getString("COLUMN_SIZE");
				System.out.println("COLUMN_SIZE = " + COLUMN_SIZE);

				final String DATA_TYPE = col.getString("DATA_TYPE");
				System.out.println("DATA_TYPE = " + DATA_TYPE);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (conn != null) {
				try {
					conn.close();
					System.out.println("Database connection terminated");
				}
				catch (Exception e) { /* ignore close errors */ }
			}
		}
	}
}
