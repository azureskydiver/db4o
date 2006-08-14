/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.test.other;

import com.db4o.test.replication.db4ounit.DrsTestCase;
import com.db4o.test.replication.db4ounit.DrsTestSuiteBuilder;
import com.db4o.test.replication.db4ounit.fixtures.Db4oDrsFixture;

import db4ounit.TestRunner;
import db4ounit.TestSuite;
import db4ounit.TestSuiteBuilder;


public class AllReplicationTests extends DrsTestCase implements TestSuiteBuilder {

	public TestSuite build() {
		return new DrsTestSuiteBuilder(
				a(), b(), new Class[] {
					TheSimplest.class
				}).build();
	}

	public static void main(String[] args) {
		new TestRunner(
				new DrsTestSuiteBuilder(
						new Db4oDrsFixture("A"), new Db4oDrsFixture("B"),
						AllReplicationTests.class)).run();
	}
}
