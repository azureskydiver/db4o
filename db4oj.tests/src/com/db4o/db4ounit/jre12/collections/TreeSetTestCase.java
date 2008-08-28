/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections;

import java.util.*;

import db4ounit.*;
import db4ounit.extensions.*;


/**
 * @exclude
 */
/**
 * @decaf.ignore.jdk11
 */
public class TreeSetTestCase extends AbstractDb4oTestCase{
    
    private static final String ONE = "one";

    public static class Item {
        
        public Set treeSet;
        
    }
    
    public void store(){
        Item item = new Item();
        item.treeSet = new TreeSet();
        item.treeSet.add(ONE);
        store(item);
    }
    
    public void test(){
        Item item = (Item) retrieveOnlyInstance(Item.class);
        Assert.areEqual(1, item.treeSet.size());
        Iterator i = item.treeSet.iterator();
        Object singleItem = i.next();
        Assert.areEqual(ONE, singleItem);
    }

}
