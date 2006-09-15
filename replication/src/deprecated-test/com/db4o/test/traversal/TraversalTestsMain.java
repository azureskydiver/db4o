/* Copyright (C) 2004 - 2005  db4objects Inc.   http://www.db4o.com */

package com.db4o.test.traversal;

import com.db4o.test.AllTests;
import com.db4o.test.TestSuite;

public class TraversalTestsMain extends AllTests{
	
    public static void main(String[] args) {
        new TraversalTestsMain().run();
    }
    
    protected void addTestSuites(TestSuite suites) {
        suites.add(new TraversalTestSuite());
	}

}
