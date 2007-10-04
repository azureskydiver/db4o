/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections;

import java.util.*;

import com.db4o.collections.*;

import db4ounit.*;

public class ArrayList4Asserter {
	public static int CAPACITY = 100;
	
	public static void createList(final List<Integer> _list) throws Exception {
		for (int i = 0; i < CAPACITY; i++) {
			_list.add(new Integer(i));
		}
	}

	public static void assertAdd(final List<Integer> _list) throws Exception {
		for (int i = 0; i < CAPACITY; ++i) {
			_list.add(new Integer(CAPACITY + i));
		}

		for (int i = 0; i < CAPACITY * 2; ++i) {
			Assert.areEqual(new Integer(i), _list.get(i));
		}
	}
	
	public static void assertAdd_LObject(final List<Integer> _list) throws Exception {
		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				_list.add(-1, new Integer(0));
			}
		});

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				_list.add(CAPACITY + 1, new Integer(0));
			}
		});

		Integer i1 = new Integer(0);
		_list.add(0, i1);
		// elements: 0, 0,1 - 100
		// index: 0, 1,2 - 101
		Assert.areSame(i1, _list.get(0));

		for (int i = 1; i < CAPACITY + 1; ++i) {
			Assert.areEqual(new Integer(i - 1), _list.get(i));
		}

		int val = CAPACITY/2;
		Integer i2 = new Integer(val);
		_list.add(val, i2);
		// elements: 0, 0,1 - C/2, C/2, C+1 - C
		// index: 0, 1,2 - C/2-1, C/2, C/2+1 - C
		for (int i = 1; i < val; ++i) {
			Assert.areEqual(new Integer(i - 1), _list.get(i));
		}

		Assert.areSame(i2, _list.get(val));
		Assert.areEqual(new Integer(val/2), _list.get(val/2+1));

		for (int i = val+1 ; i < CAPACITY + 2; ++i) {
			Assert.areEqual(new Integer(i - 2), _list.get(i));
		}
	}

	public static void assertAddAll_LCollection(final List<Integer> _list) throws Exception {
		final Vector<Integer> v = new Vector<Integer>();
		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				_list.addAll(-1, v);
			}
		});

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				_list.addAll(CAPACITY + 1, v);
			}
		});

		for (int i = 0; i < CAPACITY; ++i) {
			v.add(new Integer(CAPACITY + i));
		}

		_list.addAll(v);

		for (int i = 0; i < CAPACITY * 2; ++i) {
			Assert.areEqual(new Integer(i), _list.get(i));
		}
	}

	public static void assertAddAll_ILCollection(final List<Integer> _list) throws Exception {
		final Vector<Integer> v = new Vector<Integer>();
		final int INDEX = 42;

		for (int i = 0; i < CAPACITY; ++i) {
			v.add(new Integer(CAPACITY + i));
		}

		_list.addAll(INDEX, v);
		// elements: 0 - 41, 100 - 199, 42 - 100
		// index: 0 - 41, 42 - 141, 142 - 200
		for (int i = 0; i < INDEX; ++i) {
			Assert.areEqual(new Integer(i), _list.get(i));
		}

		for (int i = INDEX, j = 0; j < CAPACITY; ++i, ++j) {
			Assert.areEqual(new Integer(CAPACITY + j), _list.get(i));
		}

		for (int i = INDEX + CAPACITY; i < CAPACITY * 2; ++i) {
			Assert.areEqual(new Integer(i - CAPACITY), _list.get(i));
		}

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				_list.addAll(-1, v);
			}
		});
	}

	public static void assertClear(final List<Integer> _list) throws Exception {
		_list.clear();
		Assert.areEqual(0, _list.size());
	}

	public static void assertContains(final List<Integer> _list) throws Exception {
		Assert.isTrue(_list.contains(new Integer(0)));
		Assert.isTrue(_list.contains(new Integer(CAPACITY / 2)));
		Assert.isTrue(_list.contains(new Integer(CAPACITY / 3)));
		Assert.isTrue(_list.contains(new Integer(CAPACITY / 4)));

		Assert.isFalse(_list.contains(new Integer(-1)));
		Assert.isFalse(_list.contains(new Integer(CAPACITY)));

		// returns false because current data doesn't contain null.
		// Quotes from j.u.List spec: More formally, returns true if and only if
		// this list contains at least one element e such that (o==null ?
		// e==null : o.equals(e)).
		Assert.isFalse(_list.contains(null));
	}

	public static void assertContainsAll(final List<Integer> _list) throws Exception {
		Vector<Integer> v = new Vector<Integer>();

		v.add(new Integer(0));
		Assert.isTrue(_list.containsAll(v));

		v.add(new Integer(0));
		Assert.isTrue(_list.containsAll(v));

		v.add(new Integer(CAPACITY / 2));
		Assert.isTrue(_list.containsAll(v));

		v.add(new Integer(CAPACITY / 3));
		Assert.isTrue(_list.containsAll(v));

		v.add(new Integer(CAPACITY / 4));
		Assert.isTrue(_list.containsAll(v));

		v.add(new Integer(CAPACITY));
		Assert.isFalse(_list.containsAll(v));
	}

	public static void assertGet(final List<Integer> _list) throws Exception {
		for (int i = 0; i < CAPACITY; ++i) {
			Assert.areEqual(new Integer(i), _list.get(i));
		}

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				_list.get(-1);
			}
		});

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				_list.get(CAPACITY);
			}
		});
	}

	public static void assertIndexOf(final List<Integer> _list) throws Exception {
		Assert.areEqual(0, _list.indexOf(new Integer(0)));
		Assert.areEqual(CAPACITY / 2, _list.indexOf(new Integer(CAPACITY / 2)));
		Assert.areEqual(CAPACITY / 3, _list.indexOf(new Integer(CAPACITY / 3)));
		Assert.areEqual(CAPACITY / 4, _list.indexOf(new Integer(CAPACITY / 4)));

		Assert.areEqual(-1, _list.indexOf(new Integer(-1)));
		Assert.areEqual(-1, _list.indexOf(new Integer(CAPACITY)));

		// returns false because current data doesn't contain null.
		// Quotes from j.u.List spec: More formally, returns the lowest index i
		// such that (o==null ? get(i)==null : o.equals(get(i))), or -1 if there
		// is no such index.
		Assert.areEqual(-1, _list.indexOf(null));
	}

	public static void assertIsEmpty(final List<Integer> _list) throws Exception {
		Assert.isTrue(new ArrayList4<Integer>().isEmpty());
		Assert.isFalse(_list.isEmpty());
		_list.clear();
		Assert.isTrue(_list.isEmpty());
	}

	public static void assertIterator(final List<Integer> _list) throws Exception {
		Iterator iter = _list.iterator();
		int count = 0;
		while (iter.hasNext()) {
			Integer i = (Integer) iter.next();
			Assert.areEqual(count, i.intValue());
			++count;
		}
		Assert.areEqual(CAPACITY, count);

		_list.clear();
		iter = _list.iterator();
		Assert.isFalse(iter.hasNext());
	}

	public static void assertLastIndexOf(final List<Integer> _list) throws Exception {
		Assert.areEqual(0, _list.indexOf(new Integer(0)));
		Assert.areEqual(CAPACITY / 2, _list.lastIndexOf(new Integer(
				CAPACITY / 2)));
		Assert.areEqual(CAPACITY / 3, _list.lastIndexOf(new Integer(
				CAPACITY / 3)));
		Assert.areEqual(CAPACITY / 4, _list.lastIndexOf(new Integer(
				CAPACITY / 4)));

		Assert.areEqual(-1, _list.lastIndexOf(new Integer(-1)));
		Assert.areEqual(-1, _list.lastIndexOf(new Integer(CAPACITY)));

		// returns false because current data doesn't contain null.
		// Quotes from j.u.List spec: More formally, returns the lowest index i
		// such that (o==null ? get(i)==null : o.equals(get(i))), or -1 if there
		// is no such index.
		Assert.areEqual(-1, _list.lastIndexOf(null));

		_list.add(new Integer(0));
		_list.add(new Integer(CAPACITY / 2));
		_list.add(new Integer(CAPACITY / 3));
		_list.add(new Integer(CAPACITY / 4));

		Assert.areEqual(CAPACITY, _list.lastIndexOf(new Integer(0)));
		Assert.areEqual(CAPACITY + 1, _list.lastIndexOf(new Integer(
				CAPACITY / 2)));
		Assert.areEqual(CAPACITY + 2, _list.lastIndexOf(new Integer(
				CAPACITY / 3)));
		Assert.areEqual(CAPACITY + 3, _list.lastIndexOf(new Integer(
				CAPACITY / 4)));

		Assert.areEqual(-1, _list.lastIndexOf(new Integer(-1)));
		Assert.areEqual(-1, _list.lastIndexOf(new Integer(CAPACITY)));

		// returns false because current data doesn't contain null.
		// Quotes from j.u.List spec: More formally, returns the lowest index i
		// such that (o==null ? get(i)==null : o.equals(get(i))), or -1 if there
		// is no such index.
		Assert.areEqual(-1, _list.lastIndexOf(null));
	}

	public static void assertRemove_Object(final List<Integer> _list) throws Exception {
		_list.remove(new Integer(0));
		Assert.areEqual(new Integer(1), _list.get(0));

		Assert.areEqual(CAPACITY - 1, _list.size());

		int val = CAPACITY/2;
		_list.remove(new Integer(val));
		Assert.areEqual(new Integer(val+1), _list.get(val-1));
		Assert.areEqual(new Integer(val+2), _list.get(val));
		Assert.areEqual(new Integer(val+3), _list.get(val+1));
		Assert.areEqual(CAPACITY - 2, _list.size());

		for (int i = 0; i < CAPACITY - 2; ++i) {
			_list.remove(_list.get(0));
			Assert.areEqual(CAPACITY - 3 - i, _list.size());
		}
		Assert.isTrue(_list.isEmpty());
	}

	public static void assertRemove_I(final List<Integer> _list) throws Exception {
		_list.remove(0);
		Assert.areEqual(new Integer(1), _list.get(0));
		Assert.isFalse(_list.contains(new Integer(0)));
		Assert.areEqual(CAPACITY - 1, _list.size());

		_list.remove(42);
		Assert.areEqual(new Integer(44), _list.get(42));
		Assert.areEqual(new Integer(42), _list.get(41));
		Assert.isFalse(_list.contains(new Integer(43)));
		Assert.areEqual(CAPACITY - 2, _list.size());

		for (int i = 0; i < CAPACITY - 2; ++i) {
			_list.remove(0);
			Assert.areEqual(CAPACITY - 3 - i, _list.size());
		}
		Assert.isTrue(_list.isEmpty());
	}

	public static void assertRemoveAll(final List<Integer> _list) throws Exception {
		Vector<Integer>v = new Vector<Integer>();

		_list.removeAll(v);
		Assert.areEqual(CAPACITY, _list.size());

		int val = CAPACITY/2;
		v.add(new Integer(0));
		v.add(new Integer(val));
		_list.removeAll(v);
		Assert.isFalse(_list.contains(new Integer(0)));
		Assert.isFalse(_list.contains(new Integer(val)));
		Assert.areEqual(CAPACITY - 2, _list.size());

		v.add(new Integer(1));
		v.add(new Integer(2));
		_list.removeAll(v);
		Assert.isFalse(_list.contains(new Integer(1)));
		Assert.isFalse(_list.contains(new Integer(2)));
		Assert.areEqual(CAPACITY - 4, _list.size());

		for (int i = 0; i < CAPACITY; ++i) {
			v.add(new Integer(i));
		}
		_list.removeAll(v);
		Assert.isTrue(_list.isEmpty());
	}

	public static void assertRetainAll(final List<Integer> _list) throws Exception {
		Vector <Integer>v = new Vector<Integer>();
		v.add(new Integer(0));
		v.add(new Integer(42));

		boolean ret = _list.retainAll(_list);
		Assert.isFalse(ret);
		Assert.areEqual(100, _list.size());
		for (int i = 0; i < CAPACITY; ++i) {
			Assert.isTrue(_list.contains(new Integer(i)));
		}

		ret = _list.retainAll(v);
		Assert.isTrue(ret);
		Assert.areEqual(2, _list.size());
		_list.contains(new Integer(0));
		_list.contains(new Integer(42));

		ret = _list.retainAll(v);
		Assert.isFalse(ret);
		_list.contains(new Integer(0));
		_list.contains(new Integer(42));
	}

	public static void assertSet(final List<Integer> _list) throws Exception {
		Integer element = new Integer(1);
		
		Integer previousElement = _list.get(0);
		Assert.areSame(previousElement, _list.set(0, element));
		Assert.areSame(element, _list.get(0));

		int val = CAPACITY/2;
		previousElement = _list.get(val);
		Assert.areSame(previousElement, _list.set(val, element));
		Assert.areSame(element, _list.get(val));

		for (int i = 0; i < CAPACITY; ++i) {
			element = new Integer(i);
			previousElement = _list.get(i);
			Assert.areSame(previousElement, _list.set(i, element));
			Assert.areSame(element, _list.get(i));
		}

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				_list.set(-1, new Integer(0));
			}
		});

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				_list.set(CAPACITY, new Integer(0));
			}
		});
	}

	public static void assertSize(final List<Integer> _list) throws Exception {
		Assert.areEqual(CAPACITY, _list.size());
		for (int i = 0; i < CAPACITY; ++i) {
			_list.remove(0);
			Assert.areEqual(CAPACITY - 1 - i, _list.size());
		}
		for (int i = 0; i < CAPACITY; ++i) {
			_list.add(new Integer(i));
			Assert.areEqual(i + 1, _list.size());
		}
	}
	
	public static void assertToArray(final List<Integer> _list) throws Exception {
		Object[] array = _list.toArray();
		Assert.areEqual(CAPACITY, array.length);
		for(int i = 0; i < CAPACITY; ++i) {
			Integer element = (Integer) array[i];
			Assert.areEqual(new Integer(i), element);
		}
		
		_list.clear();
		array = _list.toArray();
		Assert.areEqual(0, array.length);
	}
	
	public static void assertToArray_LObject(final List<Integer> _list) throws Exception {
		Object[] array1;
		Object[] array2 = new Integer[CAPACITY];
		array1 = _list.toArray(array2);
		Assert.areSame(array1, array2);
		Assert.areEqual(CAPACITY, array2.length);
		for(int i = 0; i < CAPACITY; ++i) {	
			Integer element = (Integer) array2[i];
			Assert.areEqual(new Integer(i), element);
		}
		
		_list.clear();
		
		array1 = new Integer[0];
		array2 = new Integer[CAPACITY];
		array1 = _list.toArray(array2);
		Assert.areSame(array1, array2);
		Assert.areEqual(CAPACITY, array1.length);
		
		array2 = new Integer[0];
		array1 = _list.toArray(array2);
		Assert.areEqual(0, array1.length);
	}
	
	public static void assertToString(final List<Integer> _list) throws Exception {
		ArrayList4<Object> list = new ArrayList4<Object>();
		
		Assert.areEqual("[]",list.toString());
		
		list.add(new Integer(1));
		list.add(new Integer(2));
		Assert.areEqual("[1, 2]",list.toString());
		
		list.add(list);
		list.add(3);
		Assert.areEqual("[1, 2, (this Collection), 3]",list.toString());
	}
	
	public static void assertTrimToSize_EnsureCapacity(final ArrayList4<Integer> _list) throws Exception {
		_list.ensureCapacity(CAPACITY*2);
		Assert.areEqual(CAPACITY, _list.size());
		for(int i = 0; i < CAPACITY; ++i) {
			Integer element = (Integer) _list.get(i);
			Assert.areEqual(new Integer(i), element);
		}
		
		_list.trimToSize();
		Assert.areEqual(CAPACITY, _list.size());
		for(int i = 0; i < CAPACITY; ++i) {
			Integer element = (Integer) _list.get(i);
			Assert.areEqual(new Integer(i), element);
		}
	}
	
	public static void assertTrimToSize_Remove(final List<Integer> _list) throws Exception {
		for (int i = CAPACITY-1; i >= 10 ; i--) {
			_list.remove(i);
		}
		Assert.areEqual(10, _list.size());
		for(int i = 0; i < 10; ++i) {
			Integer element = (Integer) _list.get(i);
			Assert.areEqual(new Integer(i), element);
		}
	}
	
	public static void assertTrimToSize_Iterator(final ArrayList4<Integer> _list) throws Exception {
		final Iterator<Integer> iterator = _list.iterator();
		_list.trimToSize();
		Assert.expect(ConcurrentModificationException.class, new CodeBlock(){
			public void run() throws Throwable {
				iterator.next();
			}
		});
	}
	
	public static void assertEnsureCapacity_Iterator(final ArrayList4<Integer> _list) throws Exception {
		final Iterator<Integer> iterator = _list.iterator();
		_list.ensureCapacity(CAPACITY*2);
		Assert.expect(ConcurrentModificationException.class, new CodeBlock(){
			public void run() throws Throwable {
				iterator.next();
			}
		});
	}
	
	public static void assertClear_Iterator(final ArrayList4<Integer> _list) throws Exception {
		final Iterator<Integer> iterator = _list.iterator();
		_list.clear();
		Assert.expect(ConcurrentModificationException.class, new CodeBlock(){
			public void run() throws Throwable {
				iterator.next();
			}
		});
	}
	
	
	@SuppressWarnings("unchecked")
	public static void assertClone(final ArrayList4<Integer> _list) throws Exception {
		_list.add(null);
		ArrayList4<Integer> cloned = (ArrayList4<Integer>)_list.clone();
		for (int i = 0; i < CAPACITY; i++) {
			Assert.areSame(_list.get(i), cloned.get(i));
		}
	}
	
	public static void assertEquals(final ArrayList4<Integer> _list) throws Exception {
		Assert.isFalse(_list.equals(null));
		Assert.isFalse(_list.equals(new Integer(1)));
		Assert.isTrue(_list.equals(_list));
		Vector<Integer> v = new Vector<Integer>(_list);
		Assert.isTrue(_list.equals(v));
		v = new Vector<Integer>();
		Assert.isFalse(_list.equals(v));
		Assert.isTrue(_list.equals(_list.clone()));
	}
	
	public static void assertIteratorNext_NoSuchElementException(final List<Integer> _list) throws Exception {
		final Iterator<Integer> iterator = _list.iterator();
		Assert.expect(NoSuchElementException.class, new CodeBlock(){
			public void run() throws Throwable {
				while(true){iterator.next();}
			}
		});
	}
	
	public static void assertIteratorNext_ConcurrentModificationException(final List<Integer> _list) throws Exception {
		final Iterator<Integer> iterator = _list.iterator();
		Assert.expect(NoSuchElementException.class, new CodeBlock(){
			public void run() throws Throwable {
				while(true){iterator.next();}
			}
		});
		_list.clear();
		Assert.expect(ConcurrentModificationException.class, new CodeBlock(){
			public void run() throws Throwable {
				iterator.next();
			}
		});
		
	}
	
	public static void assertIteratorNext(final List<Integer> _list) throws Exception {
		final Iterator<Integer> iterator = _list.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			Integer e1 = iterator.next();
			Assert.areSame(e1, _list.get(i));
			i++;
		}
	}
	
	public static void assertIteratorRemove(final List<Integer> _list) throws Exception {
		final Iterator<Integer> iterator = _list.iterator();
		int i = CAPACITY-1;
		while (iterator.hasNext()) {
			Integer e1 = iterator.next();
			Assert.areSame(e1, _list.get(0));
			Assert.areEqual(new Integer(CAPACITY-1), _list.get(i));
			iterator.remove();
			Assert.areEqual(i, _list.size());
			i--;
		}
	}
	
	public static void assertRemove_IllegalStateException(final List<Integer> _list) throws Exception {
		final Iterator<Integer> iterator = _list.iterator();
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
	
	public static void assertIteratorRemove_ConcurrentModificationException(final List<Integer> _list) throws Exception {
		final Iterator<Integer> iterator = _list.iterator();
		iterator.next();
		_list.clear();
		Assert.expect(ConcurrentModificationException.class, new CodeBlock(){
			public void run() throws Throwable {
				iterator.remove();
			}
		});
	}

	public static void assertSubList(List<Integer> _list) throws Exception {
		int val = CAPACITY/2;
		List<Integer> subList1 = _list.subList(val, CAPACITY);
		for (int index = 0; index < subList1.size(); index++) {
			Assert.areSame(_list.get(val+index),subList1.get(index));
		}
		_list.set(val, new Integer(1001));
		Assert.areEqual(new Integer(1001), subList1.get(0));
		
		subList1.set(1, new Integer(1001));
		Assert.areEqual(new Integer(1001), _list.get(val+1));
	}
	
	public static void assertSubList_ConcurrentModification(List<Integer> _list) throws Exception {
		int val = CAPACITY/2;
		final List<Integer> subList1 = _list.subList(val, CAPACITY);
		for (int index = 0; index < subList1.size(); index++) {
			Assert.areSame(_list.get(val+index),subList1.get(index));
		}
		_list.remove(0);
		Assert.expect(ConcurrentModificationException.class, new CodeBlock(){
			public void run() throws Throwable {
				subList1.get(0);
			}
		});
	}

	
}
