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
	
	public static final class ItemWithCloneable {
		public Cloneable value;
		
		public ItemWithCloneable(Cloneable c) {
			value = c;
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
	
	public void testUntypedStringJaggedArray() {
		assertJaggedArray("42");
	}
	
	public void testUntypedFirstClassJaggedArray() {
		assertJaggedArray(new Data(42));
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
	
	public void testArrayAsCloneable() {
		Object[] array = new Object[] { "42", new Data(42) };
		ItemWithCloneable replicated = (ItemWithCloneable)replicate(new ItemWithCloneable(array));
		assertEquals(array, replicated.value);
	}

	private void assertUntypedReplication(Object data) {
		assertEquals(data, replicateItem(data).untyped);
	}
	
	private void assertJaggedArray(Object data) {
		Object[] expected = new Object[] { new Object[] { data } };
		Object[] actual = (Object[])replicateItem(expected).untyped;
		Assert.areEqual(expected.length, actual.length);
		
		Object[] nested = (Object[])actual[0];
		Object actualValue = nested[0];
		Assert.areEqual(data, actualValue);
		
		assertNotSame(data, actualValue);
	}

	private void assertNotSame(Object expectedFirstClass, Object actual) {
		if (isFirstClass(expectedFirstClass.getClass())) {
			Assert.areNotSame(expectedFirstClass, actual);
		}
	}

	private boolean isFirstClass(Class klass) {
		if (klass.isPrimitive()) return false;
		if (klass == String.class) return false;
		if (klass == Date.class) return false;
		return true;
	}
	
	private void assertEquals(Object expected, Object actual) {
		if (expected instanceof Object[]) {
			assertEquals((Object[])expected, (Object[])actual);
		} else {
			Assert.areEqual(expected, actual);
			assertNotSame(expected, actual);
		}
	}

	private void assertEquals(Object[] expectedArray, Object[] actualArray) {
		ArrayAssert.areEqual(expectedArray, actualArray);
		for (int i=0; i<expectedArray.length; ++i) {
			assertNotSame(expectedArray[i], actualArray[i]);
		}
	}

	private Item replicateItem(Object data) {
		return (Item) replicate(new Item(data));
	}

	private Object replicate(Object item) {
		a().provider().storeNew(item);
		a().provider().commit();
		
		replicateAll(a().provider(), b().provider());
		
		return singleReplicatedInstance(item.getClass());
	}

	private Object singleReplicatedInstance(Class klass) {
		ObjectSet found = b().provider().getStoredObjects(klass);
		Assert.areEqual(1, found.size());
		return found.get(0);
	}

}
