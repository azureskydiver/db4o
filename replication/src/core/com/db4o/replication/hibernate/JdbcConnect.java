package com.db4o.replication.hibernate;

import java.sql.Connection;
import java.sql.DriverManager;

public class JdbcConnect {
	public static void main(String[] args) {
		Connection conn = null;

		try {
			String userName = "db4o";
			String password = "db4o";
			String url = "jdbc:oracle:thin:@localhost:1521:step";
			Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
			conn = DriverManager.getConnection(url, userName, password);
			System.out.println("Database connection established");
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
