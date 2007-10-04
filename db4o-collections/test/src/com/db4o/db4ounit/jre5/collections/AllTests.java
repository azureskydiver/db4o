/* Copyright (C) 2006 - 2007 db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {
    
    public static void main(String[] arguments) {
        new AllTests().runSolo();
    }
    
	protected Class[] testCases() {
		return new Class[] {
				ArrayList4TATestCase.class,
				ArrayList4TestCase.class,
				SubArrayList4TestCase.class,
		};
	}

}
