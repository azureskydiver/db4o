package com.db4o.db4ounit.jre12.ta;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {

    public static void main(String[] args) {
        new AllTests().runAll();
    }
    
    protected Class[] testCases() {
        return new Class[]{
        		TAVirtualFieldTestCase.class,
        };
    }

}
