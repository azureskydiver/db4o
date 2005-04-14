/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;
import com.db4o.test.*;


public class R0to4Runner {
    
    public void store(){
        ExtObjectContainer oc = Test.objectContainer();
        R0Linker lCircles = new R0Linker();
        lCircles.setNames("circles");
        lCircles.linkCircles();
        lCircles.store(oc);
    }
    
    public void test(){
        ExtObjectContainer oc = Test.objectContainer();
        ensureCount(oc, 1);
    }
    
    private void ensureCount(ObjectContainer oc, int linkers){
        ensureCount(oc, R0.class, linkers * 5);
        ensureCount(oc, R1.class, linkers * 4);
        ensureCount(oc, R2.class, linkers * 3);
        ensureCount(oc, R3.class, linkers * 2);
        ensureCount(oc, R4.class, linkers * 1);
    }
    
    private void ensureCount(ObjectContainer oc, Class clazz, int count){
        Query q = oc.query();
        q.constrain(clazz);
        Test.ensure(q.execute().size() == count);
    }
    
    

}
