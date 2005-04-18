/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.interfaces;

import com.db4o.query.*;
import com.db4o.test.*;


public class ThreeSomeParent {
    
    public void store(){
        Test.deleteAllInstances(this);
        Test.store(new ThreeSomeParent());
        Test.store(new ThreeSomeLeftChild());
        Test.store(new ThreeSomeRightChild());
    }
    
    public void test(){
        Query q = Test.query();
        q.constrain(ThreeSomeInterface.class);
        Test.ensure(q.execute().size() == 2);
    }

}
