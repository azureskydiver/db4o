/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.assorted;

import com.db4o.*;
import com.db4o.ext.Db4oUUID;
import com.db4o.foundation.Hashtable4;
import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.extensions.Db4oTestCase;

public class GetByUUID extends Db4oTestCase {
	
	public static void main(String[] args) {
		new GetByUUID().runSolo();
	}

	public static class Item {
		public String name;
	
		public Item() {
		}
	
		public Item(String name) {
			this.name = name;
		}
	}

	protected void configure() {
		Db4o.configure().objectClass(Item.class).generateUUIDs(true);
	}

	protected void store() {
		db().set(new Item("one"));
		db().set(new Item("two"));
	}

	public void test() throws Exception {
		
		Hashtable4 uuidCache = new Hashtable4();
		
		assertItemsCanBeRetrievedByUUID(uuidCache);
		
		reopen();
		
		assertItemsCanBeRetrievedByUUID(uuidCache);
	}

	private void assertItemsCanBeRetrievedByUUID(Hashtable4 uuidCache) {
		Query q = db().query();
		q.constrain(Item.class);
		ObjectSet objectSet = q.execute();
		while (objectSet.hasNext()) {
			Item item = (Item) objectSet.next();
			Db4oUUID uuid = db().getObjectInfo(item).getUUID();			
			Assert.isNotNull(uuid);
			Assert.areSame(item, db().getByUUID(uuid));
			final Db4oUUID cached = (Db4oUUID) uuidCache.get(item.name);
			if (cached != null) {
				Assert.areEqual(cached, uuid);
			} else {
				uuidCache.put(item.name, uuid);
			}
		}
	}
}
