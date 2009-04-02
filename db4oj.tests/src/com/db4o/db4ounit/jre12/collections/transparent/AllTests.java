/* Copyright (C) 2006 - 2007 db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections.transparent;

import com.db4o.db4ounit.jre12.collections.transparent.list.*;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {
	
	public static void main(String[] arguments) {
        new AllTests().runAll();
    }
	
	protected Class[] testCases() {
		return new Class[] {
				ActivatableArrayListTestCase.class,
				ActivatableLinkedListTestCase.class,
				ActivatableListAPITestSuite.class,
				ActivatableMapTestCase.class,
				ActivatableStackTestCase.class
		};
	}
}
