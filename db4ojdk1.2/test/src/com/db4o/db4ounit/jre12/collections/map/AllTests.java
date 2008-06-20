/* Copyright (C) 2006 - 2007 db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections.map;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {
	
	public static void main(String[] arguments) {
        new AllTests().runAll();
    }
	
	protected Class[] testCases() {
		return new Class[] {
			ExtendsHashMapTestCase.class,
            HashMapQueryTestCase.class,
			SimpleMapTestCase.class,
		};
	}
}
