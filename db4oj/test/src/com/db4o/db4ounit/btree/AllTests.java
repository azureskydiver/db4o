/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.btree;

import db4ounit.db4o.Db4oTestSuite;

public class AllTests extends Db4oTestSuite {

	public static void main(String[] args) {
		new AllTests().runSolo();
    }

	protected Class[] testCases() {
        
        return new Class[] {
            BTreeAddRemoveTestCase.class,
            BTreeSearchTestCase.class,
            BTreeSimpleTestCase.class,
            SearcherLowestHighestTestCase.class,
            SearcherTestCase.class,
		};
    }
}
