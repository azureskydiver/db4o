/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre5;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class AllTestsDb4oUnitJdk5 extends Db4oTestSuite {

	public static void main(String[] args) {
//		runTAFixture();
		
		System.exit(new AllTestsDb4oUnitJdk5().runAll());
	}

	private static void runTAFixture() {
		TestSuiteBuilder builder = new Db4oTestSuiteBuilder(
				new Db4oSolo(new TAFixtureConfiguration()),
				AllTestsDb4oUnitJdk5.class);
		System.exit(new TestRunner(builder).run());
	}

	@Override
	protected Class[] testCases() {
		return new Class[] {
			com.db4o.db4ounit.jre5.annotation.AllTests.class,
			com.db4o.db4ounit.jre5.collections.AllTests.class,
			com.db4o.db4ounit.jre5.enums.AllTests.class,
			com.db4o.db4ounit.jre5.generic.AllTests.class,
			com.db4o.db4ounit.jre12.AllTestsJdk1_2.class,
		};
	}

}
