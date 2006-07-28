/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.foundation;

import com.db4o.test.TestSuite;

public class FoundationTestSuite extends TestSuite{
	
    public Class[] tests(){
        return new Class[] {
            BitMap4TestCase.class,
            Collection4TestCase.class,
            Hashtable4TestCase.class
        };
    }


}
