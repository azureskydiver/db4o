/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.inside.btree;

import com.db4o.test.*;


public class BTreeTestSuite extends TestSuite{
    
    public Class[] tests(){
        return new Class[] {
            BTreeIntIndex.class
        };
    }


}
