/* Copyright (C) 2004 - 2005  db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.test.legacy.*;
import com.db4o.test.nativequery.*;
import com.db4o.test.replication.old.*;

public class AllTestsJdk1_2Legacy extends AllTestsLegacy {
	
    public static void main(String[] args) {
        new AllTestsJdk1_2Legacy(new String[]{}).runWithException();
    }
    
    public AllTestsJdk1_2Legacy(String[] testcasenames) {
    	super(testcasenames);
    }
    
    protected void addTestSuites(TestSuite suites) {
    	super.addTestSuites(suites);
    	suites.add(new TestSuite() {
			public Class[] tests() {
				return new Class[]{
			            Db4oLinkedList.class,
			            Db4oHashMap.class,
			            Db4oHashMapDeletedKey.class,
				};
			}
    	});
	}

}
