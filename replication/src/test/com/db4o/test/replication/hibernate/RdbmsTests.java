package com.db4o.test.replication.hibernate;

import com.db4o.test.AllTests;
import com.db4o.test.TestSuite;
import com.db4o.test.replication.hibernate.postgresql.PostgreSQLTestSuite;

public class RdbmsTests extends AllTests {

	public static void main(String[] args) {
		new RdbmsTests().run();
	}

	protected void addTestSuites(TestSuite suites) {
		CLIENT_SERVER = false;
		suites.add(new PostgreSQLTestSuite());
	}
}
