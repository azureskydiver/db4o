/* Copyright (C) 2004 - 2006 Versant Inc. http://www.db4o.com */

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
	        CascadeOnDeleteTestCase.class,
	        CascadeOnDeleteHierarchyTestCase.class,
	        CascadeOnUpdate.class,
	        CascadeToArray.class,
	        ConjunctiveQbETestCase.class,
	        DeepMultifieldSortingTestCase.class,
	        DescendIndexQueryTestCase.class,
	        IdentityQueryForNotStoredTestCase.class,
			IdListQueryResultTestCase.class,
            IndexedJoinQueriesTestCase.class,
			IndexOnParentFieldTestCase.class,
            IndexedQueriesTestCase.class,
            InvalidFieldNameConstraintTestCase.class,
            LazyQueryResultTestCase.class,
            MultiFieldIndexQueryTestCase.class,
            NullConstraintQueryTestCase.class,
            ObjectSetTestCase.class,
            OrderedQueryTestCase.class,
            QueryByExampleTestCase.class,
            QueryingForAllObjectsTestCase.class,
            QueryingVersionFieldTestCase.class,
            SameChildOnDifferentParentQueryTestCase.class,
		};
	}
}
