/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections;

import com.db4o.collections.*;

import db4ounit.*;


/**
 * @exclude
 */
public class ArrayList4TATestCase extends ArrayList4TATestCaseBase {

	public static void main(String[] args) {
		new ArrayList4TATestCase().runAll();
	}

	public void testAdd() throws Exception {
		ListAsserter.assertAdd(retrieveAndAssertNullArrayList4());
	}
	
	public void testAdd_LObject() throws Exception {
		ListAsserter.assertAdd_LObject(retrieveAndAssertNullArrayList4());
	}

	public void testAddAll_LCollection() throws Exception {
		ListAsserter.assertAddAll_LCollection(retrieveAndAssertNullArrayList4());
	}

	public void testClear() throws Exception {
		ListAsserter.assertClear(retrieveAndAssertNullArrayList4());
	}

	public void testContains() throws Exception {
		ListAsserter.assertContains(retrieveAndAssertNullArrayList4());
	}

	public void testContainsAll() throws Exception {
		ListAsserter.assertContainsAll(retrieveAndAssertNullArrayList4());
	}

	public void testIndexOf() throws Exception {
		ListAsserter.assertIndexOf(retrieveAndAssertNullArrayList4());
	}

	public void testIsEmpty() throws Exception {
		ListAsserter.assertIsEmpty(retrieveAndAssertNullArrayList4());
		Assert.isTrue(new ArrayList4<Integer>().isEmpty());
	}

	public void testIterator() throws Exception {
		ListAsserter.assertIterator(retrieveAndAssertNullArrayList4());
	}

	public void testLastIndexOf() throws Exception {
		ListAsserter.assertLastIndexOf(retrieveAndAssertNullArrayList4());
	}

	public void testRemove_LObject() throws Exception {
		ListAsserter.assertRemove_LObject(retrieveAndAssertNullArrayList4());
	}

	public void testRemoveAll() throws Exception {
		ListAsserter.assertRemoveAll(retrieveAndAssertNullArrayList4());
	}

	public void testSet() throws Exception {
		ListAsserter.assertSet(retrieveAndAssertNullArrayList4());
	}

	public void testSize() throws Exception {
		ListAsserter.assertSize(retrieveAndAssertNullArrayList4());
	}
	
	public void testToArray() throws Exception {
		ListAsserter.assertToArray(retrieveAndAssertNullArrayList4());
	}
	
	public void testToArray_LObject() throws Exception {
		ListAsserter.assertToArray_LObject(retrieveAndAssertNullArrayList4());
	}
	
	public void testToString() throws Exception {
		ListAsserter.assertToString(retrieveAndAssertNullArrayList4());
	}
	
	public void testTrimToSize_EnsureCapacity() throws Exception {
		ArrayList4Asserter.assertTrimToSize_EnsureCapacity(retrieveAndAssertNullArrayList4());
	}
	
	public void testTrimToSize_Remove() throws Exception {
		ListAsserter.assertTrimToSize_Remove(retrieveAndAssertNullArrayList4());
	}
	
	public void testTrimToSize_Iterator() throws Exception {
		ArrayList4Asserter.assertTrimToSize_Iterator(retrieveAndAssertNullArrayList4());
	}
	
	public void testEnsureCapacity_Iterator() throws Exception {
		ArrayList4Asserter.assertEnsureCapacity_Iterator(retrieveAndAssertNullArrayList4());
	}
	
	public void testClear_Iterator() throws Exception {
		ListAsserter.assertClear_Iterator(retrieveAndAssertNullArrayList4());
	}
	
	public void testClone() throws Exception {
		ListAsserter.assertClone(retrieveAndAssertNullArrayList4());
	}
	
	public void testEquals() throws Exception {
		ListAsserter.assertEquals(retrieveAndAssertNullArrayList4());
	}
	
	public void testIteratorNext_NoSuchElementException() throws Exception {
		ListAsserter.assertIteratorNext_NoSuchElementException(retrieveAndAssertNullArrayList4());
	}
	
	public void testIteratorNext_ConcurrentModificationException() throws Exception {
		ListAsserter.assertIteratorNext_ConcurrentModificationException(retrieveAndAssertNullArrayList4());
	}
	
	public void testIteratorNext() throws Exception {
		ListAsserter.assertIteratorNext(retrieveAndAssertNullArrayList4());
	}
	
	public void testIteratorRemove() throws Exception {
		ListAsserter.assertIteratorRemove(retrieveAndAssertNullArrayList4());
	}
	
	public void testRemove_IllegalStateException() throws Exception {
		ListAsserter.assertRemove_IllegalStateException(retrieveAndAssertNullArrayList4());
	}
	
	public void testIteratorRemove_ConcurrentModificationException() throws Exception {
		ListAsserter.assertIteratorRemove_ConcurrentModificationException(retrieveAndAssertNullArrayList4());
	}
	
	public void testSubList() throws Exception {
		ListAsserter.assertSubList(retrieveAndAssertNullArrayList4());
	}
	
	public void testSubList_ConcurrentModification() throws Exception {
		ListAsserter.assertSubList_ConcurrentModification(retrieveAndAssertNullArrayList4());
	}
	public void testSubList_Clear() throws Exception {
		ListAsserter.assertSubList_Clear(retrieveAndAssertNullArrayList4());
	}
	
	public void testSubList_Clear2() throws Exception {
		ListAsserter.assertSubList_Clear2(retrieveAndAssertNullArrayList4());
	}

}
