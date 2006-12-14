/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.querying;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTests().runSoloAndClientServer();
    }

	protected Class[] testCases() {
		return new Class[] {
	    	CascadedDeleteUpdate.class,
			CascadeDeleteArray.class,
            CascadeDeleteDeleted.class,
			CascadeDeleteFalse.class,
	    	CascadeOnActivate.class,
	        CascadeOnDelete.class,
	        CascadeOnUpdate.class,
	        CascadeToArray.class,
	        ConjunctiveQbETestCase.class,
			IdListQueryResultTestCase.class,
            IndexedQueriesTestCase.class,
            LazyQueryResultTestCase.class,
            MultiFieldIndexQueryTestCase.class,
            ObjectSetTestCase.class,
            OrderedQueryTestCase.class,
		};
	}
}
