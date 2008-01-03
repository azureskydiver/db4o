/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import java.util.*;

import com.db4o.*;


public class QueryForClass {
    
    public String name;
    
    public void storeOne(){
        name = "one";
    }
    
    public void testQuery(){
        ObjectContainer oc = Test.objectContainer();
        List <QueryForClass> list = oc.query(QueryForClass.class);
        for (QueryForClass res : list) {
            Test.ensure(res.name.equals("one"));
        }
    }
    
    public void testGet(){
        ObjectContainer oc = Test.objectContainer();
        List <QueryForClass> list = oc.queryByExample(QueryForClass.class);
        for (QueryForClass res : list) {
            Test.ensure(res.name.equals("one"));
        }
    }


}
