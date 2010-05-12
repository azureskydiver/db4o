/* Copyright (C) 2004 - 2008  Versant Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
package com.db4o.drs.test.hibernate;

import com.db4o.drs.test.Db4oClientServerDrsFixture;
import com.db4o.drs.test.DrsTestSuite;
import com.db4o.drs.test.DrsTestSuiteBuilder;

import db4ounit.ConsoleTestRunner;

public class RdbmsTests extends DrsTestSuite {
	
	public static void main(String[] args) {
		new RdbmsTests().run();
	}
	
	public int run(){
		
		int failureCount = 0;
		
		/*
		 * The db4ounit forbids the reuse of provider by design Do not run two
		 * HSql combinations at the same time, otherwise out of memory.
		 */

//		failureCount += new RdbmsTests().runHsqlHsql();
		
		failureCount += new RdbmsTests().runHsqldb4oCS();
		
//		failureCount += new RdbmsTests().runOracledb4oCS();
//		failureCount += new RdbmsTests().runMySQLdb4oCS();
//		failureCount += new RdbmsTests().runPostgreSQLdb4oCS();
//		failureCount += new RdbmsTests().runMsSqldb4oCS();
//		failureCount += new RdbmsTests().runDb2db4oCS();
//		failureCount += new RdbmsTests().runDerbydb4oCS();
		
		return failureCount;
	}

	public int runHsqlHsql() {
		return new ConsoleTestRunner(new DrsTestSuiteBuilder(new HsqlMemoryFixture("hsql-a"),
				new HsqlMemoryFixture("hsql-b"), getClass())).run();
	}

	public int runHsqldb4oCS() {
		return new ConsoleTestRunner(new DrsTestSuiteBuilder(new HsqlMemoryFixture("hsql-a"),
				new Db4oClientServerDrsFixture("db4o-cs-b", 9587), getClass()))
				.run();
	}

	public int runOracledb4oCS() {
		return new ConsoleTestRunner(new DrsTestSuiteBuilder(new OracleFixture("Oracle-a"),
				new Db4oClientServerDrsFixture("db4o-cs-b", 9587), getClass()))
				.run();
	}

	public int runMySQLdb4oCS() {
		return new ConsoleTestRunner(new DrsTestSuiteBuilder(new MySQLFixture("MySQL-a"),
				new Db4oClientServerDrsFixture("db4o-cs-b", 9587), getClass()))
				.run();
	}

	public int runPostgreSQLdb4oCS() {
		return new ConsoleTestRunner(new DrsTestSuiteBuilder(new PostgreSQLFixture(
				"PostgreSQL-a"), new Db4oClientServerDrsFixture("db4o-cs-b",
				9587), getClass())).run();
	}

	public int runMsSqldb4oCS() {
		return new ConsoleTestRunner(new DrsTestSuiteBuilder(new MsSqlFixture("MsSql"),
				new Db4oClientServerDrsFixture("db4o-cs-b", 9587), getClass()))
				.run();
	}
	
	public int runDb2db4oCS() {
		return new ConsoleTestRunner(new DrsTestSuiteBuilder(new Db2Fixture("Db2"),
				new Db4oClientServerDrsFixture("db4o-cs-b", 9587), getClass()))
				.run();
	}
	
	public int runDerbydb4oCS() {
		return new ConsoleTestRunner(new DrsTestSuiteBuilder(new DerbyFixture("Derby"),
				new Db4oClientServerDrsFixture("db4o-cs-b", 9587), getClass()))
				.run();
	}

	protected Class[] specificTestCases() {
		return new Class[] {
			TablesCreatorTest.class,
			ReplicationConfiguratorTest.class,
			RoundRobinWithManyProviders.class,
		};
	}
}
