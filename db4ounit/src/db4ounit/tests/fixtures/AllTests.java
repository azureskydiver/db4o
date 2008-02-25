/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.tests.fixtures;

import db4ounit.*;


public class AllTests extends ReflectionTestSuite {

	protected Class[] testCases() {
		return new Class[] {
			FixtureBasedTestSuiteTestCase.class,
			Set4TestSuite.class,
		};
	}

}
