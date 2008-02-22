/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.defragment;

import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public abstract class AbstractDb4oDefragTestCase implements Test {

	public String getLabel() {
		return "DefragAllTestCase: " +  testSuite().getName();
	}
	
	public abstract Class testSuite();

	public void run(TestResult result) {
		try {
			final Iterator4 tests = new Db4oTestSuiteBuilder(
				new Db4oDefragSolo(new IndependentConfigurationSource()), testSuite()).build();
			TestRunner.runAll(result, tests);
		} catch (Exception e) {
			result.testFailed(this, e);
		}
	}
}
