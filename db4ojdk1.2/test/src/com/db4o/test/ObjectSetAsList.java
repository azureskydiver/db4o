/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import java.util.*;

import com.db4o.query.*;


public class ObjectSetAsList {
    
    public String name;
    
    public ObjectSetAsList(){
    }
    
    public ObjectSetAsList(String name){
        this.name = name;
    }
    
    public void store(){
        Test.deleteAllInstances(this);
        Test.store(new ObjectSetAsList("one"));
        Test.store(new ObjectSetAsList("two"));
        Test.store(new ObjectSetAsList("three"));
    }
    
    public void test(){
        Query q = Test.query();
        q.constrain(ObjectSetAsList.class);
        List list = q.execute();
        Test.ensure(list.size() == 3);
        Iterator i = list.iterator();
        boolean found = false;
        while(i.hasNext()){
            ObjectSetAsList osil = (ObjectSetAsList)i.next();
            if(osil.name.equals("two")){
                found = true;
            }
        }
        Test.ensure(found);
    }

}
