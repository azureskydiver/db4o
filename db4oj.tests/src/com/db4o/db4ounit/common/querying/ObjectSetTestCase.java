/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.querying;

import com.db4o.*;
import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

/**
 * @exclude
 */
public class ObjectSetTestCase extends AbstractDb4oTestCase {
	
	public static void main(String[] args) {
		new ObjectSetTestCase().runSoloAndClientServer();
    }
	
	public static class Item {
		public String name;
		
		public Item() {			
		}
		
		public Item(String name) {
			this.name = name;
		}
	}
	
	protected void store() throws Exception {
		db().set(new Item("foo"));
		db().set(new Item("bar"));
		db().set(new Item("baz"));
	}
	
	public void testObjectsCantBeSeenAfterDelete() {
		final Transaction trans1 = newTransaction();
		final Transaction trans2 = newTransaction();
		final ObjectSet os = queryItems(trans1);
		deleteItemAndCommit(trans2, "foo");
		assertItems(new String[] { "bar", "baz" }, os);
	}

	private void assertItems(String[] expectedNames, ObjectSet actual) {
		for (int i = 0; i < expectedNames.length; i++) {
			Assert.isTrue(actual.hasNext());
			Assert.areEqual(expectedNames[i], ((Item)actual.next()).name);
		}
		Assert.isFalse(actual.hasNext());
	}

	private void deleteItemAndCommit(Transaction trans, String name) {
		stream().delete(trans, queryItem(trans, name));
		trans.commit();
	}

	private Item queryItem(Transaction trans, String name) {
		final Query q = newQuery(trans, Item.class);
		q.descend("name").constrain(name);
		return (Item) q.execute().next();
	}

	private ObjectSet queryItems(final Transaction trans) {
		final Query q = newQuery(trans, Item.class);
		q.descend("name").orderAscending();
		return q.execute();
	}

}
