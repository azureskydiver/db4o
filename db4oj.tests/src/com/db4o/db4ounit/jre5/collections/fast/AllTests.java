/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections.fast;

import db4ounit.extensions.*;

/**
 */
@decaf.Ignore
public class AllTests extends Db4oTestSuite {
	
    public static void main(String[] arguments) {
        new AllTests().runSolo();
    }
    
    protected Class[] testCases() {
        return 
            new Class[] {
        		StatefulListTestCase.class,
		};
	}


}
