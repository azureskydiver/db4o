/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.legacy;

import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;
import com.db4o.test.*;


public class PCollectionReferencedTwice {
    

    
    public void store(){
        
        Test.deleteAllInstances(this);
        
        ExtObjectContainer oc = Test.objectContainer();
        
        PCRTHolder one = new PCRTHolder();
        
        one._list = oc.collections().newLinkedList();
        
        one._list.add("Hi");
        
        oc.set(one);
        
        PCRTHolder two = new PCRTHolder();
        
        two._list = one._list;
        
        oc.set(two);
    }
    
    public void test(){
        
        tListIdentity();
        
        Test.defragment();
        
        tListIdentity();
        
    }
    
    private void tListIdentity(){
        ExtObjectContainer oc = Test.objectContainer();
        
        Query q = oc.query();
        q.constrain(PCRTHolder.class);
        
        ObjectSet res = q.execute();
        
        Test.ensure(res.size() == 2);
        
        PCRTHolder one = (PCRTHolder) res.next();
        PCRTHolder two = (PCRTHolder) res.next();
        
        Test.ensure(one._list == two._list);
        
    }
    
    
    public static class PCRTHolder {
        
        public List _list; 
        
    }
    
    

}
