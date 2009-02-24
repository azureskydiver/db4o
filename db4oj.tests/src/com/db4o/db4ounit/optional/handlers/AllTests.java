package com.db4o.db4ounit.optional.handlers;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {

	@Override
	protected Class[] testCases() {
		return new Class[] {
			BigDecimalTypeHandlerTestCase.class,
			BigIntegerTypeHandlerTestCase.class
		};
	}

}
