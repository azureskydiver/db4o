/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.nativequery;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.test.*;


public class NQIntIsZero {
    
    public int _int;
    
    
    public NQIntIsZero() {
        
    }
    
    public NQIntIsZero(int anint) {
        _int = anint;
    }
    
    public void configure(){
        // Db4o.configure().optimizeNativeQueries(false);
    }
    
    public void store(){
        Test.deleteAllInstances(getClass());
        for (int i = 0; i < 5; i++) {
            Test.store(new NQIntIsZero(i));
        }
    }
    
    public void test(){
        ObjectContainer oc = Test.objectContainer();
        int sizeFound = oc.query(new Predicate(){
            public boolean match(NQIntIsZero candidate){
                return candidate._int == 0;
            }
        }).size();
        Test.ensureEquals(1, sizeFound);
    }

}
