package com.db4o.db4ounit.jre5.collections;

import java.util.*;

import com.db4o.collections.*;

import db4ounit.*;

public class SubArrayList4TestCase implements TestLifeCycle {

	List<Integer> _subList;
	
	public void setUp() throws Exception {
		ListAsserter.CAPACITY = 100;
		ArrayList4<Integer> list = new ArrayList4<Integer>();
		ListAsserter.createList(list);
		_subList = list.subList(0, 10);
		ListAsserter.CAPACITY = 10;
	}
	
	public void tearDown() throws Exception {
		ListAsserter.CAPACITY = 100;
	}
	
	public void testAdd() throws Exception {
		ListAsserter.assertAdd(_subList);
	}

	public void testAdd_LObject() throws Exception {
		ListAsserter.assertAdd_LObject(_subList);
	}

	public void testAddAll_LCollection() throws Exception {
		ListAsserter.assertAddAll_LCollection(_subList);
	}

	public void testClear() throws Exception {
		ListAsserter.assertClear(_subList);
	}

	public void testContains() throws Exception {
		ListAsserter.assertContains(_subList);
	}

	public void testContainsAll() throws Exception {
		ListAsserter.assertContainsAll(_subList);
	}

	public void testIndexOf() throws Exception {
		ListAsserter.assertIndexOf(_subList);
	}

	public void testIsEmpty() throws Exception {
		ListAsserter.assertIsEmpty(_subList);
	}

	public void testIterator() throws Exception {
		ListAsserter.assertIterator(_subList);
	}

	public void testLastIndexOf() throws Exception {
		ListAsserter.assertLastIndexOf(_subList);
	}

	public void testRemove_LObject() throws Exception {
		ListAsserter.assertRemove_LObject(_subList);
	}

	public void testRemoveAll() throws Exception {
		ListAsserter.assertRemoveAll(_subList);
	}

	public void testSet() throws Exception {
		ListAsserter.assertSet(_subList);
	}

	public void testSize() throws Exception {
		ListAsserter.assertSize(_subList);
	}
	
	public void testToArray() throws Exception {
		ListAsserter.assertToArray(_subList);
	}
	
	public void testToArray_LObject() throws Exception {
		ListAsserter.assertToArray_LObject(_subList);
	}
	
	public void testToString() throws Exception {
		ListAsserter.assertToString(_subList);
	}
	
	public void testTrimToSize_Remove() throws Exception {
		ListAsserter.assertTrimToSize_Remove(_subList);
	}
	
	public void testIteratorNext_NoSuchElementException() throws Exception {
		ListAsserter.assertIteratorNext_NoSuchElementException(_subList);
	}
	
	public void testIteratorNext_ConcurrentModificationException() throws Exception {
		ListAsserter.assertIteratorNext_ConcurrentModificationException(_subList);
	}
	
	public void testIteratorNext() throws Exception {
		ListAsserter.assertIteratorNext(_subList);
	}
	
	public void testIteratorRemove() throws Exception {
		ListAsserter.assertIteratorRemove(_subList);
	}
	
	public void testRemove_IllegalStateException() throws Exception {
		ListAsserter.assertRemove_IllegalStateException(_subList);
	}
	
	public void testIteratorRemove_ConcurrentModificationException() throws Exception {
		ListAsserter.assertIteratorRemove_ConcurrentModificationException(_subList);
	}
	
	public void testSubList() throws Exception {
		ListAsserter.assertSubList(_subList);
	}
	
	public void testSubList_ConcurrentModification() throws Exception {
		ListAsserter.assertSubList_ConcurrentModification(_subList);
	}	

}
