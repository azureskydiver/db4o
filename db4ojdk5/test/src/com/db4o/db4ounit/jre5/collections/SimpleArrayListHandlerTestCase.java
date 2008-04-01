/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;
import com.db4o.typehandlers.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;


public class SimpleArrayListHandlerTestCase extends AbstractDb4oTestCase implements OptOutDefragSolo {
	
	public static void main(String[] args) {
		new SimpleArrayListHandlerTestCase().runAll();
	}
    
    public static class Item {
        public ArrayList list;
    }
    
    protected void configure(Configuration config) throws Exception {
        config.registerTypeHandler(
            new SingleClassTypeHandlerPredicate(ArrayList.class), 
            new ArrayListTypeHandler());
    }
    
    @SuppressWarnings("unchecked")
	protected void store() throws Exception {
        Item item = new Item();
        item.list = new ArrayList();
        item.list.add("one");
        store(item);
    }
    
    public void test(){
        Item item = (Item) retrieveOnlyInstance(Item.class);
        assertListContent(item);
    }
    
    public void testQuery() throws Exception {
    	Query q = newQuery(Item.class);
    	q.descend("list").constrain("one");
    	assertQueryResult(q);
	}

	private void assertListContent(Item item) {
		Assert.areEqual(item.list.size(), 1);
        Assert.areEqual("one", item.list.get(0));
	}
	
	public void testContainsQuery() throws Exception {
    	Query q = newQuery(Item.class);
    	q.descend("list").constrain("e").endsWith(false);
    	assertQueryResult(q);
	}
	
	public void testFailingContainsQuery() throws Exception {
    	Query q = newQuery(Item.class);
    	q.descend("list").constrain("g").endsWith(false);
    	assertEmptyQueryResult(q);
	}
	
	public void testCompareItems() throws Exception {
    	Query q = newQuery();
    	Item item = new Item();
    	item.list = new ArrayList();
    	item.list.add("two");
    	q.constrain(item);
    	assertEmptyQueryResult(q);
    }

	private void assertEmptyQueryResult(Query q) {
		ObjectSet set = q.execute();
		Assert.isTrue(set.isEmpty());
	}

	private void assertQueryResult(Query q) {
		ObjectSet set = q.execute();
    	
    	Assert.areEqual(1, set.size());
    	Item item = (Item)set.next();
        assertListContent(item);
	}
}
