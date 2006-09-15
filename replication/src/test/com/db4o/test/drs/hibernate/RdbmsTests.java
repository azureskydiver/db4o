package com.db4o.test.drs.hibernate;

import java.util.HashSet;
import java.util.Set;

import com.db4o.test.drs.Db4oClientServerDrsFixture;
import com.db4o.test.drs.DrsTestSuite;
import com.db4o.test.drs.DrsTestSuiteBuilder;
import com.db4o.test.drs.ReplicationProviderTest;

import db4ounit.TestRunner;

public class RdbmsTests extends DrsTestSuite {
	public static void main(String[] args) {
		/*
		 * The db4ounit forbids the reuse of provider by design
		 * Do not run two HSql combinations at the same time, otherwise out of memory.
		 *
		 */
		
		//new RdbmsTests().runHsqlHsql();
		new RdbmsTests().runHsqldb4oCS();
//		new RdbmsTests().runOracledb4oCS();
//		new RdbmsTests().runMySQLdb4oCS();
//		new RdbmsTests().runPostgreSQLdb4oCS();
	}

	public void runHsqlHsql() {
		new TestRunner(
				new DrsTestSuiteBuilder(
						new HsqlMemoryFixture("hsql-a"),
						new HsqlMemoryFixture("hsql-b"),
						getClass()))
						.run();
	}
	
	public void runHsqldb4oCS() {
		new TestRunner(new DrsTestSuiteBuilder(
				new HsqlMemoryFixture("hsql-a"),
				new Db4oClientServerDrsFixture("db4o-cs-b", 1234), 
				getClass()))
				.run();
	}
	
	public void runOracledb4oCS() {
		new TestRunner(new DrsTestSuiteBuilder(
				new OracleFixture("Oracle-a"),
				new Db4oClientServerDrsFixture("db4o-cs-b", 1234), 
				getClass()))
				.run();
	}
	
	public void runMySQLdb4oCS() {
		new TestRunner(new DrsTestSuiteBuilder(
				new MySQLFixture("MySQL-a"),
				new Db4oClientServerDrsFixture("db4o-cs-b", 1234), 
				getClass()))
				.run();
	}
	
	public void runPostgreSQLdb4oCS() {
		new TestRunner(new DrsTestSuiteBuilder(
				new PostgreSQLFixture("PostgreSQL-a"),
				new Db4oClientServerDrsFixture("db4o-cs-b", 1234), 
				getClass()))
				.run();
	}
	
	protected Class[] testCases() {
		Set<Class> out = new HashSet<Class>();
		
		out.add(TablesCreatorTest.class);
		out.add(ReplicationConfiguratorTest.class);
		
		for (Class c : all())
			out.add(c);
		
		return out.toArray(new Class[]{});
	}

	protected Class[] one() {
		return new Class[] { ReplicationProviderTest.class, };
	}
}
