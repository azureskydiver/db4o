package com.db4o.drs.test.hibernate;

import java.util.HashSet;
import java.util.Set;

import com.db4o.drs.test.Db4oClientServerDrsFixture;
import com.db4o.drs.test.DrsTestSuite;
import com.db4o.drs.test.DrsTestSuiteBuilder;
import com.db4o.drs.test.ListTest;
import com.db4o.drs.test.MapTest;
import com.db4o.drs.test.ReplicationFeaturesMain;
import com.db4o.drs.test.TheSimplest;

import db4ounit.TestRunner;

public class RdbmsTests extends DrsTestSuite {
	public static void main(String[] args) {
		/*
		 * The db4ounit forbids the reuse of provider by design Do not run two
		 * HSql combinations at the same time, otherwise out of memory.
		 * 
		 */

//		new RdbmsTests().runHsqlHsql();
		new RdbmsTests().runHsqldb4oCS();
//		new RdbmsTests().runOracledb4oCS();
//		new RdbmsTests().runMySQLdb4oCS();
//		new RdbmsTests().runPostgreSQLdb4oCS();
		//new RdbmsTests().runMsSqldb4oCS();
//		new RdbmsTests().runDb2db4oCS();
		//new RdbmsTests().runDerbydb4oCS();
	}

	public void runHsqlHsql() {
		new TestRunner(new DrsTestSuiteBuilder(new HsqlMemoryFixture("hsql-a"),
				new HsqlMemoryFixture("hsql-b"), getClass())).run();
	}

	public void runHsqldb4oCS() {
		new TestRunner(new DrsTestSuiteBuilder(new HsqlMemoryFixture("hsql-a"),
				new Db4oClientServerDrsFixture("db4o-cs-b", 9587), getClass()))
				.run();
	}

	public void runOracledb4oCS() {
		new TestRunner(new DrsTestSuiteBuilder(new OracleFixture("Oracle-a"),
				new Db4oClientServerDrsFixture("db4o-cs-b", 9587), getClass()))
				.run();
	}

	public void runMySQLdb4oCS() {
		new TestRunner(new DrsTestSuiteBuilder(new MySQLFixture("MySQL-a"),
				new Db4oClientServerDrsFixture("db4o-cs-b", 9587), getClass()))
				.run();
	}

	public void runPostgreSQLdb4oCS() {
		new TestRunner(new DrsTestSuiteBuilder(new PostgreSQLFixture(
				"PostgreSQL-a"), new Db4oClientServerDrsFixture("db4o-cs-b",
				9587), getClass())).run();
	}

	public void runMsSqldb4oCS() {
		new TestRunner(new DrsTestSuiteBuilder(new MsSqlFixture("MsSql"),
				new Db4oClientServerDrsFixture("db4o-cs-b", 9587), getClass()))
				.run();
	}
	
	public void runDb2db4oCS() {
		new TestRunner(new DrsTestSuiteBuilder(new Db2Fixture("Db2"),
				new Db4oClientServerDrsFixture("db4o-cs-b", 9587), getClass()))
				.run();
	}
	
	public void runDerbydb4oCS() {
		new TestRunner(new DrsTestSuiteBuilder(new DerbyFixture("Derby"),
				new Db4oClientServerDrsFixture("db4o-cs-b", 9587), getClass()))
				.run();
	}

	protected Class[] testCases() {
		return all();
	}

	private Class[] all() {
		Set<Class> out = new HashSet<Class>();

		out.add(TablesCreatorTest.class);
		out.add(ReplicationConfiguratorTest.class);
		out.add(RoundRobinWithManyProviders.class);

		for (Class c : shared())
			out.add(c);

		return out.toArray(new Class[] {});
	}

	protected Class[] one() {
		return new Class[] {
		RoundRobinWithManyProviders.class,
		 //TheSimplest.class
		};
	}
}
