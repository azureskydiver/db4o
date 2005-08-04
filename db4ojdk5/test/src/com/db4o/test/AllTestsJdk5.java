/* Copyright (C) 2004 - 2005  db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.test.nativequery.*;

public class AllTestsJdk5 extends AllTestsJdk1_2{
	
    public static void main(String[] args) {
        new AllTestsJdk5().run();
    }
    
    @Override
    protected void addTestSuites(TestSuite suites) {
    	super.addTestSuites(suites);
    	suites.add(new Jdk5TestSuite());
        if(Db4oVersion.MAJOR >= 5){
            suites.add(new NativeQueryTestSuite());
        }
	}

}
