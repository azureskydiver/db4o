package com.db4o.db4ounit.common.defragment;

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
			new Db4oTestSuiteBuilder(
				new Db4oDefragSolo(new IndependentConfigurationSource()), testSuite()).build().run(result);
		} catch (Exception e) {
			result.testFailed(this, e);
		}
	}
}
