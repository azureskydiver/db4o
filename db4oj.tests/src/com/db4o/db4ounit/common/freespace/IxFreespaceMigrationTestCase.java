/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.freespace;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.handlers.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;

public class IxFreespaceMigrationTestCase extends FormatMigrationTestCaseBase {

	protected void configureForStore(Configuration config) {
		config.freespace().useIndexSystem();
	}
	
	protected void configureForTest(Configuration config) {
		config.freespace().useRamSystem();
	}
	
	protected void store(ExtObjectContainer objectContainer) {
		Item nextItem = null;
		for (int i = 9; i >= 0; i--) {
			Item storedItem = new Item("item" + i, nextItem);
			objectContainer.set(storedItem);
			nextItem = storedItem;
		}
		objectContainer.commit();
		Item item = queryForItem(objectContainer, 0);
		for (int i = 0; i < 5; i++) {
			objectContainer.delete(item);
			item = item._next;
		}
		objectContainer.commit();
		assertObjectsAreReadable(objectContainer);
	}

	private Item queryForItem(ExtObjectContainer objectContainer, int n) {
		Query q = objectContainer.query();
		q.constrain(Item.class);
		q.descend("_name").constrain("item" + n);
		return (Item)q.execute().next();
	}
	
	protected void assertObjectsAreReadable(ExtObjectContainer objectContainer) {
		assertItemCount(objectContainer, 5);
		Item item = queryForItem(objectContainer, 5);
		for (int i = 5; i < 10; i++) {
			Assert.areEqual("item" + i, item._name);
			item = item._next;
		}
	}
	
	private void assertItemCount(ExtObjectContainer objectContainer, int i) {
		Query q = objectContainer.query();
		q.constrain(Item.class);
		Assert.areEqual(i, q.execute().size());
	}

	public static class Item{
		
		public String _name;
		
		public Item _next;
		
		public Item(String name){
			_name = name;
		}
		
		public Item(String name, Item next_){
			_name = name;
			_next = next_;
		}
		
	}

	protected String fileNamePrefix() {
		return "migrate_freespace_ix_" ;
	}

	protected String[] versionNames() {
		return new String[] { Db4o.version().substring(5) };
	}

}
