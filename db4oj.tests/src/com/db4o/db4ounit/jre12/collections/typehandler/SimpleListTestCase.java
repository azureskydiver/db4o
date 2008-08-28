/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections.typehandler;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * @exclude
 * @decaf.ignore.jdk11
 */
public class SimpleListTestCase extends AbstractDb4oTestCase{
	
	public static class Item {
		public List list;
	}
	
	public static class FirstClassElement {
		
		public String name;
		
		public FirstClassElement(String name_){
			name = name_;
		}
		
	}
	
	protected void configure(Configuration config) throws Exception {
		config.objectClass(Item.class).cascadeOnDelete(true);
	}
	
	protected void store() throws Exception {
		Item item = new Item();
		item.list = new ArrayList();
		item.list.add("zero");
		item.list.add(new FirstClassElement("one"));
		store(item);
	}
	
	public void testRetrieveInstance() {
		Item item = (Item) retrieveOnlyInstance(Item.class);
		Assert.areEqual(2, item.list.size());
		Assert.areEqual("zero", item.list.get(0));
	}
	
    public void testActivation(){
    	Item item = (Item) retrieveOnlyInstance(Item.class);
        List list = item.list;
        Assert.areEqual(2, list.size());
        Object element = list.get(1);
        if(db().isActive(element)){
            db().deactivate(item, Integer.MAX_VALUE);
            Assert.isFalse(db().isActive(element));
            db().activate(item, Integer.MAX_VALUE);
            Assert.isTrue(db().isActive(element));
        }
    }
	
	public void testQuery() {
		Query q = db().query();
		q.constrain(Item.class);
		q.descend("list").constrain("zero");
		ObjectSet objectSet = q.execute();
		Assert.areEqual(1, objectSet.size());
		Item item = (Item) objectSet.next();
		Assert.areEqual("zero", item.list.get(0));
	}
	
	public void testDeletion() {
		assertObjectCount(FirstClassElement.class, 1);
		Item item = (Item) retrieveOnlyInstance(Item.class);
		db().delete(item);
		assertObjectCount(FirstClassElement.class, 0);
	}

	private void assertObjectCount(Class clazz, int count) {
		Assert.areEqual(count, db().query(clazz).size());
	}

}
