package com.db4o.test.drs;

import com.db4o.test.replication.db4ounit.DrsTestSuite;
import com.db4o.test.replication.db4ounit.DrsTestSuiteBuilder;
import com.db4o.test.replication.db4ounit.fixtures.Db4oClientServerDrsFixture;
import com.db4o.test.replication.db4ounit.fixtures.Db4oDrsFixture;

import db4ounit.TestRunner;

public class AllTests extends DrsTestSuite {
	public static void main(String[] args) {
		new AllTests().runDb4oDb4o();
		new AllTests().rundb4oCS();
		new AllTests().runCSdb4o();
		new AllTests().runCSCS();
	}

	public void runDb4oDb4o() {
		new TestRunner(new DrsTestSuiteBuilder(new Db4oDrsFixture("db4o-a"),
				new Db4oDrsFixture("db4o-b"), getClass())).run();
	}

	public void runCSCS() {
		new TestRunner(new DrsTestSuiteBuilder(new Db4oClientServerDrsFixture(
				"db4o-cs-a", 0xdb40), new Db4oClientServerDrsFixture(
				"db4o-cs-b", 4455), getClass())).run();
	}

	public void rundb4oCS() {
		new TestRunner(new DrsTestSuiteBuilder(new Db4oDrsFixture("db4o-a"),
				new Db4oClientServerDrsFixture("db4o-cs-b", 4455), getClass()))
				.run();
	}

	public void runCSdb4o() {
		new TestRunner(new DrsTestSuiteBuilder(new Db4oClientServerDrsFixture(
				"db4o-cs-a", 4455), new Db4oDrsFixture("db4o-b"), getClass()))
				.run();
	}
}
