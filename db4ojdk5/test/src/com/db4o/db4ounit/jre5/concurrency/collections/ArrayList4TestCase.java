/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.concurrency.collections;

import com.db4o.collections.*;
import com.db4o.config.*;
import com.db4o.db4ounit.jre5.collections.*;
import com.db4o.ext.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * @exclude
 */
public class ArrayList4TestCase extends Db4oConcurrenyTestCase {
	public static void main(String[] args) {
		new ArrayList4TestCase().runEmbeddedConcurrency();
	}

	protected void store() throws Exception {
		ArrayList4<Integer> list = new ArrayList4<Integer>();
		ListAsserter.createList(list);
		store(list);
	}

	@Override
	protected void configure(Configuration config) throws Exception {
		config.add(new TransparentActivationSupport());
		super.configure(config);
	}
	
	public void conc(ExtObjectContainer oc) throws Exception {
		retrieveAndAssertNullArrayList4(oc);
	}
	
	public void concAdd(ExtObjectContainer oc, int seq) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4(oc);
		ListAsserter.assertAdd(list);
		markTaskDone(seq, true);
		waitForAllTasksDone();
		oc.store(list);
	}
	
	public void checkAdd(ExtObjectContainer oc) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4(oc);
		ListAsserter.checkAdd(list);
	}
	
	public void concAdd_LObject(ExtObjectContainer oc, int seq) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4(oc);
		ListAsserter.assertAdd_LObject(list);
		markTaskDone(seq, true);
		waitForAllTasksDone();
		oc.store(list);
	}

	public void checkAdd_LObject(ExtObjectContainer oc) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4(oc);
		ListAsserter.checkAdd_LObject(list);
	}
	
	public void concAddAll_LCollection(ExtObjectContainer oc, int seq) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4(oc);
		ListAsserter.assertAddAll_LCollection(list);
		markTaskDone(seq, true);
		waitForAllTasksDone();
		oc.store(list);
	}

	public void checkAddAll_LCollection(ExtObjectContainer oc) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4(oc);
		ListAsserter.checkAddAll_LCollection(list);
	}
	
	public void concClear(ExtObjectContainer oc, int seq) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4(oc);
		ListAsserter.assertClear(list);
		markTaskDone(seq, true);
		waitForAllTasksDone();
		oc.store(list);
	}
	
	public void checkClear(ExtObjectContainer oc) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4(oc);
		ListAsserter.checkClear(list);
	}

	public void concContains(ExtObjectContainer oc) throws Exception {
		ListAsserter.assertContains(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concContainsAll(ExtObjectContainer oc) throws Exception {
		ListAsserter.assertContainsAll(retrieveAndAssertNullArrayList4(oc));
	}

	public void concIndexOf(ExtObjectContainer oc) throws Exception {
		ListAsserter.assertIndexOf(retrieveAndAssertNullArrayList4(oc));
	}

	public void concIsEmpty(ExtObjectContainer oc) throws Exception {
		ListAsserter.assertIsEmpty(retrieveAndAssertNullArrayList4(oc));
		Assert.isTrue(new ArrayList4<Integer>().isEmpty());
	}

	public void concIterator(ExtObjectContainer oc) throws Exception {
		ListAsserter.assertIterator(retrieveAndAssertNullArrayList4(oc));
	}

	public void concLastIndexOf(ExtObjectContainer oc) throws Exception {
		ListAsserter.assertLastIndexOf(retrieveAndAssertNullArrayList4(oc));
	}

	public void concRemove_LObject(ExtObjectContainer oc, int seq) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4(oc);
		ListAsserter.assertRemove_LObject(list);
		markTaskDone(seq, true);
		waitForAllTasksDone();
		oc.store(list);
	}
	
	public void checkRemove_LObject(ExtObjectContainer oc) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4(oc);
		ListAsserter.checkRemove_LObject(list);
	}
	

	public void concRemoveAll(ExtObjectContainer oc, int seq) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4(oc);
		ListAsserter.assertRemoveAll(list);
		markTaskDone(seq, true);
		waitForAllTasksDone();
		oc.store(list);
	}
	
	public void checkRemoveAll(ExtObjectContainer oc) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4(oc);
		ListAsserter.checkRemoveAll(list);
	}

	public void concSet(ExtObjectContainer oc) throws Exception {
		ListAsserter.assertSet(retrieveAndAssertNullArrayList4(oc));
	}

	public void concSize(ExtObjectContainer oc) throws Exception {
		ListAsserter.assertSize(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concToArray(ExtObjectContainer oc) throws Exception {
		ListAsserter.assertToArray(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concToArray_LObject(ExtObjectContainer oc) throws Exception {
		ListAsserter.assertToArray_LObject(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concToString(ExtObjectContainer oc) throws Exception {
		ListAsserter.assertToString(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concTrimToSize_EnsureCapacity(ExtObjectContainer oc, int seq) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4(oc);
		ArrayList4Asserter.assertTrimToSize_EnsureCapacity(list);
		markTaskDone(seq, true);
		waitForAllTasksDone();
		oc.store(list);
	}
	
	public void checkTrimToSize_EnsureCapacity(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.checkTrimToSize_EnsureCapacity(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concExtOTrimToSize_Remove(ExtObjectContainer oc) throws Exception {
		ListAsserter.assertTrimToSize_Remove(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concTrimToSize_Iterator(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.assertTrimToSize_Iterator(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concEnsureCapacity_Iterator(ExtObjectContainer oc) throws Exception {
		ArrayList4Asserter.assertEnsureCapacity_Iterator(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concClear_Iterator(ExtObjectContainer oc) throws Exception {
		ListAsserter.assertClear_Iterator(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concClone(ExtObjectContainer oc) throws Exception {
		ListAsserter.assertClone(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concEquals(ExtObjectContainer oc) throws Exception {
		ListAsserter.assertEquals(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concIteratorNext_NoSuchElementException(ExtObjectContainer oc) throws Exception {
		ListAsserter.assertIteratorNext_NoSuchElementException(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concIteratorNext_ConcurrentModificationException(ExtObjectContainer oc) throws Exception {
		ListAsserter.assertIteratorNext_ConcurrentModificationException(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concIteratorNext(ExtObjectContainer oc) throws Exception {
		ListAsserter.assertIteratorNext(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concIteratorRemove(ExtObjectContainer oc) throws Exception {
		ListAsserter.assertIteratorRemove(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concRemove_IllegalStateException(ExtObjectContainer oc) throws Exception {
		ListAsserter.assertRemove_IllegalStateException(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concIteratorRemove_ConcurrentModificationException(ExtObjectContainer oc) throws Exception {
		ListAsserter.assertIteratorRemove_ConcurrentModificationException(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concSubList(ExtObjectContainer oc) throws Exception {
		ListAsserter.assertSubList(retrieveAndAssertNullArrayList4(oc));
	}
	
	public void concSubList_ConcurrentModification(ExtObjectContainer oc) throws Exception {
		ListAsserter.assertSubList_ConcurrentModification(retrieveAndAssertNullArrayList4(oc));
	}
	
	private ArrayList4<Integer> retrieveAndAssertNullArrayList4(ExtObjectContainer oc) throws Exception{
		return CollectionsUtil.retrieveAndAssertNullArrayList4(oc, reflector());
	}
}
