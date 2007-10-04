/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections;

import java.lang.reflect.Field;
import java.util.*;

import com.db4o.collections.*;
import com.db4o.config.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * @exclude
 */
public class ArrayList4TATestCase extends AbstractDb4oTestCase {

	private static int CAPACITY = 100;

	public static void main(String[] args) {
		new ArrayList4TATestCase().runSolo();
	}


	@Override
	protected void store() throws Exception {
		ArrayList4<Integer> list = new ArrayList4<Integer>();
		for (int i = 0; i < CAPACITY; i++) {
			list.add(new Integer(i));
		}
		store(list);
	}

	@Override
	protected void configure(Configuration config) throws Exception {
		config.add(new TransparentActivationSupport());
		config.activationDepth(0);
		super.configure(config);
	}

	public void testAdd() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		for (int i = 0; i < CAPACITY; ++i) {
			list.add(new Integer(CAPACITY + i));
		}

		for (int i = 0; i < CAPACITY * 2; ++i) {
			Assert.areEqual(new Integer(i), list.get(i));
		}
	}

	public void testAdd_LObject() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				list.add(-1, new Integer(0));
			}
		});

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				list.add(CAPACITY + 1, new Integer(0));
			}
		});

		Integer i1 = new Integer(0);
		list.add(0, i1);
		// elements: 0, 0,1 - 100
		// index: 0, 1,2 - 101
		Assert.areSame(i1, list.get(0));

		for (int i = 1; i < CAPACITY + 1; ++i) {
			Assert.areEqual(new Integer(i - 1), list.get(i));
		}

		Integer i2 = new Integer(42);
		list.add(42, i2);
		// elements: 0, 0,1 - 42, 42, 43 - 100
		// index: 0, 1,2 - 43, 44, 45 - 102
		for (int i = 1; i < 42; ++i) {
			Assert.areEqual(new Integer(i - 1), list.get(i));
		}

		Assert.areSame(i2, list.get(42));
		Assert.areEqual(new Integer(41), list.get(43));

		for (int i = 44; i < CAPACITY + 2; ++i) {
			Assert.areEqual(new Integer(i - 2), list.get(i));
		}
	}

	public void testAddAll_LCollection() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		final Vector<Integer> v = new Vector<Integer>();
		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				list.addAll(-1, v);
			}
		});

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				list.addAll(CAPACITY + 1, v);
			}
		});

		for (int i = 0; i < CAPACITY; ++i) {
			v.add(new Integer(CAPACITY + i));
		}

		list.addAll(v);

		for (int i = 0; i < CAPACITY * 2; ++i) {
			Assert.areEqual(new Integer(i), list.get(i));
		}
	}

	public void testAddAll_ILCollection() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		final Vector<Integer> v = new Vector<Integer>();
		final int INDEX = 42;

		for (int i = 0; i < CAPACITY; ++i) {
			v.add(new Integer(CAPACITY + i));
		}

		list.addAll(INDEX, v);
		// elements: 0 - 41, 100 - 199, 42 - 100
		// index: 0 - 41, 42 - 141, 142 - 200
		for (int i = 0; i < INDEX; ++i) {
			Assert.areEqual(new Integer(i), list.get(i));
		}

		for (int i = INDEX, j = 0; j < CAPACITY; ++i, ++j) {
			Assert.areEqual(new Integer(CAPACITY + j), list.get(i));
		}

		for (int i = INDEX + CAPACITY; i < CAPACITY * 2; ++i) {
			Assert.areEqual(new Integer(i - CAPACITY), list.get(i));
		}

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				list.addAll(-1, v);
			}
		});
	}

	public void testClear() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		list.clear();
		Assert.areEqual(0, list.size());
	}
	
	public void testContains() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		Assert.isTrue(list.contains(new Integer(0)));
		Assert.isTrue(list.contains(new Integer(CAPACITY / 2)));
		Assert.isTrue(list.contains(new Integer(CAPACITY / 3)));
		Assert.isTrue(list.contains(new Integer(CAPACITY / 4)));

		Assert.isFalse(list.contains(new Integer(-1)));
		Assert.isFalse(list.contains(new Integer(CAPACITY)));

		// returns false because current data doesn't contain null.
		// Quotes from j.u.List spec: More formally, returns true if and only if
		// this list contains at least one element e such that (o==null ?
		// e==null : o.equals(e)).
		Assert.isFalse(list.contains(null));
	}

	public void testContainsAll() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		Vector<Integer> v = new Vector<Integer>();

		v.add(new Integer(0));
		Assert.isTrue(list.containsAll(v));

		v.add(new Integer(0));
		Assert.isTrue(list.containsAll(v));

		v.add(new Integer(CAPACITY / 2));
		Assert.isTrue(list.containsAll(v));

		v.add(new Integer(CAPACITY / 3));
		Assert.isTrue(list.containsAll(v));

		v.add(new Integer(CAPACITY / 4));
		Assert.isTrue(list.containsAll(v));

		v.add(new Integer(CAPACITY));
		Assert.isFalse(list.containsAll(v));
	}
	
	@SuppressWarnings("unchecked")
	public void testGet() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		for (int i = 0; i < CAPACITY; ++i) {
			Assert.areEqual(new Integer(i), list.get(i));
		}

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				list.get(-1);
			}
		});

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				list.get(CAPACITY);
			}
		});
	}

	public void testIndexOf() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		Assert.areEqual(0, list.indexOf(new Integer(0)));
		Assert.areEqual(CAPACITY / 2, list.indexOf(new Integer(CAPACITY / 2)));
		Assert.areEqual(CAPACITY / 3, list.indexOf(new Integer(CAPACITY / 3)));
		Assert.areEqual(CAPACITY / 4, list.indexOf(new Integer(CAPACITY / 4)));

		Assert.areEqual(-1, list.indexOf(new Integer(-1)));
		Assert.areEqual(-1, list.indexOf(new Integer(CAPACITY)));

		// returns false because current data doesn't contain null.
		// Quotes from j.u.List spec: More formally, returns the lowest index i
		// such that (o==null ? get(i)==null : o.equals(get(i))), or -1 if there
		// is no such index.
		Assert.areEqual(-1, list.indexOf(null));
	}

	public void testIsEmpty() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		Assert.isTrue(new ArrayList4<Integer>().isEmpty());
		Assert.isFalse(list.isEmpty());
		list.clear();
		Assert.isTrue(list.isEmpty());
	}

	public void testIterator() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		Iterator iter = list.iterator();
		int count = 0;
		while (iter.hasNext()) {
			Integer i = (Integer) iter.next();
			Assert.areEqual(count, i.intValue());
			++count;
		}
		Assert.areEqual(CAPACITY, count);

		list.clear();
		iter = list.iterator();
		Assert.isFalse(iter.hasNext());
	}

	public void testLastIndexOf() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		Assert.areEqual(0, list.indexOf(new Integer(0)));
		Assert.areEqual(CAPACITY / 2, list.lastIndexOf(new Integer(
				CAPACITY / 2)));
		Assert.areEqual(CAPACITY / 3, list.lastIndexOf(new Integer(
				CAPACITY / 3)));
		Assert.areEqual(CAPACITY / 4, list.lastIndexOf(new Integer(
				CAPACITY / 4)));

		Assert.areEqual(-1, list.lastIndexOf(new Integer(-1)));
		Assert.areEqual(-1, list.lastIndexOf(new Integer(CAPACITY)));

		// returns false because current data doesn't contain null.
		// Quotes from j.u.List spec: More formally, returns the lowest index i
		// such that (o==null ? get(i)==null : o.equals(get(i))), or -1 if there
		// is no such index.
		Assert.areEqual(-1, list.lastIndexOf(null));

		list.add(new Integer(0));
		list.add(new Integer(CAPACITY / 2));
		list.add(new Integer(CAPACITY / 3));
		list.add(new Integer(CAPACITY / 4));

		Assert.areEqual(CAPACITY, list.lastIndexOf(new Integer(0)));
		Assert.areEqual(CAPACITY + 1, list.lastIndexOf(new Integer(
				CAPACITY / 2)));
		Assert.areEqual(CAPACITY + 2, list.lastIndexOf(new Integer(
				CAPACITY / 3)));
		Assert.areEqual(CAPACITY + 3, list.lastIndexOf(new Integer(
				CAPACITY / 4)));

		Assert.areEqual(-1, list.lastIndexOf(new Integer(-1)));
		Assert.areEqual(-1, list.lastIndexOf(new Integer(CAPACITY)));

		// returns false because current data doesn't contain null.
		// Quotes from j.u.List spec: More formally, returns the lowest index i
		// such that (o==null ? get(i)==null : o.equals(get(i))), or -1 if there
		// is no such index.
		Assert.areEqual(-1, list.lastIndexOf(null));
	}

	public void testRemove_Object() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		list.remove(new Integer(0));
		Assert.areEqual(new Integer(1), list.get(0));

		Assert.areEqual(CAPACITY - 1, list.size());

		list.remove(new Integer(43));
		Assert.areEqual(new Integer(44), list.get(42));
		Assert.areEqual(new Integer(42), list.get(41));
		Assert.areEqual(CAPACITY - 2, list.size());

		for (int i = 0; i < CAPACITY - 2; ++i) {
			list.remove(list.get(0));
			Assert.areEqual(CAPACITY - 3 - i, list.size());
		}
		Assert.isTrue(list.isEmpty());
	}

	public void testRemove_I() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		list.remove(0);
		Assert.areEqual(new Integer(1), list.get(0));
		Assert.isFalse(list.contains(new Integer(0)));
		Assert.areEqual(CAPACITY - 1, list.size());

		list.remove(42);
		Assert.areEqual(new Integer(44), list.get(42));
		Assert.areEqual(new Integer(42), list.get(41));
		Assert.isFalse(list.contains(new Integer(43)));
		Assert.areEqual(CAPACITY - 2, list.size());

		for (int i = 0; i < CAPACITY - 2; ++i) {
			list.remove(0);
			Assert.areEqual(CAPACITY - 3 - i, list.size());
		}
		Assert.isTrue(list.isEmpty());
	}

	public void testRemoveAll() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		Vector<Integer>v = new Vector<Integer>();

		list.removeAll(v);
		Assert.areEqual(CAPACITY, list.size());

		v.add(new Integer(0));
		v.add(new Integer(42));
		list.removeAll(v);
		Assert.isFalse(list.contains(new Integer(0)));
		Assert.isFalse(list.contains(new Integer(42)));
		Assert.areEqual(CAPACITY - 2, list.size());

		v.add(new Integer(1));
		v.add(new Integer(2));
		list.removeAll(v);
		Assert.isFalse(list.contains(new Integer(1)));
		Assert.isFalse(list.contains(new Integer(2)));
		Assert.areEqual(CAPACITY - 4, list.size());

		for (int i = 0; i < CAPACITY; ++i) {
			v.add(new Integer(i));
		}
		list.removeAll(v);
		Assert.isTrue(list.isEmpty());
	}

	public void testRetainAll() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		Vector <Integer>v = new Vector<Integer>();
		v.add(new Integer(0));
		v.add(new Integer(42));

		boolean ret = list.retainAll(list);
		Assert.isFalse(ret);
		Assert.areEqual(100, list.size());
		for (int i = 0; i < CAPACITY; ++i) {
			Assert.isTrue(list.contains(new Integer(i)));
		}

		ret = list.retainAll(v);
		Assert.isTrue(ret);
		Assert.areEqual(2, list.size());
		list.contains(new Integer(0));
		list.contains(new Integer(42));

		ret = list.retainAll(v);
		Assert.isFalse(ret);
		list.contains(new Integer(0));
		list.contains(new Integer(42));
	}


	public void testSet() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		Integer element = new Integer(1);
		
		Integer previousElement = list.get(0);
		Assert.areSame(previousElement, list.set(0, element));
		Assert.areSame(element, list.get(0));

		previousElement = list.get(42);
		Assert.areSame(previousElement, list.set(42, element));
		Assert.areSame(element, list.get(42));

		for (int i = 0; i < CAPACITY; ++i) {
			element = new Integer(i);
			previousElement = list.get(i);
			Assert.areSame(previousElement, list.set(i, element));
			Assert.areSame(element, list.get(i));
		}

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				list.set(-1, new Integer(0));
			}
		});

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				list.set(CAPACITY, new Integer(0));
			}
		});
	}

	public void testSize() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		Assert.areEqual(CAPACITY, list.size());
		for (int i = 0; i < CAPACITY; ++i) {
			list.remove(0);
			Assert.areEqual(CAPACITY - 1 - i, list.size());
		}
		for (int i = 0; i < CAPACITY; ++i) {
			list.add(new Integer(i));
			Assert.areEqual(i + 1, list.size());
		}
	}

	public void testToArray() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		Object[] array = list.toArray();
		Assert.areEqual(CAPACITY, array.length);
		for(int i = 0; i < CAPACITY; ++i) {
			Integer element = (Integer) array[i];
			Assert.areEqual(new Integer(i), element);
		}
		
		list.clear();
		array = list.toArray();
		Assert.areEqual(0, array.length);
	}
	
	public void testToArray_LObject() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		Object[] array1;
		Object[] array2 = new Integer[CAPACITY];
		array1 = list.toArray(array2);
		Assert.areSame(array1, array2);
		Assert.areEqual(CAPACITY, array2.length);
		for(int i = 0; i < CAPACITY; ++i) {	
			Integer element = (Integer) array2[i];
			Assert.areEqual(new Integer(i), element);
		}
		
		list.clear();
		
		array1 = new Integer[0];
		array2 = new Integer[CAPACITY];
		array1 = list.toArray(array2);
		Assert.areSame(array1, array2);
		Assert.areEqual(CAPACITY, array1.length);
		
		array2 = new Integer[0];
		array1 = list.toArray(array2);
		Assert.areEqual(0, array1.length);
	}
	
	public void testToString() throws Exception {
		ArrayList4<Object> list = new ArrayList4<Object>();
		
		Assert.areEqual("[]",list.toString());
		
		list.add(new Integer(1));
		list.add(new Integer(2));
		Assert.areEqual("[1, 2]",list.toString());
		
		list.add(list);
		list.add(3);
		Assert.areEqual("[1, 2, (this Collection), 3]",list.toString());
	}
	
	public void testTrimToSize_EnsureCapacity() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		list.ensureCapacity(CAPACITY*2);
		Assert.areEqual(CAPACITY, list.size());
		for(int i = 0; i < CAPACITY; ++i) {
			Integer element = (Integer) list.get(i);
			Assert.areEqual(new Integer(i), element);
		}
		
		list.trimToSize();
		Assert.areEqual(CAPACITY, list.size());
		for(int i = 0; i < CAPACITY; ++i) {
			Integer element = (Integer) list.get(i);
			Assert.areEqual(new Integer(i), element);
		}
	}
	
	public void testTrimToSize_Remove() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		
		for (int i = CAPACITY-1; i >= 10 ; i--) {
			list.remove(i);
		}
		Assert.areEqual(10, list.size());
		for(int i = 0; i < 10; ++i) {
			Integer element = (Integer) list.get(i);
			Assert.areEqual(new Integer(i), element);
		}
	}
	
	public void testTrimToSize_Iterator() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		final Iterator<Integer> iterator = list.iterator();
		list.trimToSize();
		Assert.expect(ConcurrentModificationException.class, new CodeBlock(){
			public void run() throws Throwable {
				iterator.next();
			}
		});
	}
	
	public void testEnsureCapacity_Iterator() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		final Iterator<Integer> iterator = list.iterator();
		list.ensureCapacity(CAPACITY*2);
		Assert.expect(ConcurrentModificationException.class, new CodeBlock(){
			public void run() throws Throwable {
				iterator.next();
			}
		});
	}
	
	public void testClear_Iterator() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		final Iterator<Integer> iterator = list.iterator();
		list.clear();
		Assert.expect(ConcurrentModificationException.class, new CodeBlock(){
			public void run() throws Throwable {
				iterator.next();
			}
		});
	}
	
	
	@SuppressWarnings("unchecked")
	public void testClone() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		list.add(null);
		ArrayList4<Integer> cloned = (ArrayList4<Integer>)list.clone();
		for (int i = 0; i < CAPACITY; i++) {
			Assert.areSame(list.get(i), cloned.get(i));
		}
	}
	
	public void testEquals() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		Assert.isFalse(list.equals(null));
		Assert.isFalse(list.equals(new Integer(1)));
		Assert.isTrue(list.equals(list));
		Vector<Integer> v = new Vector<Integer>(list);
		Assert.isTrue(list.equals(v));
		v = new Vector<Integer>();
		Assert.isFalse(list.equals(v));
		Assert.isTrue(list.equals(list.clone()));
	}
	
	public void testIteratorNext_NoSuchElementException() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		final Iterator<Integer> iterator = list.iterator();
		Assert.expect(NoSuchElementException.class, new CodeBlock(){
			public void run() throws Throwable {
				while(true){iterator.next();}
			}
		});
	}
	
	public void testIteratorNext_ConcurrentModificationException() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		final Iterator<Integer> iterator = list.iterator();
		Assert.expect(NoSuchElementException.class, new CodeBlock(){
			public void run() throws Throwable {
				while(true){iterator.next();}
			}
		});
		list.clear();
		Assert.expect(ConcurrentModificationException.class, new CodeBlock(){
			public void run() throws Throwable {
				iterator.next();
			}
		});
		
	}
	
	public void testIteratorNext() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		final Iterator<Integer> iterator = list.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			Integer e1 = iterator.next();
			Assert.areSame(e1, list.get(i));
			i++;
		}
	}
	
	public void testIteratorRemove() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		final Iterator<Integer> iterator = list.iterator();
		int i = CAPACITY-1;
		while (iterator.hasNext()) {
			Integer e1 = iterator.next();
			Assert.areSame(e1, list.get(0));
			Assert.areEqual(new Integer(CAPACITY-1), list.get(i));
			iterator.remove();
			Assert.areEqual(i, list.size());
			i--;
		}
	}
	
	public void testRemove_IllegalStateException() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		final Iterator<Integer> iterator = list.iterator();
		Assert.expect(IllegalStateException.class, new CodeBlock(){
			public void run() throws Throwable {
				iterator.remove();
			}
		});
		
		iterator.next();
		
		Assert.expect(IllegalStateException.class, new CodeBlock(){
			public void run() throws Throwable {
				iterator.remove();
				iterator.remove();
			}
		});
	}
	
	public void testIteratorRemove_ConcurrentModificationException() throws Exception {
		final ArrayList4<Integer> list = retrieveAndAssertNullArrayList4();
		final Iterator<Integer> iterator = list.iterator();
		iterator.next();
		list.clear();
		Assert.expect(ConcurrentModificationException.class, new CodeBlock(){
			public void run() throws Throwable {
				iterator.remove();
			}
		});
	}
	@SuppressWarnings("unchecked")
	private ArrayList4<Integer> retrieveAndAssertNullArrayList4() throws Exception{
		ArrayList4<Integer> list = (ArrayList4<Integer>) retrieveOnlyInstance(ArrayList4.class);
		assertNullArrayList4(list);
		return list;
	}

	private void assertNullArrayList4(ArrayList4<Integer> list) throws Exception {
		Assert.isNull(getFieldByReflection(list, "elements"));
		Assert.areEqual(0, getFieldByReflection(list, "capacity"));
		Assert.areEqual(0, getFieldByReflection(list, "listSize"));
	}


	private Object getFieldByReflection(ArrayList4<Integer> list, String fieldName)
			throws NoSuchFieldException, IllegalAccessException {
		Field field = list.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		return field.get(list);
	}
	
	
}
