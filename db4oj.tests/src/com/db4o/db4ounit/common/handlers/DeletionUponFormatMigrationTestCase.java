/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;

public class DeletionUponFormatMigrationTestCase extends FormatMigrationTestCaseBase {

	private static final int ITEMS_TO_KEEP_COUNT = 3;
	private static final String CHILD_TO_BE_KEPT = "bar";
	private static final String CHILD_TO_BE_DELETE = "foo";
	
	private static final int ID_TO_BE_DELETED = 42;
	private static final int ID_TO_BE_KEPT = 0xdb40;

	public static class Item {
		public Item(int id, ChildItem child, Item... items) {
			this(id);
			
			_child = child;
			_array = items;
		}
		
		public Item(int id) {
			_id = id;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			if (!obj.getClass().equals(Item.class)) return false;
			
			Item other = (Item) obj;
			return other._id == _id;
		}
		
		public Item[] _array;
		public Object _child;
		public int _id;
	}
	
	public static class ChildItem {
		public ChildItem(String name) {
			_name = name;
		}

		public String _name;
	}
	
	@Override
	protected void configureForTest(Configuration config) {
		config.objectClass(Item.class).cascadeOnDelete(true);	
	}
	
	@Override
	protected void assertObjectsAreReadable(ExtObjectContainer objectContainer) {
		if (db4oMajorVersion() < 5 || (db4oMajorVersion() ==5 && db4oMinorVersion() <4)) {
			return;
		}			
		
		assertChildItem(objectContainer, CHILD_TO_BE_DELETE, false);
		assertChildItem(objectContainer, CHILD_TO_BE_KEPT, true);
		
		assertReferenceToDeletedObjectSetToNull(objectContainer);		
		assertCascadeDeletionOnArrays(objectContainer);
	}

	private void assertCascadeDeletionOnArrays(ExtObjectContainer objectContainer) {
		ObjectSet<Item> keptItems = itemByIdGreaterThan(objectContainer, ID_TO_BE_KEPT);
		Assert.areEqual(0, keptItems.size());
	}

	private void assertReferenceToDeletedObjectSetToNull(ExtObjectContainer objectContainer) {
		Item item = itemById(objectContainer, ID_TO_BE_KEPT);
		Assert.isNotNull(item);
		Assert.areEqual(1, item._array.length);
		Assert.isNull(item._array[0]);
	}
	
	@Override
	protected void assertObjectDeletion(ExtObjectContainer objectContainer) {
		Item item = itemById(objectContainer, ID_TO_BE_DELETED);
		
		Assert.isNotNull(item._child);
		Assert.isNotNull(item._array[0]);
		objectContainer.delete(item);	
	}

	private void assertChildItem(ExtObjectContainer objectContainer, String name, boolean expectToBeFound) {
		Query query = objectContainer.query();
		query.constrain(ChildItem.class);
		
		query.descend("_name").constrain(name);
		
		ObjectSet<Object> result = query.execute();
		Assert.areEqual(expectToBeFound, result.hasNext(), name);
		
		if (expectToBeFound) {
			ChildItem childItem = (ChildItem) result.next();
			Assert.areEqual(name, childItem._name);
		}
	}

	private Item itemById(ExtObjectContainer objectContainer, int id) {
		Query query = objectContainer.query();
		query.constrain(Item.class);
		query.descend("_id").constrain(id);
		
		ObjectSet result = query.execute();
		Assert.areEqual(1, result.size());
		
		return (Item) result.next();
	}
	
	private ObjectSet<Item> itemByIdGreaterThan(ExtObjectContainer objectContainer, int id) {
		Query query = objectContainer.query();
		query.constrain(Item.class);
		query.descend("_id").constrain(id).greater();
		
		return query.<Item>execute();
	}


	@Override
	protected String fileNamePrefix() {
		return "deletion-tests";
	}

	@Override
	protected void store(ExtObjectContainer objectContainer) {
		Item item1 = new Item(ID_TO_BE_DELETED, new ChildItem(CHILD_TO_BE_DELETE), itemsToKeep());
		objectContainer.set(item1, 10);
		
		Item item2 = new Item(ID_TO_BE_KEPT, new ChildItem(CHILD_TO_BE_KEPT), item1);
		objectContainer.set(item2, 10);
	}

	private Item[] itemsToKeep() {
		Item[] items = new Item[ITEMS_TO_KEEP_COUNT];
		for (int i=1; i <= items.length; i++) {
			items[i - 1] = new Item(ID_TO_BE_KEPT + i);
		}		
		return items;
	}
}	
