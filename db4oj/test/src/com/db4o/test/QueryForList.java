/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import java.util.*;

import com.db4o.*;
import com.db4o.query.*;


/**
 * 
 */
public class QueryForList {
    
    List _list;
    
    public void storeOne(){
        _list = new QueryForListArrayList();
        _list.add("hi");
    }
    
    public void test(){
        Query q = Test.query();
        q.constrain(List.class);
        ObjectSet objectSet = q.execute();
        boolean found = false;
        while(objectSet.hasNext()){
            List list = (List)objectSet.next();
            if(list instanceof QueryForListArrayList){
                if(list.get(0).equals("hi")){
                    found = true; 
                    break;
                }
            }
        }
        Test.ensure(found);
    }
    
    static class QueryForListArrayList extends ArrayList{
    }

}
