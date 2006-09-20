package com.db4o.test.drs.transients;

import com.db4o.test.drs.Db4oClientServerDrsFixture;
import com.db4o.test.drs.DrsTestSuite;
import com.db4o.test.drs.DrsTestSuiteBuilder;

import db4ounit.TestRunner;

public class TransientTests extends DrsTestSuite {
	public static void main(String[] args) {
		new TransientTests().runTransientdb4oCS();
	}
	
	public void runTransientdb4oCS() {
		new TestRunner(new DrsTestSuiteBuilder(
				new TransientFixture("Transient-a"),
				new Db4oClientServerDrsFixture("db4o-cs-b", 1234), 
				getClass()))
				.run();
	}
}
