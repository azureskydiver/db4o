/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.querying;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class QueryByExampleTestCase extends AbstractDb4oTestCase {

    static final int COUNT = 10;

    static LinkedList list = LinkedList.newLongCircularList();
    
    public static class Item {
    	
    	public String _name;
    	
    	public Item(String name){
    		_name = name;
    	}
    }

    public static void main(String[] args) {
        new QueryByExampleTestCase().runSolo();
    }

    protected void store() {
        store(list);
    }
    
    public void testDefaultQueryModeIsIdentity(){
    	Item itemOne = new Item("one");
    	Item itemTwo = new Item("two");
    	store(itemOne);
    	store(itemTwo);
    	
    	// Change the name of the "sample"
    	itemOne._name = "two";
    	
    	// Query by Identity
    	Query q = db().query();
    	q.constrain(itemOne);
    	ObjectSet objectSet = q.execute();
    	
    	// Expect to get the sample 
    	Assert.areEqual(1, objectSet.size());
    	Item retrievedItem = (Item) objectSet.next();
    	Assert.areSame(itemOne, retrievedItem);
    }
    
    
    public void testQueryByExample(){
    	Item itemOne = new Item("one");
    	Item itemTwo = new Item("two");
    	store(itemOne);
    	store(itemTwo);
    	
    	// Change the name of the "sample"
    	itemOne._name = "two";
    	
    	// Query by Example
    	Query q = db().query();
    	q.constrain(itemOne).byExample();
    	ObjectSet objectSet = q.execute();
    	
    	// Expect to get the other 
    	Assert.areEqual(1, objectSet.size());
    	Item retrievedItem = (Item) objectSet.next();
    	Assert.areSame(itemTwo, retrievedItem);
    }
    

    public void testByExample() {
        Query q = db().query();
        q.constrain(list).byExample();
        ObjectSet result = q.execute();
        Assert.areEqual(COUNT, result.size());
    }

    public void testByIdentity() {
        Query q = db().query();

        q.constrain(LinkedList.class);
        ObjectSet result = q.execute();
        Assert.areEqual(COUNT, result.size());
        while (result.hasNext()) {
            db().delete(result.next());
        }

        q = db().query();
        q.constrain(LinkedList.class);
        result = q.execute();
        Assert.areEqual(0, result.size());

        LinkedList newList = LinkedList.newLongCircularList();
        db().store(newList);
        q = db().query();
        q.constrain(newList);
        result = q.execute();
        Assert.areEqual(1, result.size());

    }
    
    

    public void testClassConstraint() {
        Query q = db().query();
        q.constrain(LinkedList.class);
        ObjectSet result = q.execute();
        Assert.areEqual(COUNT, result.size());

        q = db().query();
        q.constrain(LinkedList.class).byExample();
        result = q.execute();
        Assert.areEqual(COUNT, result.size());

    }

    public static class LinkedList {

        public LinkedList _next;

        public transient int _depth;

        public static LinkedList newLongCircularList() {
            LinkedList head = new LinkedList();
            LinkedList tail = head;
            for (int i = 1; i < COUNT; i++) {
                tail._next = new LinkedList();
                tail = tail._next;
                tail._depth = i;
            }
            tail._next = head;
            return head;
        }

        public String toString() {
            return "List[" + _depth + "]";
        }
    }

}
