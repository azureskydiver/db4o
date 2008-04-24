package com.db4o.drs.test;

import java.util.*;

import com.db4o.*;

import db4ounit.*;

public class UntypedFieldTestCase extends DrsTestCase {
	
	public static final class Item {
		
		public Object untyped;
		
		public Item(Object value) {
			untyped = value;
		}
	}
	
	public static final class Data {
		
		public int id;
		
		public Data(int value) {
			id = value;
		}
		
		public boolean equals(Object obj) {
			Data other = (Data)obj;
			return id == other.id;
		}
	}
	
	public void testUntypedString() {
		assertUntypedReplication("42");
	}
	
	public void testUntypedStringArray() {
		assertUntypedReplication(new Object[] { "42" });
	}
	
	public void testUntypedDate() {
		assertUntypedReplication(new Date(100, 2, 2));
	}
	
	public void testUntypedDateArray() {
		assertUntypedReplication(new Object[] { new Date(100, 2, 2) });
	}
	
	public void testUntypedMixedArray() {
		assertUntypedReplication(new Object[] { "42", new Data(42) });
		Assert.areEqual(42, ((Data)singleReplicatedInstance(Data.class)).id);
	}

	private void assertUntypedReplication(Object data) {
		Item item = new Item(data);
		a().provider().storeNew(item);
		a().provider().commit();
		
		replicateAll(a().provider(), b().provider());
		
		Item replicated = (Item) singleReplicatedInstance(Item.class);
		Object expected = item.untyped;
		if (expected instanceof Object[]) {
			ArrayAssert.areEqual((Object[])expected, (Object[])replicated.untyped);
		} else {
			Assert.areEqual(expected, replicated.untyped);
		}
	}

	private Object singleReplicatedInstance(Class klass) {
		ObjectSet found = b().provider().getStoredObjects(klass);
		Assert.areEqual(1, found.size());
		return found.get(0);
	}

}
