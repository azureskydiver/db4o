package com.db4o.drs.test;

import db4ounit.TestRunner;

public class Db4oTests extends DrsTestSuite {
	public static int main(String[] args) {
		int failureCount = new Db4oTests().rundb4oCS();
		//new Db4oTests().runCSdb4o();
		failureCount = failureCount + new Db4oTests().runCSCS();
		//new Db4oTests().runDb4oDb4o();
		return failureCount;
	}

	public void runDb4oDb4o() {
		new TestRunner(new DrsTestSuiteBuilder(new Db4oDrsFixture("db4o-a"),
				new Db4oDrsFixture("db4o-b"), getClass())).run();
	}

	public int runCSCS() {
		return new TestRunner(new DrsTestSuiteBuilder(new Db4oClientServerDrsFixture(
				"db4o-cs-a", 0xdb40), new Db4oClientServerDrsFixture(
				"db4o-cs-b", 4455), getClass())).run();
	}

	public int rundb4oCS() {
		return new TestRunner(new DrsTestSuiteBuilder(new Db4oDrsFixture("db4o-a"),
				new Db4oClientServerDrsFixture("db4o-cs-b", 4455), getClass()))
				.run();
	}

	public void runCSdb4o() {
		new TestRunner(new DrsTestSuiteBuilder(new Db4oClientServerDrsFixture(
				"db4o-cs-a", 4455), new Db4oDrsFixture("db4o-b"), getClass()))
				.run();
	}
	
	protected Class[] testCases() {
		return shared();
	}

	protected Class[] one() {
		return new Class[] { ByteArrayTest.class, };
	}
}
