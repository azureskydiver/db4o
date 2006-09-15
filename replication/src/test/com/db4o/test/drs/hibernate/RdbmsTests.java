package com.db4o.test.drs.hibernate;

import com.db4o.test.replication.db4ounit.DrsTestSuite;
import com.db4o.test.replication.db4ounit.DrsTestSuiteBuilder;
import com.db4o.test.replication.db4ounit.fixtures.Db4oClientServerDrsFixture;

import db4ounit.TestRunner;

public class RdbmsTests extends DrsTestSuite {
	public static void main(String[] args) {
		new RdbmsTests().runHsqlHsql();
		new RdbmsTests().runHsqldb4oCS();
		//AllTests.main(null);
	}

	public void runHsqlHsql() {
		new TestRunner(
				new DrsTestSuiteBuilder(
						new HsqlFixture("hsql-a"),
						new HsqlFixture("hsql-b"),
						getClass()))
						.run();
	}
	
	public void runHsqldb4oCS() {
		new TestRunner(new DrsTestSuiteBuilder(
				new HsqlFixture("hsql-a"),
				new Db4oClientServerDrsFixture("db4o-cs-b", 4455), 
				getClass()))
				.run();
	}
}
