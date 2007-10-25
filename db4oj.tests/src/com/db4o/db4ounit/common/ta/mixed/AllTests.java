/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta.mixed;

import db4ounit.extensions.*;

public class AllTests extends Db4oTestSuite {
    
    public static void main(String[] args) {
        new AllTests().runAll();
    }

    protected Class[] testCases() {
        return new Class[] {
                NNTTestCase.class,
                NTTestCase.class,
        };
    }

}
