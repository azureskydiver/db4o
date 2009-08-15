/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.db4ounit.optional.monitoring;

import db4ounit.extensions.*;

@decaf.Remove
public class AllTests extends Db4oTestSuite {

	@Override
	protected Class[] testCases() {
		return new Class[] {
			MonitoredStorageTestCase.class,
			NativeQueryMonitoringSupportTestCase.class,
			QueryMonitoringSupportTestCase.class,
		};
	}

}
