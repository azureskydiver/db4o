package com.db4o.test.other;

import db4ounit.*;
import db4ounit.db4o.*;
import db4ounit.db4o.fixtures.Db4oSolo;

public class AllTests extends Db4oTestCase implements TestSuiteBuilder {
	
	public TestSuite build() {
		return new Db4oTestSuiteBuilder(
				fixture(),
				new Class[] {
					CollectionUuidTest.class,
				}).build();
	}
	
	public static void main(String[] args) {
		 new TestRunner(
				 new Db4oTestSuiteBuilder(
						 new Db4oSolo(),
						 AllTests.class)).run();
	}

}
