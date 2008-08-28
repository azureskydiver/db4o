/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre5;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;


/**
 * @decaf.ignore
 */
public class AllTestsWithTAJdk5 {
	public static void main(String[] args) {
		TestSuiteBuilder builder = new Db4oTestSuiteBuilder(
				new Db4oSolo(new TAFixtureConfiguration()),
				AllTestsDb4oUnitJdk5.class);
		System.exit(new ConsoleTestRunner(builder).run());
	}
}
