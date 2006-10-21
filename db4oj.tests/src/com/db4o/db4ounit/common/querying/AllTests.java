/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.querying;

import db4ounit.extensions.Db4oTestSuite;

public class AllTests extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTests().runSoloAndClientServer();
    }

	protected Class[] testCases() {
		return new Class[] {
            IndexedQueriesTestCase.class,
            MultiFieldIndexQueryTestCase.class,
            ObjectSetTestCase.class,
            OrderedQueryTestCase.class,
            QueryResultTestCase.class,
		};
	}
}
