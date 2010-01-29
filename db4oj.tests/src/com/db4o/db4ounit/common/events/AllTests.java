/* Copyright (C) 2007   Versant Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.events;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {
	
	public static void main(String[] args) {
		new AllTests().runAll();
	}

	protected Class[] testCases() {
		return new Class[] {
			ActivationEventsTestCase.class,
			ClassRegistrationEventsTestCase.class,
			CreationEventsTestCase.class,
			DeleteEventOnClientTestCase.class,
			DeleteOnDeletingCallbackTestCase.class,
			DeletionEventExceptionTestCase.class,
			DeletionEventsTestCase.class,
			EventArgsTransactionTestCase.class,			
			EventCountTestCase.class,
			ExceptionPropagationInEventsTestSuite.class,
			InstantiationEventsTestCase.class,
			ObjectContainerEventsTestCase.class,
			ObjectContainerOpenEventTestCase.class,
			QueryEventsTestCase.class,
			UpdateInCallbackThrowsTestCase.class,
		};
	}

}
