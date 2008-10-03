package com.db4o.db4ounit.common.api;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {

	@Override
	protected Class[] testCases() {
		return new Class[] {
			BaseAndLocalConfigurationTestSuite.class,
			Db4oClientServerTestCase.class,
			Db4oEmbeddedTestCase.class,
		};
	}

}
