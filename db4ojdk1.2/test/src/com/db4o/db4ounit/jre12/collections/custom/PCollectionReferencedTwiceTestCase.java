/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections.custom;

import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class PCollectionReferencedTwiceTestCase extends AbstractDb4oTestCase {
    

    protected void store(){
        
        ExtObjectContainer oc = db();
        
        PCRTHolder one = new PCRTHolder();
        
        one._list = oc.collections().newLinkedList();
        
        one._list.add("Hi");
        
        oc.set(one);
        
        PCRTHolder two = new PCRTHolder();
        
        two._list = one._list;
        
        oc.set(two);
    }
    
    public void test() throws Exception{
        
        tListIdentity();
        
        reopen();
        
        tListIdentity();
        
    }
    
    private void tListIdentity(){
        ExtObjectContainer oc = db();
        
        Query q = oc.query();
        q.constrain(PCRTHolder.class);
        
        ObjectSet res = q.execute();
        
        Assert.areEqual(2,res.size());
        
        PCRTHolder one = (PCRTHolder) res.next();
        PCRTHolder two = (PCRTHolder) res.next();
        
        Assert.areSame(one._list,two._list);
        
    }
    
    
    public static class PCRTHolder {
        
        public List _list; 
        
    }
    
    

}
