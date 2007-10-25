/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta.mixed;

import com.db4o.db4ounit.common.ta.*;

import db4ounit.extensions.*;


public class LinkedArrayTestCase extends AbstractDb4oTestCase {

    public static void main(String[] args) {
        new LinkedArrayTestCase().runSolo();
    }
    
    public void testTheTest(){
        for (int depth = 1; depth < 8; depth++) {
            LinkedArrays linkedArrays = LinkedArrays.newLinkedArrays(depth);
            linkedArrays.assertActivationDepth(depth - 1);
        }
    }
    

}
