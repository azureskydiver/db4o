package com.db4o.test.replication.hibernate;

import com.db4o.test.AllTests;
import com.db4o.test.TestSuite;
import com.db4o.test.replication.db4o.Db4oReplicationTestUtil;
import com.db4o.test.replication.hibernate.postgresql.PostgreSQLTestSuite;

public class RdbmsTests extends AllTests {
	protected void addTestSuites(TestSuite suites) {
		CLIENT_SERVER = false;
		//suites.add(new MySQLTestSuite());
		//suites.add(new PostgreSQLTestSuite());
		suites.add(new PostgreSQLTestSuite());
	}

// --------------------------- main() method ---------------------------

	public static void main(String[] args) {
		Db4oReplicationTestUtil.configure();
		new RdbmsTests().run();
	}
}
