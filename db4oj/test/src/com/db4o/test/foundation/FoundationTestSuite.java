package com.db4o.test.foundation;

import com.db4o.test.TestSuite;

public class FoundationTestSuite extends TestSuite{
	
    public Class[] tests(){
        return new Class[] {
            Collection4TestCase.class,
            Hashtable4TestCase.class
        };
    }


}
