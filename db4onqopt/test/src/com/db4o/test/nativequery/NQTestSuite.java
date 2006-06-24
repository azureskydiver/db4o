/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.nativequery;

import com.db4o.test.*;
import com.db4o.test.nativequery.cats.*;

public class NQTestSuite extends TestSuite{

    public Class[] tests() {
        return new Class[] {
            NQRegressionTests.class,
            NQIntIsZero.class,
            TestCatConsistency.class
        };
    }
}
