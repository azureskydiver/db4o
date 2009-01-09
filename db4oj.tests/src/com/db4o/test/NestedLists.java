/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.util.*;

import com.db4o.query.*;


/**
 * 
 */
/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class NestedLists {
    
    static final int DEPTH = 10;
    
    List list;
    String name;
    
    public void storeOne() {
        nest(DEPTH);
        name = "root";
    }
    
    private void nest(int depth) {
        if(depth > 0) {
            list = Test.objectContainer().collections().newLinkedList();
            NestedLists nl = new NestedLists();
            nl.name = "nested";
            nl.nest(depth - 1);
            list.add(nl);
        }
    }
    
    
    
    public void test() {
        Query q = Test.query();
        q.constrain(NestedLists.class);
        q.descend("name").constrain("root");
        NestedLists nl = (NestedLists)q.execute().next();
        for (int i = 0; i < DEPTH - 1; i++) {
            nl = nl.checkNest();
        }
    }
    
    private NestedLists checkNest() {
        Test.ensure(list != null);
        NestedLists nl = (NestedLists)list.get(0);
        Test.ensure(nl.name.equals("nested"));
        return nl;
    }
    
    public String toString() {
        String str = "NestedList ";
        if(name != null) {
            str += name;
        }
        if(list != null) {
            str += " list valid";
        }else {
            str += " list INVALID";
        }
        return str;
    }
    

}
