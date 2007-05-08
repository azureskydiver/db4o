package com.db4o.drs.test.foundation;

import db4ounit.ReflectionTestSuite;

public class AllTests extends ReflectionTestSuite {
	
	public static void main(String[] args) {
		new AllTests().run();
	}

	@Override
	protected Class[] testCases() {
		return new Class[] { 
			ObjectSetCollection4FacadeTestCase.class,
			Set4Testcase.class,
		};
	}

}
