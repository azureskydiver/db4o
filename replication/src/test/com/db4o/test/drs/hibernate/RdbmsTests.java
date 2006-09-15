package com.db4o.test.drs.hibernate;

import com.db4o.test.drs.Db4oTests;
import com.db4o.test.drs.Db4oClientServerDrsFixture;
import com.db4o.test.drs.DrsTestSuite;
import com.db4o.test.drs.DrsTestSuiteBuilder;

import db4ounit.TestRunner;

public class RdbmsTests extends DrsTestSuite {
	public static void main(String[] args) {
		//new RdbmsTests().runHsqlHsql();
		//new RdbmsTests().runHsqldb4oCS();
		Db4oTests.main(null);
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
