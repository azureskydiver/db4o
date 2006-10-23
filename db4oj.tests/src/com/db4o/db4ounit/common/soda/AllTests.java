/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.soda;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class AllTests extends Db4oTestSuite {

	protected Class[] testCases() {
		return new Class[]{
				com.db4o.db4ounit.common.soda.arrays.AllTests.class,
		};
	}
	
	public static void main(String[] args) {
		new TestRunner(new Db4oTestSuiteBuilder(new Db4oSolo(),AllTests.class)).run();
	}

}
