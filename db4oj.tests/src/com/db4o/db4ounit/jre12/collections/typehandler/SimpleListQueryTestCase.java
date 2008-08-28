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
public class SimpleListQueryTestCase extends AbstractDb4oTestCase {
	
	public static class Item {
		public List list;
	}
	
	public static class FirstClassElement {
		
		public String name;
		
		public FirstClassElement(String name_){
			name = name_;
		}
		
		public boolean equals(Object obj) {
			if(! (obj instanceof FirstClassElement)){
				return false;
			}
			FirstClassElement other = (FirstClassElement) obj;
			if(name == null){
				return other.name == null;
			}
			return name.equals(other.name);
		}
		
	}
	
	static final Object[] DATA = new Object[]{
		"one",
		"two",
		new Integer(1),
		new Integer(2),
		new Integer(42),
		new FirstClassElement("one"),
		new FirstClassElement("fortytwo"),
		
	};
	
	protected void configure(Configuration config) throws Exception {
		config.objectClass(Item.class).cascadeOnDelete(true);
	}
	
	protected void store() throws Exception {
		for (int i = 0; i < DATA.length; i++) {
			storeItem(DATA[i]);
		}
	}
	
	private void storeItem(Object listElement){
		Item item = new Item();
		item.list = new ArrayList();
		item.list.add(listElement);
		store(item);
	}
	
	public void testListConstrainQuery() {
		for (int i = 0; i < DATA.length; i++) {
			assertSingleElementQuery(DATA[i]);
		}
	}

	private void assertSingleElementQuery(Object element) {
		Query q = db().query();
		q.constrain(Item.class);
		q.descend("list").constrain(element);
		assertSingleElementQueryResult(q, element);
	}

	private void assertSingleElementQueryResult(Query query, Object element) {
		ObjectSet objectSet = query.execute();
		Assert.areEqual(1, objectSet.size());
		Item item = (Item) objectSet.get(0);
		Assert.areEqual(element, item.list.get(0));
	}

}
