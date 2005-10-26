/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.nativequery.cats;

import java.util.*;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.test.*;


public class TestCatSpeed {
    
    int COUNT = 10000;
    int QUERY_RUNS = 5;
    
    public void store(){
        storeCats();
    }
    
    public void test(){
        
        for (int i = 0; i < QUERY_RUNS; i++) {
            
            
        }
        
        
        
        ObjectContainer db = Test.objectContainer();
        
        List noneExpected = db.query(new Predicate(){
            public boolean match(Cat cat){
                return cat._age == 7;
            }
        });
        
        Test.ensure(noneExpected.size() == 0);
    }
    
    public void storeCats(){
        for (int i = 0; i < COUNT; i++) {
            Cat fastCat = new Cat();
            fastCat._firstName = "SpeedyClone" + i;
            fastCat._age = i;
            Test.store(fastCat);
        }
    }
    


}
