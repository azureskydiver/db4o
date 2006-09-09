/* Copyright (C) 2004 - 2005  db4objects Inc.   http://www.db4o.com */

package com.db4o.test.nativequery;

import com.db4o.test.*;

public class NativeQueryTestSuite extends TestSuite{
    
    public Class[] tests(){
        return new Class[] {
            Cat.class
        };
    }
    
}