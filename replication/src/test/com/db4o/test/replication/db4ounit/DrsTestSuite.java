/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.test.replication.db4ounit;

import com.db4o.test.replication.db4ounit.fixtures.Db4oDrsFixture;

import db4ounit.*;

/**
 * @exclude
 */
public abstract class DrsTestSuite  extends DrsTestCase implements
		TestSuiteBuilder {

	public TestSuite build() {
		return new DrsTestSuiteBuilder(a(), b(), testCases()).build();
	}
	
	public void runDb4oDb4o() {
		new TestRunner(
				new DrsTestSuiteBuilder(
						new Db4oDrsFixture("db4o-a"),
						new Db4oDrsFixture("db4o-b"),
						getClass())).run();
	}
	
	protected abstract Class[] testCases();

}
