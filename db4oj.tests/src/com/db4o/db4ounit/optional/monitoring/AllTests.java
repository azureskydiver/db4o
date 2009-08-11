/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.db4ounit.optional.monitoring;

import db4ounit.*;

@decaf.Remove
public class AllTests extends ReflectionTestSuite {

	@Override
	protected Class[] testCases() {
		return new Class[] {
			MonitoredStorageTestCase.class,
		};
	}

}
