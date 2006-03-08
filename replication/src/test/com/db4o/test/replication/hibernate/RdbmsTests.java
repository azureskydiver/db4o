package com.db4o.test.replication.hibernate;

import com.db4o.test.AllTests;
import com.db4o.test.TestSuite;
import com.db4o.test.replication.hibernate.ref_as_columns.mysql.MySQLTestSuite;
import com.db4o.test.replication.hibernate.ref_as_columns.postgresql.PostgreSQLTestSuite;

public class RdbmsTests extends AllTests {

	public static void main(String[] args) {
		new RdbmsTests().run();
	}

	protected void addTestSuites(TestSuite suites) {
		CLIENT_SERVER = false;
		suites.add(new MySQLTestSuite());
		suites.add(new PostgreSQLTestSuite());
		//suites.add(new OracleTestSuite());
	}
}
