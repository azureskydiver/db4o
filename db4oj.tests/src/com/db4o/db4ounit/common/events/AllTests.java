package com.db4o.db4ounit.common.events;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {

	protected Class[] testCases() {
		return new Class[] {
			ActivationEventsTestCase.class,
			InstantiationEventsTestCase.class,
		};
	}

}
