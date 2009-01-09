/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.concurrency;

import com.db4o.test.*;


/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class ConcurrencyTestSuite extends TestSuite{
    
    public Class[] tests(){
        return new Class[] {
            CascadedDeleteStaleReference.class
        };
    }

}
