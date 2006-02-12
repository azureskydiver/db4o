/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.nativequery;

import com.db4o.test.*;

public class AllTestsNQ extends AllTests{

    public static void main(String[] args) {
        new AllTestsNQ().run();
    }
    
    protected void addTestSuites(TestSuite suites) {
    	//CLIENT_SERVER=false;
        suites.add(new NQTestSuite());
    }


}
