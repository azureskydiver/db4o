/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import java.util.*;

import com.db4o.query.*;


public class ObjectSetAsIterator {
    
    String name;
    
    public ObjectSetAsIterator(){
    }
    
    public ObjectSetAsIterator(String name){
        this.name = name;
    }
    
    public void store(){
        Test.deleteAllInstances(this);
        Test.store(new ObjectSetAsIterator("one"));
        Test.store(new ObjectSetAsIterator("two"));
        Test.store(new ObjectSetAsIterator("three"));
    }
    
    public void test(){
        Query q = Test.query();
        q.constrain(ObjectSetAsIterator.class);
        Iterator i = q.execute();
        boolean found = false;
        while(i.hasNext()){
            ObjectSetAsIterator osil = (ObjectSetAsIterator)i.next();
            if(osil.name.equals("two")){
                found = true;
            }
        }
        Test.ensure(found);
    }

}
