/* Copyright (C) 2004 - 2005  db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.test.nativequery.*;
import com.db4o.test.replication.old.*;

public class AllTestsJdk1_2 extends AllTests{
	
    public static void main(String[] args) {
        new AllTestsJdk1_2().run();
    }
    
    protected void addTestSuites(TestSuite suites) {
    	super.addTestSuites(suites);
    	suites.add(new Jdk1_2TestSuite());
        suites.add(new ReplicationTestSuite());
        if(Db4oVersion.MAJOR >= 5){
            suites.add(new NativeQueryTestSuite());
        }
	}

}
