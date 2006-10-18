package com.db4o.drs.test.transients;

import com.db4o.drs.test.*;

import db4ounit.TestRunner;

public class TransientTests {
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
