/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.querying;

import com.db4o.ObjectSet;
import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

/**
 * @exclude
 */
public class OrderedQueryTestCase extends AbstractDb4oTestCase {
	
	public static void main(String[] args) {
		new OrderedQueryTestCase().runSolo();
	}
	
	public static final class Item {
		public int value;
		
		public Item(int value) {
			this.value = value;
		}
	}
	
	public static class Item2 {
	    public String _name;
	    
	    public Item2(String name) {
	        _name = name;
	    }
	}
	
	protected void store() throws Exception {
		db().store(new Item(1));
		db().store(new Item(3));
		db().store(new Item(2));
	}

	public void testOrderAscending() {
		final Query query = newQuery(Item.class);
		query.descend("value").orderAscending();
		assertQuery(new int[] { 1, 2, 3 }, query.execute());
	}
	
	public void testOrderDescending() {
		final Query query = newQuery(Item.class);
		query.descend("value").orderDescending();
		assertQuery(new int[] { 3, 2, 1 }, query.execute());
	}

	public void _testCOR1212() {
	    store(new Item2("Item 2"));
	    Query query = newQuery();
	    query.constrain(Item.class).or(query.constrain(Item2.class));
	    query.descend("value").orderAscending();
	    ObjectSet result = query.execute();
	    assertQuery(new int[] { 1, 2, 3 }, result);
	}
	
	private void assertQuery(int[] expected, ObjectSet actual) {
		for (int i = 0; i < expected.length; i++) {
			Assert.isTrue(actual.hasNext());
			Assert.areEqual(expected[i], ((Item)actual.next()).value);
		}
		Assert.isFalse(actual.hasNext());
	}
}
