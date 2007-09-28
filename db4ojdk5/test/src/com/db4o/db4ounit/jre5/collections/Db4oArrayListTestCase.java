/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections;

import java.util.*;

import com.db4o.collections.*;

import db4ounit.*;

public class Db4oArrayListTestCase implements TestLifeCycle {

	public static void main(String[] args) {
		new TestRunner(Db4oArrayListTestCase.class).run();
	}
	
	public ArrayList <Integer> _list;

	private static int CAPACITY = 100;

	public void setUp() throws Exception {
		_list = new ArrayList<Integer>();
		// commmet the following code to use platform ArrayList
		//_list = new Db4oArrayList<Integer>();
		for (int i = 0; i < CAPACITY; i++) {
			_list.add(new Integer(i));
		}
	}

	public void tearDown() throws Exception {
		
	}
	
	public void testConstructor() throws Exception {
		Db4oArrayList<Integer> arrayList = new Db4oArrayList<Integer>();
		fill(arrayList);
		Assert.areEqual(CAPACITY, arrayList.size());
	}
	
	public void testConstructor_I_LegalArguments1() throws Exception {
		Db4oArrayList<Integer> arrayList; 
		arrayList = new Db4oArrayList<Integer>(CAPACITY);
		fill(arrayList);
		Assert.areEqual(CAPACITY, arrayList.size());
	}
	
	public void testConstructor_I_LegalArguments2() throws Exception {
		Db4oArrayList<Integer> arrayList; 
		arrayList = new Db4oArrayList<Integer>(0);
		fill(arrayList);
		Assert.areEqual(CAPACITY, arrayList.size());
	}

	public void testConstructor_I_IllegalArgumentException() throws Exception {
		Assert.expect(IllegalArgumentException.class, new CodeBlock(){
			public void run() throws Throwable {
				Db4oArrayList<Integer> arrayList = new Db4oArrayList<Integer>(-1);
			}
		});
	}
	
	public void testConstructor_LCollection_NullPointerException() throws Exception {
		Assert.expect(NullPointerException.class, new CodeBlock(){
			public void run() throws Throwable {
				Db4oArrayList<Integer> arrayList = new Db4oArrayList<Integer>(null);
			}
		});
	}
	
	public void testConstructor_LCollection() throws Exception {
		Db4oArrayList<Integer> arrayList = new Db4oArrayList<Integer>(_list);
		Assert.areEqual(_list.size(), arrayList.size());
		Assert.isTrue(Arrays.equals(_list.toArray(), arrayList.toArray()));
		
	}

	private void fill(Db4oArrayList<Integer> arrayList) {
		for (int i = 0; i < CAPACITY; i++) {
			arrayList.add(new Integer(i));
		}
	}

	public void testAdd() throws Exception {
		for (int i = 0; i < CAPACITY; ++i) {
			_list.add(new Integer(CAPACITY + i));
		}

		for (int i = 0; i < CAPACITY * 2; ++i) {
			Assert.areEqual(new Integer(i), _list.get(i));
		}
	}

	public void testAdd_LObject() throws Exception {
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

		Integer i2 = new Integer(42);
		_list.add(42, i2);
		// elements: 0, 0,1 - 42, 42, 43 - 100
		// index: 0, 1,2 - 43, 44, 45 - 102
		for (int i = 1; i < 42; ++i) {
			Assert.areEqual(new Integer(i - 1), _list.get(i));
		}

		Assert.areSame(i2, _list.get(42));
		Assert.areEqual(new Integer(41), _list.get(43));

		for (int i = 44; i < CAPACITY + 2; ++i) {
			Assert.areEqual(new Integer(i - 2), _list.get(i));
		}
	}

	public void testAddAll_LCollection() throws Exception {
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

	public void testAddAll_ILCollection() throws Exception {
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

	public void testClear() throws Exception {
		_list.clear();
		Assert.areEqual(0, _list.size());
	}

	public void testContains() throws Exception {
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

	public void testContainsAll() throws Exception {
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

	public void testGet() throws Exception {
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

	public void testIndexOf() throws Exception {
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

	public void testIsEmpty() throws Exception {
		Assert.isTrue(new Db4oArrayList<Integer>().isEmpty());
		Assert.isFalse(_list.isEmpty());
		_list.clear();
		Assert.isTrue(_list.isEmpty());
	}

	public void testIterator() throws Exception {
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

	public void testLastIndexOf() throws Exception {
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

	public void testRemove_Object() throws Exception {
		_list.remove(new Integer(0));
		Assert.areEqual(new Integer(1), _list.get(0));

		Assert.areEqual(CAPACITY - 1, _list.size());

		_list.remove(new Integer(43));
		Assert.areEqual(new Integer(44), _list.get(42));
		Assert.areEqual(new Integer(42), _list.get(41));
		Assert.areEqual(CAPACITY - 2, _list.size());

		for (int i = 0; i < CAPACITY - 2; ++i) {
			_list.remove(_list.get(0));
			Assert.areEqual(CAPACITY - 3 - i, _list.size());
		}
		Assert.isTrue(_list.isEmpty());
	}

	public void testRemove_I() throws Exception {
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

	public void testRemoveAll() throws Exception {
		Vector<Integer>v = new Vector<Integer>();

		_list.removeAll(v);
		Assert.areEqual(CAPACITY, _list.size());

		v.add(new Integer(0));
		v.add(new Integer(42));
		_list.removeAll(v);
		Assert.isFalse(_list.contains(new Integer(0)));
		Assert.isFalse(_list.contains(new Integer(42)));
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

	public void testRetainAll() throws Exception {
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

	public void testSet() throws Exception {
		Integer element = new Integer(1);
		
		Integer previousElement = _list.get(0);
		Assert.areSame(previousElement, _list.set(0, element));
		Assert.areSame(element, _list.get(0));

		previousElement = _list.get(42);
		Assert.areSame(previousElement, _list.set(42, element));
		Assert.areSame(element, _list.get(42));

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

	public void testSize() throws Exception {
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
	
	public void testToArray() throws Exception {
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
	
	public void testToArray_LObject() throws Exception {
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
	
	public void testToString() throws Exception {
		Db4oArrayList<Object> list = new Db4oArrayList<Object>();
		
		Assert.areEqual("[]",list.toString());
		
		list.add(new Integer(1));
		list.add(new Integer(2));
		Assert.areEqual("[1, 2]",list.toString());
		
		list.add(list);
		list.add(3);
		Assert.areEqual("[1, 2, (this Collection), 3]",list.toString());
	}
	
	public void testTrimToSize_EnsureCapacity() throws Exception {
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
	
	public void testTrimToSize_Remove() throws Exception {
		for (int i = CAPACITY-1; i >= 10 ; i--) {
			_list.remove(i);
		}
		Assert.areEqual(10, _list.size());
		for(int i = 0; i < 10; ++i) {
			Integer element = (Integer) _list.get(i);
			Assert.areEqual(new Integer(i), element);
		}
	}
	
	public void testTrimToSize_Iterator() throws Exception {
		final Iterator<Integer> iterator = _list.iterator();
		_list.trimToSize();
		Assert.expect(ConcurrentModificationException.class, new CodeBlock(){
			public void run() throws Throwable {
				iterator.next();
			}
		});
	}
	
	public void testEnsureCapacity_Iterator() throws Exception {
		final Iterator<Integer> iterator = _list.iterator();
		_list.ensureCapacity(CAPACITY*2);
		Assert.expect(ConcurrentModificationException.class, new CodeBlock(){
			public void run() throws Throwable {
				iterator.next();
			}
		});
	}
	
	public void testClear_Iterator() throws Exception {
		final Iterator<Integer> iterator = _list.iterator();
		_list.clear();
		Assert.expect(ConcurrentModificationException.class, new CodeBlock(){
			public void run() throws Throwable {
				iterator.next();
			}
		});
	}
	
	
	@SuppressWarnings("unchecked")
	public void testClone() throws Exception {
		_list.add(null);
		ArrayList<Integer> cloned = (ArrayList<Integer>)_list.clone();
		for (int i = 0; i < CAPACITY; i++) {
			Assert.areSame(_list.get(i), cloned.get(i));
		}
	}
	
	public void testEquals() throws Exception {
		Assert.isFalse(_list.equals(null));
		Assert.isFalse(_list.equals(new Integer(1)));
		Assert.isTrue(_list.equals(_list));
		Vector<Integer> v = new Vector<Integer>(_list);
		Assert.isTrue(_list.equals(v));
		v = new Vector<Integer>();
		Assert.isFalse(_list.equals(v));
		Assert.isTrue(_list.equals(_list.clone()));
	}
	
	public void testIteratorNext_NoSuchElementException() throws Exception {
		final Iterator<Integer> iterator = _list.iterator();
		Assert.expect(NoSuchElementException.class, new CodeBlock(){
			public void run() throws Throwable {
				while(true){iterator.next();}
			}
		});
	}
	
	public void testIteratorNext_ConcurrentModificationException() throws Exception {
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
	
	public void testIteratorNext() throws Exception {
		final Iterator<Integer> iterator = _list.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			Integer e1 = iterator.next();
			Assert.areSame(e1, _list.get(i));
			i++;
		}
	}
	
	public void testIteratorRemove() throws Exception {
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
	
	public void testRemove_IllegalStateException() throws Exception {
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
	
	public void testIteratorRemove_ConcurrentModificationException() throws Exception {
		final Iterator<Integer> iterator = _list.iterator();
		iterator.next();
		_list.clear();
		Assert.expect(ConcurrentModificationException.class, new CodeBlock(){
			public void run() throws Throwable {
				iterator.remove();
			}
		});
	}
	
	public void testSubList() throws Exception {
		
	}
	
	
}
